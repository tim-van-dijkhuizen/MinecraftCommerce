package nl.timvandijkhuizen.commerce.base;

import java.util.Map;

import com.google.gson.JsonObject;

public class PaymentResponse {

    public int statusCode;
    public Map<String, String> headers;
    public JsonObject body;
    
    public PaymentResponse(int statusCode, Map<String, String> headers, JsonObject body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public JsonObject getBody() {
        return body;
    }
    
}
