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
import nl.timvandijkhuizen.commerce.base.StorageType;
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
        try {
            if (request.decoderResult().isFailure()) {
                throw new BadRequestHttpException("Invalid HTTP request.");
            }

            handleRequest(ctx, request);
        } catch (HttpException e) {
            handleError(ctx, request, e.getStatus(), e);
        } catch (Throwable e) {
            ConsoleHelper.printError("An error occurred while handling HTTP request.", e);
            handleError(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        ConsoleHelper.printError("Uncaught exception while handling HTTP request.", e);
        handleError(ctx, null, HttpResponseStatus.INTERNAL_SERVER_ERROR, e);
    }

    /**
     * Handle all incoming requests.
     * 
     * @param ctx
     * @param request
     * @return
     * @throws Throwable
     */
    private void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Throwable {
        URL url = WebHelper.createWebUrl(request.uri());
        
        if (handleRouteRequest(ctx, request, url)) {
            return;
        }

        if (handleGatewayRequest(ctx, request, url)) {
            return;
        }

        throw new NotFoundHttpException("Page not found");
    }

    /**
     * Handle route requests.
     * 
     * @param ctx
     * @param request
     * @return
     * @throws Throwable
     */
    private boolean handleRouteRequest(ChannelHandlerContext ctx, FullHttpRequest request, URL url) throws Throwable {
        WebService webService = Commerce.getInstance().getService(WebService.class);
        StaticRoute route = webService.getRoutes().get(url.getPath());

        ConsoleHelper.printDebug("Trying to find route for action: " + url.getPath());
        
        if (route != null) {
            ConsoleHelper.printDebug("Matched route, handling request...");
            route.handleRequest(ctx, request);
            return true;
        }

        return false;
    }

    /**
     * Handle gateway requests.
     * 
     * @param ctx
     * @param request
     * @return
     * @throws Throwable
     */
    private boolean handleGatewayRequest(ChannelHandlerContext ctx, FullHttpRequest request, URL url) throws Throwable {
        QueryParameters queryParams = WebHelper.parseQuery(url);

        // Get gatewayId parameter
        StorageType storage = Commerce.getInstance().getStorage();
        UUID uniqueId = queryParams.getUUID("order");

        if (uniqueId != null) {
            Order order = storage.getOrderByUniqueId(uniqueId);

            // Make sure we've got a valid order
            if (order == null || !order.isValid(Order.SCENARIO_PAY)) {
                throw new BadRequestHttpException("Invalid order.");
            }

            // Let gateway handle the response
            FullHttpResponse response = order.getGateway().getClient().handleWebRequest(order, request);
            WebHelper.sendResponse(ctx, request, response);
            
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handle request errors.
     *
     * @param ctx
     * @param request
     * @param statusCode
     * @param error
     * @throws Throwable
     */
    private void handleError(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponseStatus statusCode, Throwable error) {
        WebService webService = Commerce.getInstance().getService(WebService.class);
        String content;

        // Try to render error template
        try {
            Map<String, Object> variables = new HashMap<>();

            variables.put("statusCode", statusCode);
            variables.put("error", error);

            content = webService.renderTemplate("error.html", variables);
        } catch (Exception e) {
            content = "Failed to render error template: " + e.getMessage();
        }

        FullHttpResponse response = WebHelper.createResponse(statusCode, content);
        WebHelper.sendResponse(ctx, request, response);
    }

}