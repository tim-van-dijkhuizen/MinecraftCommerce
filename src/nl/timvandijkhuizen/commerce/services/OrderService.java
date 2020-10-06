package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataAction;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class OrderService extends BaseService {

    @Override
    public String getHandle() {
        return "orders";
    }
    
    /**
     * Returns the cart of the specified user.
     * 
     * @param player
     * @return
     */
    public void getCart(Player player, Consumer<Order> callback) {
        Storage storage = Commerce.getInstance().getStorage();

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
        Storage storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getOrders(), callback, error -> {
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
        Storage storage = Commerce.getInstance().getStorage();
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
        Storage storage = Commerce.getInstance().getStorage();
        
        try {
            storage.completeOrder(order);
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
        Storage storage = Commerce.getInstance().getStorage();

        // Delete order
        ThreadHelper.executeAsync(() -> storage.deleteOrder(order), () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete order: " + error.getMessage(), error);
        });
    }

}
