package nl.timvandijkhuizen.commerce.webserver.routes;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.webserver.ContentType;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;

public class RouteRobots implements StaticRoute {

	@Override
	public FullHttpResponse handleRequest(FullHttpRequest request) throws Exception {
		return WebHelper.createResponse(
			HttpResponseStatus.OK,
			ContentType.TEXT_PLAIN,
			"User-agent: *\r\nDisallow: /"
		);
	}

}
