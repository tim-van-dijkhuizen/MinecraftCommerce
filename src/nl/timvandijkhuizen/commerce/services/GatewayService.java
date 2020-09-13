package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.events.RegisterGatewayTypesEvent;
import nl.timvandijkhuizen.commerce.gateways.GatewayMollie;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class GatewayService extends BaseService {

    private Set<GatewayType> types;
    private Set<Gateway> gateways;
    
    @Override
    public String getHandle() {
        return "gateways";
    }

    @Override
    public void init() throws Exception {
        RegisterGatewayTypesEvent event = new RegisterGatewayTypesEvent();

        // Register gateway types
        event.addType(GatewayMollie.class);
        Bukkit.getServer().getPluginManager().callEvent(event);

        types = event.getTypes();
    }
    
    @Override
    public void load() throws Exception {
        Storage storage = Commerce.getInstance().getStorage();
        
        // Load fields from storage
        try {
            gateways = storage.getGateways();
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load gateways: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns all gateways.
     * 
     * @return
     */
    public Set<Gateway> getGateways() {
        return gateways;
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
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                if (isNew) {
                    storage.createGateway(gateway);
                    gateways.add(gateway);
                } else {
                    storage.updateGateway(gateway);
                }

                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to create/update gateway: " + e.getMessage(), e);
            }
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
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                storage.deleteGateway(gateway);
                gateways.remove(gateway);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete gateway: " + e.getMessage(), e);
            }
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
