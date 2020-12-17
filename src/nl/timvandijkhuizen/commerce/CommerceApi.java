package nl.timvandijkhuizen.commerce;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.StorageService;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class CommerceApi {

    public static void registerStorageType(StorageType storageType) {
        StorageService service = Commerce.getInstance().getService("storage");
        
        if(service == null) {
            throw new RuntimeException("Storage service has not been registered yet.");
        }
        
        service.registerStorageType(storageType);
        ConsoleHelper.printDebug("Registered storage type: " + storageType.getHandle());
    }
    
    public static void registerFieldType(FieldType<?> fieldType) {
        FieldService service = Commerce.getInstance().getService("fields");
        
        if(service == null) {
            throw new RuntimeException("Field service has not been registered yet.");
        }
        
        service.registerFieldType(fieldType);
        ConsoleHelper.printDebug("Registered field type: " + fieldType.getHandle());
    }
    
    public static void registerGatewayType(GatewayType gatewayType) {
        GatewayService service = Commerce.getInstance().getService("gateways");
        
        if(service == null) {
            throw new RuntimeException("Gateway service has not been registered yet.");
        }
        
        service.registerGatewayType(gatewayType);
        ConsoleHelper.printDebug("Registered gateway type: " + gatewayType.getHandle());
    }
    
    public static void registerOrderVariable(OrderVariable orderVariable) {
        OrderService service = Commerce.getInstance().getService("orders");
        
        if(service == null) {
            throw new RuntimeException("Order service has not been registered yet.");
        }
        
        service.registerOrderVariable(orderVariable);
        ConsoleHelper.printDebug("Registered order variable: " + orderVariable.getKey());
    }
    
    public static void registerOrderEffect(OrderEffect orderEffect) {
        OrderService service = Commerce.getInstance().getService("orders");
        
        if(service == null) {
            throw new RuntimeException("Order service has not been registered yet.");
        }
        
        service.registerOrderEffect(orderEffect);
        ConsoleHelper.printDebug("Registered order effect: " + orderEffect.getHandle());
    }
    
}
