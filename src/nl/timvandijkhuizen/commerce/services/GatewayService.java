package nl.timvandijkhuizen.commerce.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class GatewayService implements Service {

    private Set<GatewayType> types = new LinkedHashSet<>();

    @Override
    public String getHandle() {
        return "gateways";
    }

    /**
     * Register a gateway type
     * 
     * @param type
     */
    public void registerGatewayType(GatewayType gatewayType) {
        types.add(gatewayType);
    }
    
    /**
     * Returns all available gateway types.
     * 
     * @return
     */
    public Set<GatewayType> getTypes() {
        return types;
    }

    /**
     * Returns a gateway type by its handle.
     * 
     * @param handle
     * @return
     */
    public GatewayType getTypeByHandle(String handle) {
        return types.stream().filter(i -> i.getHandle().equals(handle)).findFirst().orElse(null);
    }
    
    /**
     * Returns all gateways.
     * 
     * @param callback
     */
    public void getGateways(Consumer<Set<Gateway>> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getGateways(), callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load gateways: " + error.getMessage(), error);
        });
    }

    /**
     * Saves a gateway.
     * 
     * @param gateway
     * @param callback
     */
    public void saveGateway(Gateway gateway, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();
        boolean isNew = gateway.getId() == null;

        // Validate the model
        if (!gateway.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit gateway
        ThreadHelper.executeAsync(() -> {
            if (isNew) {
                storage.createGateway(gateway);
            } else {
                storage.updateGateway(gateway);
            }

            gateway.clearClientCache();
        }, () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to create/update gateway: " + error.getMessage(), error);
        });
    }

    /**
     * Deletes a gateway.
     * 
     * @param gateway
     * @param callback
     */
    public void deleteGateway(Gateway gateway, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        // Delete gateway
        ThreadHelper.executeAsync(() -> storage.deleteGateway(gateway), () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete gateway: " + error.getMessage(), error);
        });
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

}
