package nl.timvandijkhuizen.commerce.elements;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;

public class Transaction extends Element {

    private int orderId;
    private Gateway gateway;
    private StoreCurrency currency;
    private String reference;
    private float amount;
    private JsonObject meta;
    private long dateCreated;
    
    public Transaction(int id, int orderId, Gateway gateway, StoreCurrency currency, String reference, float amount, JsonObject meta, long dateCreated) {
        this(orderId, gateway, currency, reference, amount, meta, dateCreated);
        this.setId(id);
    }
    
    public Transaction(int orderId, Gateway gateway, StoreCurrency currency, String reference, float amount, long dateCreated) {
        this(orderId, gateway, currency, reference, amount, null, dateCreated);
    }
    
    public Transaction(int orderId, Gateway gateway, StoreCurrency currency, String reference, float amount, JsonObject meta, long dateCreated) {
        this.orderId = orderId;
        this.gateway = gateway;
        this.currency = currency;
        this.reference = reference;
        this.amount = amount;
        this.meta = meta;
        this.dateCreated = dateCreated;
    }
    
    @Override
    protected boolean validate(String scenario) {
        return true;
    }

    public int getOrderId() {
        return orderId;
    }
    
    public Gateway getGateway() {
        return gateway;
    }
    
    public StoreCurrency getCurrency() {
        return currency;
    }
    
    public String getReference() {
        return reference;
    }
    
    public float getAmount() {
        return amount;
    }
    
    public JsonObject getMeta() {
        return meta;
    }
    
    public long getDateCreated() {
        return dateCreated;
    }
    
}
