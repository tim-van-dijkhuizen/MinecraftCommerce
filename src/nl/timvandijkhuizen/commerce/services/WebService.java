package nl.timvandijkhuizen.commerce.services;

import java.io.File;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

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
	private TemplateEngine templateEngine;
	
    @Override
    public String getHandle() {
        return "web";
    }
	
    @Override
    public void init() throws Exception {
    	YamlConfig config = Commerce.getInstance().getConfig();
        
        // Create SSL context
    	// =================================================
        ConfigOption<File> optionSslCert = config.getOption("general.sslCertificate");
        ConfigOption<File> optionSslKey = config.getOption("general.sslPrivateKey");
        SslContext sslContext = null;
        
        if(!optionSslCert.isValueEmpty(config) && !optionSslCert.isValueEmpty(config)) {
        	sslContext = SslContextBuilder.forServer(
    			optionSslCert.getValue(config),
    			optionSslKey.getValue(config)
    		).build();
        }
        
        // Setup web-server
        // =================================================
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
        
        // Setup template engine
        // =================================================
        templateEngine = new TemplateEngine();
        
        // Add file resolver
        FileTemplateResolver fileResolver = new FileTemplateResolver();
        
        fileResolver.setCheckExistence(true);
        fileResolver.setPrefix(Commerce.getInstance().getDataFolder().getAbsolutePath() + File.separatorChar);
        fileResolver.setSuffix(".html");
        fileResolver.setCharacterEncoding("UTF-8");
        fileResolver.setTemplateMode(TemplateMode.HTML);
        fileResolver.setOrder(1);
        fileResolver.setCacheable(true);
        
        templateEngine.addTemplateResolver(fileResolver);
        
        // Add class loader resolver
        ClassLoaderTemplateResolver resourceResolver = new ClassLoaderTemplateResolver();
        
        resourceResolver.setCheckExistence(true);
        resourceResolver.setPrefix("templates" + File.separatorChar);
        resourceResolver.setSuffix(".html");
        resourceResolver.setCharacterEncoding("UTF-8");
        resourceResolver.setTemplateMode(TemplateMode.HTML);
        resourceResolver.setOrder(2);
        resourceResolver.setCacheable(true);
        
        templateEngine.addTemplateResolver(resourceResolver);
    }
    
    public String renderTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        
        context.setVariables(variables);
        
        return templateEngine.process(templateName, context);
    }
    
    public String renderTemplate(File templateFile, Map<String, Object> variables) {
        return renderTemplate(templateFile.getPath(), variables);
    }
	
}
