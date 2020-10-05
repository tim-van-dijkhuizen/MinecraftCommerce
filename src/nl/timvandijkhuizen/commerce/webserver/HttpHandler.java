package nl.timvandijkhuizen.commerce.webserver;

import java.net.URL;
import java.util.UUID;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response;
        
        // Handle request and catch any errors
        try {
        	response = handleRequest(request);
        } catch(Exception e) {
        	ConsoleHelper.printError("An error occurred while handling a web request.", e);
        	response = WebHelper.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, "An internal error occurred.");
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
        	Gateway gateway = order != null ? order.getGateway() : null;
        	
        	if(order == null || gateway == null) {
        		return WebHelper.createResponse(HttpResponseStatus.BAD_REQUEST, "Invalid order.");
        	}
        	
        	// Let gateway handle the response
        	return gateway.getClient().handleWebRequest(order, request);
        } else {
        	return WebHelper.createResponse(HttpResponseStatus.BAD_REQUEST, "Missing required order parameter.");
        }
    }
    
}