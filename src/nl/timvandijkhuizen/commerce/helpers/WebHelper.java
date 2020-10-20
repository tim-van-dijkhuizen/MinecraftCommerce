package nl.timvandijkhuizen.commerce.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.webserver.ContentType;
import nl.timvandijkhuizen.commerce.webserver.QueryParameters;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class WebHelper {
    
	/**
	 * Creates a formatted URL from the specified action.
	 * Uses the configured webUrl and webPort as base 
	 * and then adds the action at the end.
	 * 
	 * @param action
	 * @return URL The formatted URL.
	 * @throws RuntimeException
	 */
    public static URL createWebUrl(String action) {
    	Commerce plugin = Commerce.getInstance();
    	
    	// Get configuration & options
    	YamlConfig config = plugin.getConfig();
    	ConfigOption<String> optionHost = config.getOption("general.webserverHost");
    	ConfigOption<Integer> optionPort = config.getOption("general.webserverPort");
    	ConfigOption<File> optionSSLCert = config.getOption("general.sslCertificate");
    	ConfigOption<File> optionSSLKey = config.getOption("general.sslPrivateKey");
    	
    	// Get configuration values
    	String host = optionHost.getValue(config);
    	int port = optionPort.getValue(config);
    	String protocol = "http";
    	
    	if(!optionSSLCert.isValueEmpty(config) && !optionSSLKey.isValueEmpty(config)) {
    		protocol += "s";
    	}
    	
    	// Parse action
    	action.replace('\\', '/');
    	
    	if(!action.startsWith("/")) {
    	    action = "/" + action;
    	}
    	
    	if(action.endsWith("/")) {
    	    action = action.substring(0, action.length() - 1);
    	}
    	
    	try {
    		return new URL(protocol, host, port, action);
    	} catch(MalformedURLException e) {
    		throw new RuntimeException(e);
    	}
    }
    
    /**
     * Parses an URL into a Map of query params.
     * 
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static QueryParameters parseQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();
        String query = url.getQuery();
        
        if(query != null) {
	        for (String pair : query.split("&")) {
	            int idx = pair.indexOf("=");
	            map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	        }
        }
        
        return new QueryParameters(map);
    }
    
    /**
     * Creates a redirect response.
     * 
     * @param url The URL to redirect to.
     * @return
     */
    public static FullHttpResponse createRedirectRequest(String url) {
        FullHttpResponse response = createResponse(HttpResponseStatus.TEMPORARY_REDIRECT, ContentType.TEXT_HTML, "");
        
        response.headers().set(HttpHeaderNames.LOCATION, url);
        
        return response;
    }
    
    /**
     * Creates an OK response with the specified content.
     * 
     * @param content
     * @return
     */
    public static FullHttpResponse createResponse(String content) {
        return createResponse(HttpResponseStatus.OK, ContentType.TEXT_HTML, content);
    }
    
    /**
     * Creates a response with the specified status and content.
     * 
     * @param status
     * @param content
     * @return
     */
    public static FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        return createResponse(status, ContentType.TEXT_HTML, content);
    }
    
    /**
     * Creates a response the the specified status, contentType and content.
     * 
     * @param status
     * @param contentType
     * @param content
     * @return
     */
    public static FullHttpResponse createResponse(HttpResponseStatus status, CharSequence contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ByteBuf entity = Unpooled.wrappedBuffer(bytes);
        
        return createResponse(status, entity, contentType, bytes.length);
    }

    /**
     * Creates a response the with the specified specifications.
     * 
     * @param status
     * @param buf
     * @param contentType
     * @param contentLength
     * @return
     */
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

    /**
     * Sends the response and closes the connection.
     * 
     * @param ctx
     * @param response
     */
    public static void sendResponse(ChannelHandlerContext ctx, FullHttpResponse response) {
    	sendResponse(ctx, null, response);
    }
    
    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response
     * and closes the connection after the response being sent.
     * 
     * @param ctx
     * @param request
     * @param response
     */
    public static void sendResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        boolean keepAlive = request != null ? HttpUtil.isKeepAlive(request) : false;
        
        // Add connection header
        if (keepAlive) {
        	response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
        	response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }

        // Close if not keep alive
        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    public static void sendFileResponse(ChannelHandlerContext ctx, FullHttpRequest request, String contentType, InputStream stream) throws IOException {
        boolean keepAlive = request != null ? HttpUtil.isKeepAlive(request) : false;
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        
        // Add headers
        ZonedDateTime dateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;

        DefaultHttpHeaders headers = (DefaultHttpHeaders) response.headers();
        headers.set(HttpHeaderNames.DATE, dateTime.format(formatter));
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, Integer.toString(stream.available()));

        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }

        // Write the initial response
        ctx.write(response);

        // Write the content
        ChannelFuture future;
        
        if (ctx.pipeline().get("ssl") == null) {
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
            future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            future = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 8192)), ctx.newProgressivePromise());
        }

        // Close after we've sent all content
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
}
