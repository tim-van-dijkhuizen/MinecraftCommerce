package nl.timvandijkhuizen.commerce.webserver;

import java.io.File;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.JdkSslServerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import nl.timvandijkhuizen.commerce.Commerce;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	public void initChannel(SocketChannel ch) {
		ChannelPipeline pipeline = ch.pipeline();
		
		// Set up SSL
        try {
            File folder = Commerce.getInstance().getDataFolder();
            File certChain = new File(folder, "certs/cert.pem");
            File certKey = new File(folder, "certs/privkey.pem");
            
            if(!certChain.exists() || !certKey.exists()) {
                throw new Exception("Certificate files are missing");
            }
            
            // Create SSL handler
            SslContext sslContext = new JdkSslServerContext(certChain, certKey);
            SSLEngine sslEngine = sslContext.newEngine(ch.alloc());
            SslHandler sslHandler = new SslHandler(sslEngine);
            
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);
            
            pipeline.addLast("ssl", sslHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false));
		pipeline.addLast("aggregator", new HttpObjectAggregator(100 * 1024 * 1024));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", new HttpHandler());
	}

}
