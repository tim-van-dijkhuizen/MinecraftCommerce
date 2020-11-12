package nl.timvandijkhuizen.commerce.webserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    public HttpInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Add SSL if its enabled
        if (sslContext != null) {
            pipeline.addLast("ssl", new OptionalSslHandler(sslContext));
        }

        // Add other handlers
        pipeline.addLast("http", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("chunked", new ChunkedWriteHandler());
        pipeline.addLast("handler", new HttpHandler());
    }

}
