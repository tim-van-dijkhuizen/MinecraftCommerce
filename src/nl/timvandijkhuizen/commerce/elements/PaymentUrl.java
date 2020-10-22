package nl.timvandijkhuizen.commerce.elements;

import nl.timvandijkhuizen.commerce.base.Element;

public class PaymentUrl extends Element {

    private int orderId;
    private String url;
    private Long expiryTime;
    
    public PaymentUrl(int id, int orderId, String url, Long expiryTime) {
        this(orderId, url, expiryTime);
        this.setId(id);
    }
    
    public PaymentUrl(int orderId, String url, Long expiryTime) {
        this.orderId = orderId;
        this.url = url;
        this.expiryTime = expiryTime;
    }
    
    @Override
    protected boolean validate(String scenario) {
        return true;
    }
    
    /**
     * Returns the id of the order this url belongs to.
     * 
     * @return
     */
    public int getOrderId() {
        return orderId;
    }
    
    /**
     * Returns the payment URL.
     * 
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the time at which the URL expires.
     * 
     * @return
     */
    public Long getExpiryTime() {
        return expiryTime;
    }

}
