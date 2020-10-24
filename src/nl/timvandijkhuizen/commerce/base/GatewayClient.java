package nl.timvandijkhuizen.commerce.base;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.PaymentUrl;

public interface GatewayClient {

    /**
     * Creates a payment URL for the specified order.
     * 
     * @param order
     * @return PaymentUrl
     */
    public PaymentUrl createPaymentUrl(Order order) throws Throwable;

    /**
     * Handles a web request for this gateway.
     * 
     * @param order
     * @param request
     * @return FullHttpResponse
     */
    public FullHttpResponse handleWebRequest(Order order, FullHttpRequest request) throws Throwable;

}
