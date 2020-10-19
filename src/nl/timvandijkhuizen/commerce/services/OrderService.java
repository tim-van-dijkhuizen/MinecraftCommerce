package nl.timvandijkhuizen.commerce.services;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.effects.EffectFirework;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.events.RegisterOrderEffectsEvent;
import nl.timvandijkhuizen.commerce.events.RegisterOrderVariablesEvent;
import nl.timvandijkhuizen.commerce.variables.VariablePlayerUniqueId;
import nl.timvandijkhuizen.commerce.variables.VariablePlayerUsername;
import nl.timvandijkhuizen.commerce.variables.VariableUniqueId;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataAction;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class OrderService extends BaseService {

    private Set<OrderVariable> orderVariables;
    private Set<OrderEffect> orderEffects;
    
    @Override
    public String getHandle() {
        return "orders";
    }
    
    @Override
    public void init() throws Exception {
        RegisterOrderVariablesEvent variableEvent = new RegisterOrderVariablesEvent();
        RegisterOrderEffectsEvent effectEvent = new RegisterOrderEffectsEvent();

        // Add core variables
        variableEvent.addVariable(new VariableUniqueId());
        variableEvent.addVariable(new VariablePlayerUsername());
        variableEvent.addVariable(new VariablePlayerUniqueId());
        
        // Add core effects
        effectEvent.addEffect(new EffectFirework());
        
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
            if(cart == null) {
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
        ConfigOption<StoreCurrency> optionCurrency = preferences.getOption("currency");
        StoreCurrency currency = optionCurrency.getValue(preferences);

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
        
        // Clear payment URL if order changed
        if(!order.updatePaymentUrl()) {
            order.setPaymentUrl(null);
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
                
                if(lineItem.getQuantity() <= 0) {
                    order.getLineItems().remove(lineItem);
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
     * Completes an order.
     * 
     * @param order
     * @return
     */
    public boolean completeOrder(Order order) {
        StorageType storage = Commerce.getInstance().getStorage();
        
        try {
            storage.completeOrder(order);
            
            // Perform commands and play effect
            ThreadHelper.execute(() -> {
            	YamlConfig config = Commerce.getInstance().getConfig();
            	
            	// Perform product commands
            	// =============================================
                for(LineItem lineItem : order.getLineItems()) {
                	ProductSnapshot product = lineItem.getProduct();
                	List<String> commands = product.getCommands();
                	
                	for(String rawCommand : commands) {
                		String command = replaceVariables(rawCommand, order);
                		
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        ConsoleHelper.printInfo("Performed command for order with id " + order.getId() + ": " + command);
                	}
                }
                  
                // Play effect & show title
                // =============================================
                Player player = Bukkit.getPlayer(order.getPlayerUniqueId());
                
                ConfigOption<OrderEffect> optionEffect = config.getOption("general.completeEffect");
                ConfigOption<String> optionTitle = config.getOption("general.completeTitle");
                ConfigOption<String> optionSubtitle = config.getOption("general.completeSubtitle");
                
                OrderEffect effect = optionEffect.getValue(config);
                String title = replaceVariables(optionTitle.getValue(config), order);
                String subtitle = replaceVariables(optionSubtitle.getValue(config), order);
                
                if(player != null) {
                	effect.playEffect(player, order);
                	player.sendTitle(title, subtitle, 10, 100, 20);
                	
                    new BukkitRunnable() {
						double pitch = 5.0D;
						  
						public void run() {
							if (pitch < 7.5D) {
								player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3.0F, (float) pitch);
								pitch += 0.5D;
							} else {
								cancel();
							}
						}
                    }.runTaskTimer(Commerce.getInstance(), 1L, 4L);
                }
            });
            
            return true;
        } catch(Exception e) {
            ConsoleHelper.printError("Failed to complete order", e);
            return false;
        }
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
     * Replaces all variable placeholder with the
     * variable values and returns the parsed string.
     * 
     * @param value
     * @param order
     * @return
     */
    public String replaceVariables(String value, Order order) {
        for (OrderVariable variable : orderVariables) {
        	value = value.replace("{" + variable.getKey() + "}", variable.getValue(order));
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
