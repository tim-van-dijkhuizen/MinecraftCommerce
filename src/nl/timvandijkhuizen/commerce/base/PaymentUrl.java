package nl.timvandijkhuizen.commerce.base;

public class PaymentUrl {

    private String url;
    private long expiryTime;

    public PaymentUrl(String url, long expiryTime) {
        this.url = url;
        this.expiryTime = expiryTime;
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

    /**
     * Returns whether this URL has expired.
     * 
     * @return
     */
    public boolean hasExpired() {
        return expiryTime <= System.currentTimeMillis();
    }

}
