package nl.timvandijkhuizen.commerce.services;

import java.io.File;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.webserver.HttpInitializer;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class WebService extends BaseService {

	private static final EventLoopGroup THREAD_GROUP = new NioEventLoopGroup(4);
	
	private ServerBootstrap bootstrap;
	
    @Override
    public String getHandle() {
        return "webServer";
    }
	
    @Override
    public void init() throws Exception {
    	YamlConfig config = Commerce.getInstance().getConfig();
        
        // Create SSL context
        ConfigOption<File> optionSslCert = config.getOption("general.sslCertificate");
        ConfigOption<File> optionSslKey = config.getOption("general.sslPrivateKey");
        SslContext sslContext = null;
        
        if(!optionSslCert.isValueEmpty(config) && !optionSslCert.isValueEmpty(config)) {
        	sslContext = SslContextBuilder.forServer(
    			optionSslCert.getValue(config),
    			optionSslKey.getValue(config)
    		).build();
        }
        
        // Configure server
        bootstrap = new ServerBootstrap();
        
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.group(THREAD_GROUP);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new HttpInitializer(sslContext));
        bootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        
        // Bind to configured port
        ConfigOption<Integer> optionPort = config.getOption("general.webserverPort");
        int port = optionPort.getValue(config);
        
        ConsoleHelper.printInfo("Starting webserver on port " + port);
        
        try {
            bootstrap.bind(port).sync();
            ConsoleHelper.printInfo("Successfully started webserver on port " + port);
        } catch(Exception e) {
            ConsoleHelper.printError("Failed to start webserver on port " + port, e);
        }
    }
	
}