package nl.timvandijkhuizen.commerce.base;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.elements.Order;

public interface GatewayClient {
    
    /**
     * Creates a payment URL for the specified order.
     * Returns null if we were unable to obtain an url.
     * 
     * @param order
     * @return string|null
     */
    public PaymentUrl createPaymentUrl(Order order) throws Exception;

    /**
     * Processes a webhook response. Returns
     * null if the payment did not succeed.
     * 
     * @param response
     * @return Payment|null
     */
    public FullHttpResponse handleWebRequest(Order order, FullHttpRequest request) throws Exception;
	
}
