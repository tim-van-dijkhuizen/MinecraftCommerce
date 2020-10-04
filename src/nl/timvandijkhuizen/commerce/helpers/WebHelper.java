package nl.timvandijkhuizen.commerce.helpers;

import java.io.File;
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
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.webserver.QueryParameters;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class WebHelper {

    public static final String TYPE_PLAIN = "text/plain; charset=UTF-8";
    public static final String TYPE_HTML = "text/html; charset=UTF-8";
    public static final String TYPE_JSON = "application/json; charset=UTF-8";

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
     * Creates an OK response with the specified content.
     * 
     * @param content
     * @return
     */
    public static FullHttpResponse createResponse(String content) {
        return createResponse(HttpResponseStatus.OK, TYPE_PLAIN, content);
    }
    
    /**
     * Creates a response with the specified status and content.
     * 
     * @param status
     * @param content
     * @return
     */
    public static FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        return createResponse(status, TYPE_PLAIN, content);
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

}
