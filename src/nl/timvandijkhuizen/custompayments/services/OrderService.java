package nl.timvandijkhuizen.custompayments.services;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataAction;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class OrderService implements Service {

    
    
    @Override
    public String getHandle() {
        return "orders";
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() throws Exception {

    }
    
    /**
     * Returns the cart of the specified user.
     * 
     * @param player
     * @return
     */
    public Order getCart(Player player) {
        CacheService cacheService = CustomPayments.getInstance().getService("cache");
        Order cart = cacheService.getCart(player);
        
        // Set default if null
        if(cart == null) {
            cart = createCart(player);
            cacheService.updateCart(player, cart);
            return cart;
        }
        
        return cart;
    }
    
    /**
     * Creates a new cart for the specified player.
     * 
     * @param player
     * @return
     */
    public Order createCart(Player player) {
        UserService userService = CustomPayments.getInstance().getService("users");
        UserPreferences preferences = userService.getPreferences(player);
        ConfigOption<StoreCurrency> optionCurrency = preferences.getOption("currency");
        StoreCurrency currency = optionCurrency.getValue(preferences);
        
        // Create order number
        String number = RandomStringUtils.random(20, true, true);
        
        return new Order(number, player.getUniqueId(), player.getName(), currency);
    }
    
    /**
     * Returns all orders.
     * 
     * @param callback
     */
    public void getOrders(Consumer<List<Order>> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                List<Order> orders = storage.getOrders();
                MainThread.execute(() -> callback.accept(orders));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to load orders: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Saves a order.
     * 
     * @param order
     * @param callback
     */
    public void saveOrder(Order order, Consumer<Boolean> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();
        boolean isNew = order.getId() == null;

        // Validate the model
        if (!order.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit order
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            DataList<LineItem> lineItems = order.getLineItems();
            
            try {
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
                
                // Remove pending from data list
                lineItems.clearPending();

                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to create/update order: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deletes a order.
     * 
     * @param order
     * @param callback
     */
    public void deleteOrder(Order order, Consumer<Boolean> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();

        // Delete order
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                storage.deleteOrder(order);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete order: " + e.getMessage(), e);
            }
        });
    }

}
