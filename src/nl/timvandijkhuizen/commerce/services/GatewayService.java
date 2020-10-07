package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.events.RegisterGatewayTypesEvent;
import nl.timvandijkhuizen.commerce.gateways.paypal.GatewayPayPal;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class GatewayService extends BaseService {

    private Set<GatewayType> types;
    
    @Override
    public String getHandle() {
        return "gateways";
    }

    @Override
    public void init() throws Exception {
        RegisterGatewayTypesEvent event = new RegisterGatewayTypesEvent();

        event.addType(GatewayPayPal.class);
        Bukkit.getServer().getPluginManager().callEvent(event);

        types = event.getTypes();
    }
    
    /**
     * Returns all gateways.
     * 
     * @param callback
     */
    public void getGateways(Consumer<Set<Gateway>> callback) {
        Storage storage = Commerce.getInstance().getStorage();

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
        Storage storage = Commerce.getInstance().getStorage();
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
        Storage storage = Commerce.getInstance().getStorage();

        // Delete gateway
        ThreadHelper.executeAsync(() -> storage.deleteGateway(gateway), () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete gateway: " + error.getMessage(), error);
        });
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

}
