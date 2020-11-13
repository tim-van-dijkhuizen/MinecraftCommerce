package nl.timvandijkhuizen.commerce.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.effects.OrderEffectDefault;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.commerce.events.RegisterOrderEffectsEvent;
import nl.timvandijkhuizen.commerce.events.RegisterOrderVariablesEvent;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.variables.VariableFields;
import nl.timvandijkhuizen.commerce.variables.VariablePlayerUniqueId;
import nl.timvandijkhuizen.commerce.variables.VariablePlayerUsername;
import nl.timvandijkhuizen.commerce.variables.VariableQuantity;
import nl.timvandijkhuizen.commerce.variables.VariableUniqueId;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataAction;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class OrderService extends BaseService {

    public static final Pattern VARIABLE_FORMAT = Pattern.compile("\\{(.*?)\\}");
    
    private Set<OrderVariable> orderVariables;
    private Set<OrderEffect> orderEffects;

    @Override
    public String getHandle() {
        return "orders";
    }

    @Override
    public void init() throws Throwable {
        RegisterOrderVariablesEvent variableEvent = new RegisterOrderVariablesEvent();
        RegisterOrderEffectsEvent effectEvent = new RegisterOrderEffectsEvent();

        // Add core variables
        variableEvent.addVariable(new VariableUniqueId());
        variableEvent.addVariable(new VariablePlayerUsername());
        variableEvent.addVariable(new VariablePlayerUniqueId());
        variableEvent.addVariable(new VariableQuantity());
        variableEvent.addVariable(new VariableFields());

        // Add core effects
        effectEvent.addEffect(new OrderEffectDefault());

        // Register custom variables and effects
        Bukkit.getServer().getPluginManager().callEvent(variableEvent);
        Bukkit.getServer().getPluginManager().callEvent(effectEvent);

        orderVariables = variableEvent.getVariables();
        orderEffects = effectEvent.getEffects();
    }

    /**
     * Returns the cart of the specified user.
     * 
     * @param player
     * @return
     */
    public void getCart(Player player, Consumer<Order> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> {
            Order cart = storage.getCart(player.getUniqueId());

            // Create cart if we didn't find one
            if (cart == null) {
                return createCart(player);
            }

            return cart;
        }, callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load cart: " + error.getMessage(), error);
        });
    }

    /**
     * Creates a new cart for the specified player.
     * 
     * @param player
     * @return
     */
    public Order createCart(Player player) {
        UserService userService = Commerce.getInstance().getService("users");
        UserPreferences preferences = userService.getPreferences(player);
        StoreCurrency currency = preferences.getOptionValue("currency");

        return new Order(UUID.randomUUID(), player.getUniqueId(), player.getName(), currency);
    }

    /**
     * Returns all orders.
     * 
     * @param callback
     */
    public void getOrders(Consumer<Set<Order>> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getOrders(), callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load orders: " + error.getMessage(), error);
        });
    }

    /**
     * Returns all orders that belong to a player.
     * 
     * @param playerUniqueId
     * @param callback
     */
    public void getOrdersByPlayer(UUID playerUniqueId, Consumer<Set<Order>> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getOrdersByPlayer(playerUniqueId), callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load orders: " + error.getMessage(), error);
        });
    }

    /**
     * Saves an order.
     * 
     * @param order
     * @param callback
     */
    public void saveOrder(Order order, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();
        boolean isNew = order.getId() == null;

        // Validate the model
        if (!order.isValid()) {
            callback.accept(false);
            return;
        }
        
        // Update player name
        Player player = Bukkit.getPlayer(order.getPlayerUniqueId());
        
        if(player != null) {
            order.updatePlayerName(player.getName());
        }

        // Create or edit order
        ThreadHelper.executeAsync(() -> {
            DataList<LineItem> lineItems = order.getLineItems();

            if (isNew) {
                storage.createOrder(order);
            } else {
                storage.updateOrder(order);
            }

            // Set order id and remove if quantity <= 0
            for (LineItem lineItem : lineItems) {
                lineItem.setOrderId(order.getId());

                if (lineItem.getQuantity() <= 0) {
                    order.getLineItems().remove(lineItem);
                }
                
                // Update snapshot
                Integer productId = lineItem.getProductId();
                
                if(productId != null) {
                    Product product = storage.getProductById(productId);
                    lineItem.updateProduct(product);
                }
            }

            // Update line items
            for (LineItem lineItem : lineItems.getByAction(DataAction.CREATE)) {
                storage.createLineItem(lineItem);
            }

            for (LineItem lineItem : lineItems.getByAction(DataAction.UPDATE)) {
                storage.updateLineItem(lineItem);
            }

            for (LineItem lineItem : lineItems.getByAction(DataAction.DELETE)) {
                storage.deleteLineItem(lineItem);
            }

            // Clear pending
            lineItems.clearPending();
        }, () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to create/update order: " + error.getMessage(), error);
        });
    }

    /**
     * Completes an order. Returns true if the order was
     * completed and false if the transaction was saved
     * but there is still an outstanding debt.
     * 
     * @param order
     * @param transaction
     * @return
     * @throws Throwable
     */
    public boolean completeOrder(Order order, Transaction transaction) throws Throwable {
        StorageType storage = Commerce.getInstance().getStorage();

        // Save transaction
        storage.createTransaction(transaction);
        
        // Complete order if they've paid enough
        StoreCurrency transactionCurrency = transaction.getCurrency();
        StoreCurrency baseCurrency = ShopHelper.getBaseCurrency();
        float newAmount = ShopHelper.convertPrice(transaction.getAmount(), transactionCurrency, baseCurrency);
        float newPaid = order.getAmountPaid() + newAmount;
        
        ConsoleHelper.printDebug("COMPLETE ORDER 1 -> " + newPaid + " / " + order.getTotal());
        
        if(order.getTotal() <= newPaid) {
            order.setCompleted(true);
            storage.updateOrder(order);
            
            // Execute commands & play effect
            ThreadHelper.execute(() -> {
                YamlConfig config = Commerce.getInstance().getConfig();

                // Perform product commands
                // =============================================
                
                for (LineItem lineItem : order.getLineItems()) {
                    ProductSnapshot product = lineItem.getProduct();
                    List<String> commands = product.getCommands();

                    for (String rawCommand : commands) {
                        String command = replaceVariables(rawCommand, order, lineItem);

                        for(int i = 0; i < lineItem.getQuantity(); i++) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            ConsoleHelper.printInfo("Performed command for order with id " + order.getId() + ": " + command + "(" + (i+1) + "/" + lineItem.getQuantity() + ")");
                        }
                    }
                }

                // Play effect & show title
                // =============================================
                
                Player player = Bukkit.getPlayer(order.getPlayerUniqueId());
                OrderEffect effect = config.getOptionValue("general.completeEffect");

                if (player != null) {
                    effect.playEffect(player, order);
                }
            });
            
            ConsoleHelper.printDebug("COMPLETE ORDER 1 -> COMPLETED");
            
            return true;
        }
        
        ConsoleHelper.printDebug("COMPLETE ORDER 1 -> PARTIAL");

        return false;
    }

    /**
     * Deletes a order.
     * 
     * @param order
     * @param callback
     */
    public void deleteOrder(Order order, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        // Delete order
        ThreadHelper.executeAsync(() -> storage.deleteOrder(order), () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete order: " + error.getMessage(), error);
        });
    }

    /**
     * Replaces all variable placeholder with the variable values and returns
     * the parsed string.
     * 
     * @param value
     * @param order
     * @return
     */
    public String replaceVariables(String value, Order order, LineItem lineItem) {
        Matcher matcher = VARIABLE_FORMAT.matcher(value);
        
        // Find all variables
        while (matcher.find()) {
            String[] match = matcher.group(1).split(":");
            String key = match[0];
            String property = match.length == 2 ? match[1] : null;
            
            // Find variable and replace with value
            OrderVariable variable = orderVariables.stream()
                .filter(i -> i.getKey().equals(key))
                .findFirst()
                .orElse(null);
            
            if(variable != null) {
                String replace = "{" + key + (property != null ? (":" + property) : "") + "}";
                String[] properties = variable.getProperties();
                
                // Error if we're missing the required property
                if(properties.length > 0 && property == null) {
                    ConsoleHelper.printError("Failed to parse variable, missing required property.");
                    continue;
                }
                
                try {
                    value = value.replace(replace, variable.getValue(order, lineItem, property));
                } catch(Throwable e) {
                    ConsoleHelper.printError("Failed to parse variable: " + replace, e);
                }
            }
        }
        
        return value;
    }

    /**
     * Returns all available order variables.
     * 
     * @return
     */
    public Collection<OrderVariable> getOrderVariables() {
        return Collections.unmodifiableSet(orderVariables);
    }

    /**
     * Returns all available order effects.
     * 
     * @return
     */
    public Collection<OrderEffect> getOrderEffects() {
        return Collections.unmodifiableSet(orderEffects);
    }

}
