package nl.timvandijkhuizen.commerce.elements;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.base.Element;

public class Transaction extends Element {

    private int orderId;
    private String reference;
    private JsonObject meta;
    
    public Transaction(int id, int orderId, String reference, JsonObject meta) {
        this(orderId, reference, meta);
        this.setId(id);
    }
    
    public Transaction(int orderId, String reference, JsonObject meta) {
        this.orderId = orderId;
        this.reference = reference;
        this.meta = meta;
    }
    
    @Override
    protected boolean validate(String scenario) {
        return true;
    }

    public int getOrderId() {
        return orderId;
    }
    
    public String getReference() {
        return reference;
    }
    
    public JsonObject getMeta() {
        return meta;
    }
    
}
