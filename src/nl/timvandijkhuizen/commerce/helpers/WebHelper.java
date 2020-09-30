package nl.timvandijkhuizen.commerce.helpers;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class WebHelper {

    public static final String TYPE_PLAIN = "text/plain; charset=UTF-8";
    public static final String TYPE_JSON = "application/json; charset=UTF-8";

    public static FullHttpResponse createResponse(String content) {
        return createResponse(HttpResponseStatus.OK, TYPE_PLAIN, content);
    }
    
    public static FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        return createResponse(status, TYPE_PLAIN, content);
    }
    
    public static FullHttpResponse createResponse(HttpResponseStatus status, CharSequence contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteBuf entity = Unpooled.wrappedBuffer(bytes);
        
        return createResponse(status, entity, contentType, bytes.length);
    }

    public static FullHttpResponse createResponse(HttpResponseStatus status, ByteBuf buf, CharSequence contentType, int contentLength) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buf, false);

        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
        headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(contentLength));

        return response;
    }
    
    public static void writeResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
        
        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.writeAndFlush(response, ctx.voidPromise());
        }
    }
    
    public static void writeResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
}
