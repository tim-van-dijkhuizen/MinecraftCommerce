package nl.timvandijkhuizen.commerce.services;

import java.util.function.Consumer;

import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class PaymentService extends BaseService {

    @Override
    public String getHandle() {
        return "payments";
    }
    
    public void createPaymentUrl(Order order, Consumer<String> callback) {
    	Gateway gateway = order.getGateway();
        
    	// Make sure the gateway was set
    	if(gateway == null) {
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
