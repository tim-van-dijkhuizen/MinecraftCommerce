package nl.timvandijkhuizen.commerce.webserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        FullHttpResponse response;
        HttpMethod method = request.method();
        String uri = request.uri();

        ConsoleHelper.printInfo("HTTP request " + method.name() + " " + uri);

        if(method == HttpMethod.GET && uri.equals("/test")) {
            response = WebHelper.createResponse(HttpResponseStatus.NOT_FOUND, "Not Found");
        } else {
            try {
                response = WebHelper.createResponse("Hello World!");
            } catch (Exception e) {
                response = WebHelper.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        
        WebHelper.writeResponse(ctx, response);
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
        
}