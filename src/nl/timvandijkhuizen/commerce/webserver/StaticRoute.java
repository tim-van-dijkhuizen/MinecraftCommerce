package nl.timvandijkhuizen.commerce.webserver;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface StaticRoute {
	
	/**
	 * Handles a HTTP request and returns the proper response.
	 * 
	 * @param request
	 * @return
	 */
	public FullHttpResponse handleRequest(FullHttpRequest request) throws Exception;
	
}
