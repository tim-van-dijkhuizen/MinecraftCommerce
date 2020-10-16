package nl.timvandijkhuizen.commerce.webserver.routes;

import java.io.InputStream;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;
import nl.timvandijkhuizen.commerce.webserver.errors.ServerErrorHttpException;
import nl.timvandijkhuizen.spigotutils.helpers.ResourceHelper;

public class RouteFavicon implements StaticRoute {

	@Override
	public FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
		InputStream file = ResourceHelper.getStream("assets/favicon.ico");
		
		// Make sure the file exists
		if(file == null) {
			throw new ServerErrorHttpException("Favicon file is missing.");
		}
		
		return WebHelper.createFileRequest(file);
	}

}
