package nl.timvandijkhuizen.commerce.webserver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import nl.timvandijkhuizen.commerce.webserver.errors.NotFoundHttpException;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response;

        // Handle request and catch any errors
        try {
            if(request.decoderResult().isFailure()) {
            	throw new BadRequestHttpException("Invalid HTTP request.");
            }
            
        	response = handleRequest(request);
        } catch(HttpException e) {
            response = handleError(e.getStatus(), e);
        } catch(Exception e) {
        	ConsoleHelper.printError("An error occurred while handling HTTP request.", e);
        	response = handleError(HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
        
        WebHelper.sendResponse(ctx, request, response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
    	FullHttpResponse response = handleError(HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
    	
        ConsoleHelper.printError("Uncaught exception while handling HTTP request.", e);

        if (ctx.channel().isActive()) {
        	WebHelper.sendResponse(ctx, response);
        }
    }

    /**
     * Handle all incoming requests.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
    	FullHttpResponse response;
    	
    	// Handle any matching route requests
    	if((response = handleRouteRequest(request)) != null) {
    		return response;
    	}
    	
    	// Handle any matching gateway requests
    	if((response = handleGatewayRequest(request)) != null) {
    		return response;
    	}
    	
    	throw new NotFoundHttpException("Page not found");
    }
    
    /**
     * Handle route requests.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private FullHttpResponse handleRouteRequest(FullHttpRequest request) throws Exception {
    	WebService webService = Commerce.getInstance().getService("web");
    	StaticRoute route = webService.getRoutes().get(request.uri());
    	
    	if(route != null) {
    		return route.handleRequest(request);
    	}
    	
    	return null;
    }
    
    /**
     * Handle gateway requests.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    private FullHttpResponse handleGatewayRequest(FullHttpRequest request) throws Exception {
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
        	return null;
        }
    }
    
    /**
     * Handle request errors.
     * 
     * @param statusCode
     * @param error
     * @return
     */
    private FullHttpResponse handleError(HttpResponseStatus statusCode, Throwable error) {
        WebService webService = Commerce.getInstance().getService("web");
        String content;
        
        // Try to render error template
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