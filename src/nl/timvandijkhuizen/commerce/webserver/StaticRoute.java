package nl.timvandijkhuizen.commerce.webserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface StaticRoute {
	
	/**
	 * Handles a HTTP request.
	 * 
	 * @param ctx
	 * @param request
	 */
	public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Throwable;
	
}
