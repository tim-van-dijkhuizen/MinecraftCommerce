package nl.timvandijkhuizen.commerce.webserver;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String TYPE_PLAIN = "text/plain; charset=UTF-8";
    public static final String TYPE_JSON = "application/json; charset=UTF-8";
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (request.decoderResult() != DecoderResult.SUCCESS) {
            ctx.close();
            return;
        }
        
        // Handle request
        HttpMethod method = request.method();
        String uri = request.uri();

        ConsoleHelper.printInfo("HTTP request " + method.name() + " " + uri);
        
        if(method == HttpMethod.GET && uri == "/test") {
            writeNotFound(ctx, request);
            return;
        }
        
        try {
            writeResponse(ctx, request, HttpResponseStatus.OK, TYPE_PLAIN, "Hello World");
        } catch (final Exception e) {
            writeInternalServerError(ctx, request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        ConsoleHelper.printError("HTTP request exception", e);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void writeNotFound(ChannelHandlerContext ctx, FullHttpRequest request) {
        writeErrorResponse(ctx, request, HttpResponseStatus.NOT_FOUND);
    }

    private void writeInternalServerError(ChannelHandlerContext ctx, FullHttpRequest request) {
        writeErrorResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private void writeErrorResponse(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponseStatus status) {
        writeResponse(ctx, request, status, TYPE_PLAIN, status.reasonPhrase().toString());
    }

    private void writeResponse(final ChannelHandlerContext ctx, final FullHttpRequest request, HttpResponseStatus status, CharSequence contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteBuf entity = Unpooled.wrappedBuffer(bytes);
        
        writeResponse(ctx, request, status, entity, contentType, bytes.length);
    }

    private void writeResponse(ChannelHandlerContext ctx, FullHttpRequest request, HttpResponseStatus status, ByteBuf buf, CharSequence contentType, int contentLength) {
        final boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);

        // Build the response object.
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf, false);

        final ZonedDateTime dateTime = ZonedDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        final DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
        headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }
        
}