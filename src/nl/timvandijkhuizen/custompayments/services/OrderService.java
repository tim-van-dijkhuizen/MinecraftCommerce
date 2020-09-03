package nl.timvandijkhuizen.custompayments.services;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.spigotutils.MainThread;
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
                
                // Set order id on LineItems
                for (LineItem lineItem : lineItems) {
                    lineItem.setOrderId(order.getId());
                }

                // Update commands
                for (LineItem lineItem : lineItems.getToAdd()) {
                    storage.createLineItem(lineItem);
                }
                
                for (LineItem lineItem : lineItems.getToUpdate()) {
                    storage.updateLineItem(lineItem);
                }

                for (LineItem lineItem : lineItems.getToRemove()) {
                    storage.deleteLineItem(lineItem);
                }

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
