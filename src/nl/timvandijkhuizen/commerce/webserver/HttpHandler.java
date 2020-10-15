package nl.timvandijkhuizen.commerce.webserver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.services.WebService;
import nl.timvandijkhuizen.commerce.webserver.errors.BadRequestHttpException;
import nl.timvandijkhuizen.commerce.webserver.errors.HttpException;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response;
        
        // Handle request and catch any errors
        try {
        	response = handleRequest(request);
        } catch(HttpException e) {
            response = handleError(e.getStatus(), e);
        } catch(Exception e) {
        	ConsoleHelper.printError("An error occurred while handling a web request.", e);
        	response = handleError(HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
        
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ConsoleHelper.printError("HTTP request exception", cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
    	URL url = WebHelper.createWebUrl(request.uri());
        QueryParameters queryParams = WebHelper.parseQuery(url);
        
        // Get gatewayId parameter
        Storage storage = Commerce.getInstance().getStorage();
        UUID uniqueId = queryParams.getUUID("order");
        
        if(uniqueId != null) {
        	Order order = storage.getOrderByUniqueId(uniqueId);
        	
        	// Make sure we've got a valid order
        	if(order == null || !order.isValid(Order.SCENARIO_PAY)) {
        	    throw new BadRequestHttpException("Invalid order.");
        	}
        	
        	// Let gateway handle the response
        	return order.getGateway().getClient().handleWebRequest(order, request);
        } else {
        	throw new BadRequestHttpException("Missing required order parameter.");
        }
    }
    
    private FullHttpResponse handleError(HttpResponseStatus statusCode, Exception error) {
        WebService webService = Commerce.getInstance().getService("web");
        String content;
        
        try {
            Map<String, Object> variables = new HashMap<>();
            
            variables.put("statusCode", statusCode);
            variables.put("error", error);
            
            content = webService.renderTemplate("error.html", variables);
        } catch(Exception e) {
            content = "Failed to render error template: " + e.getMessage();
        }
        
        return WebHelper.createResponse(statusCode, content);
    }
    
}