package nl.timvandijkhuizen.commerce.webserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;
    
    public HttpInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }
    
	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		
        pipeline.addLast("ssl", new OptionalSslHandler(sslContext));
		pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
		pipeline.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", new HttpHandler());
	}

}
