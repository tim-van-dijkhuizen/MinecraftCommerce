package nl.timvandijkhuizen.commerce.services;

import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class PaymentService extends BaseService {

    @Override
    public String getHandle() {
        return "payments";
    }
    
    public void createPaymentUrl(Order order, Gateway gateway, Consumer<String> callback) {
        GatewayType type = gateway.getType();
        
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                callback.accept(type.createPaymentUrl(order));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to create payment url: " + e.getMessage(), e);
            }
        });
    }

}
