package nl.timvandijkhuizen.commerce.elements;

import nl.timvandijkhuizen.commerce.base.Element;

public class PaymentUrl extends Element {

    private String url;
    private long expiryTime;
    
    public PaymentUrl(String url, long expiryTime) {
        this.url = url;
        this.expiryTime = expiryTime;
    }
    
    @Override
    protected boolean validate(String scenario) {
        return true;
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
    public long getExpiryTime() {
        return expiryTime;
    }

}
