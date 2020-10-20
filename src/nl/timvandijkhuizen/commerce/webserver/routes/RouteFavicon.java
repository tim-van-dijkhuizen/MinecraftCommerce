package nl.timvandijkhuizen.commerce.webserver.routes;

import java.io.InputStream;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.webserver.ContentType;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;
import nl.timvandijkhuizen.commerce.webserver.errors.ServerErrorHttpException;
import nl.timvandijkhuizen.spigotutils.helpers.ResourceHelper;

public class RouteFavicon implements StaticRoute {

	@Override
	public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Throwable {
	    InputStream favicon = ResourceHelper.getStream("assets/favicon.ico");
	    
		if(favicon == null) {
			throw new ServerErrorHttpException("Favicon file is missing.");
		}
		
		// Send file
		WebHelper.sendFileResponse(ctx, request, ContentType.IMAGE_ICO, favicon);
	}

}
