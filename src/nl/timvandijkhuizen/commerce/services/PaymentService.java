package nl.timvandijkhuizen.commerce.services;

import java.util.function.Consumer;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class PaymentService extends BaseService {

    @Override
    public String getHandle() {
        return "payments";
    }

    /**
     * Create a payment URL for the specified order.
     * 
     * @param order
     * @param callback
     */
    public void createPaymentUrl(Order order, Consumer<String> callback) {
        Gateway gateway = order.getGateway();

        // Make sure the gateway was set
        if (gateway == null) {
            ConsoleHelper.printError("Unable to create payment link: Missing gateway");
            callback.accept(null);
            return;
        }

        // Create payment link
        ThreadHelper.getAsync(() -> {
            GatewayClient client = gateway.getClient();
            return client.createPaymentUrl(order);
        }, callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to create payment url", error);
        });
    }
    
    /**
     * Saves a transaction and completes the order
     * if the entire order has been paid for.
     * 
     * @param transaction
     * @return
     */
    public boolean saveTransaction(Transaction transaction) {
        StorageType storage = Commerce.getInstance().getStorage();
        OrderService orderService = Commerce.getInstance().getService("orders");

        try {
            storage.createTransaction(transaction);

            // Complete order if they've paid enough
            Order order = storage.getOrderById(transaction.getOrderId());
            
            if(order == null) {
                throw new Exception("Transaction order does not exist");
            }

            ConsoleHelper.printDebug("Saved transaction, paid " + order.getAmountPaid() + " / " + order.getTotal());
            
            // Complete order if they've paid enough
            if(order.getAmountPaid() >= order.getTotal()) {
                orderService.completeOrder(order);
            }
            
            return true;
        } catch (Throwable e) {
            ConsoleHelper.printError("Failed to save transaction", e);
            return false;
        }
    }

}
