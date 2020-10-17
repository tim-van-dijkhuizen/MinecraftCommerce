package nl.timvandijkhuizen.commerce.webserver.routes;

import java.io.InputStream;

import com.google.common.io.ByteStreams;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.webserver.ContentType;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;
import nl.timvandijkhuizen.commerce.webserver.errors.ServerErrorHttpException;
import nl.timvandijkhuizen.spigotutils.helpers.ResourceHelper;

public class RouteFavicon implements StaticRoute {

	private byte[] faviconBytes;
	
	public RouteFavicon() throws Exception {
		InputStream favicon = ResourceHelper.getStream("assets/favicon.ico");
		
		if(favicon != null) {
			faviconBytes = ByteStreams.toByteArray(favicon);
		}
	}
	
	@Override
	public FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
		if(faviconBytes == null) {
			throw new ServerErrorHttpException("Favicon file is missing.");
		}
		
		// Create buffer from favicon bytes
		ByteBuf buffer = Unpooled.copiedBuffer(faviconBytes);

		return WebHelper.createResponse(HttpResponseStatus.OK, buffer, ContentType.IMAGE_ICO, faviconBytes.length);
	}

}
