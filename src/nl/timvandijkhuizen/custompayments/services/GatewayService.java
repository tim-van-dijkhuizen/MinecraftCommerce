package nl.timvandijkhuizen.custompayments.services;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.GatewayType;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.events.RegisterGatewayTypesEvent;
import nl.timvandijkhuizen.custompayments.gateways.GatewayMollie;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
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

        event.addType(GatewayMollie.class);
        Bukkit.getServer().getPluginManager().callEvent(event);

        types = event.getTypes();
    }
    
    /**
     * Returns all gateways.
     * 
     * @param callback
     */
    public void getGateways(Consumer<Set<Gateway>> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                Set<Gateway> gateways = storage.getGateways();
                MainThread.execute(() -> callback.accept(gateways));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to load gateways: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Saves a gateway.
     * 
     * @param gateway
     * @param callback
     */
    public void saveGateway(Gateway gateway, Consumer<Boolean> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();
        boolean isNew = gateway.getId() == null;

        // Validate the model
        if (!gateway.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit gateway
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                if (isNew) {
                    storage.createGateway(gateway);
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
        Storage storage = CustomPayments.getInstance().getStorage();

        // Delete gateway
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                storage.deleteGateway(gateway);
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
