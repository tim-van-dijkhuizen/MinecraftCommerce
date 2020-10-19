package nl.timvandijkhuizen.commerce;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.commands.CommandCommerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeOrderEffect;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypePort;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStorageType;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.commerce.effects.EffectFirework;
import nl.timvandijkhuizen.commerce.events.RegisterStorageTypesEvent;
import nl.timvandijkhuizen.commerce.services.CacheService;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.PaymentService;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.commerce.services.UserService;
import nl.timvandijkhuizen.commerce.services.WebService;
import nl.timvandijkhuizen.commerce.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeFile;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeList;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class Commerce extends PluginBase {
    
    public static final Material[] MATERIAL_ICONS = Stream.of(Material.values()).filter(icon -> icon.isItem() && !icon.isAir()).toArray(Material[]::new);
    public static final StoreCurrency DEFAULT_CURRENCY = new StoreCurrency("USD", 1, new DecimalFormat("###,###,###.00"));
    
    private static Commerce instance;
    private YamlConfig config;
    private Set<StorageType> storageTypes;
    
    // Configuration options
    private ConfigOption<String> configServerName;
    private ConfigOption<Boolean> configDevMode;
    private ConfigOption<List<StoreCurrency>> configCurrencies;
    private ConfigOption<StoreCurrency> configBaseCurrency;
    private ConfigOption<String> configWebserverHost;
    private ConfigOption<Integer> configWebserverPort;
    private ConfigOption<File> configSslCertificate;
    private ConfigOption<File> configSslPrivateKey;
    private ConfigOption<OrderEffect> configOrderEffect;
    private ConfigOption<String> configOrderTitle;
    private ConfigOption<String> configOrderSubtitle;
    private ConfigOption<StorageType> configStorageType;

    @Override
    public void init() throws Exception {
        instance = this;
        ThreadHelper.setPlugin(this);
        
        // Register storage types
        RegisterStorageTypesEvent storageEvent = new RegisterStorageTypesEvent();
        
        storageEvent.addStorageType(new StorageMysql());
        
        getServer().getPluginManager().callEvent(storageEvent);
        storageTypes = storageEvent.getStorageTypes();
        
        // Setup configuration options
        config = new YamlConfig(this);
        
        // Create options
        ConfigTypeFile configTypeCert = new ConfigTypeFile(new Pattern[] { Pattern.compile("^.*\\.pem$") });
        
        configServerName = new ConfigOption<>("general.serverName", "Server Name", Material.PAPER, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("Minecraft Commerce");
        
        configDevMode = new ConfigOption<>("general.devMode", "Dev Mode", Material.REDSTONE, ConfigTypes.BOOLEAN)
            .setRequired(true)
            .setDefaultValue(false);
        
        configCurrencies = new ConfigOption<>("general.currencies", "Currencies", Material.SUNFLOWER, new ConfigTypeList<StoreCurrency>(StoreCurrency.class, "Currencies", Material.SUNFLOWER))
            .setRequired(true)
            .setDefaultValue(Arrays.asList(DEFAULT_CURRENCY));
        
        configBaseCurrency = new ConfigOption<>("general.baseCurrency", "Base Currency", Material.SUNFLOWER, new ConfigTypeStoreCurrency())
            .setRequired(true)
            .setDefaultValue(DEFAULT_CURRENCY);
        
        configWebserverHost = new ConfigOption<>("general.webserverHost", "Webserver Host", Material.COBWEB, ConfigTypes.DOMAIN)
            .setRequired(true)
            .setDefaultValue(getServer().getIp());
        
        configWebserverPort = new ConfigOption<>("general.webserverPort", "Webserver Port", Material.COBWEB, new ConfigTypePort())
            .setRequired(true)
            .setDefaultValue(8080)
            .setMeta(new DataArguments(true));
        
        configSslCertificate = new ConfigOption<>("general.sslCertificate", "SSL Certificate", Material.TRIPWIRE_HOOK, configTypeCert)
            .setMeta(new DataArguments(true));
        
        configSslPrivateKey = new ConfigOption<>("general.sslPrivateKey", "SSL Private Key", Material.TRIPWIRE_HOOK, configTypeCert)
            .setMeta(new DataArguments(true));
        
        configOrderEffect = new ConfigOption<>("general.completeEffect", "Order Complete Effect", Material.FIREWORK_ROCKET, new ConfigTypeOrderEffect())
            .setRequired(true)
            .setDefaultValue(new EffectFirework());
        
        configOrderTitle = new ConfigOption<>("general.completeTitle", "Order Complete Title", Material.OAK_SIGN, ConfigTypes.MESSAGE)
            .setDefaultValue("&a&lOrder Completed");
        
        configOrderSubtitle = new ConfigOption<>("general.completeSubtitle", "Order Complete Subtitle", Material.OAK_SIGN, ConfigTypes.MESSAGE)
            .setDefaultValue("&7Thanks for your order {playerUsername}");
        
        configStorageType = new ConfigOption<>("storage.type", "Storage Type", Material.BARREL, new ConfigTypeStorageType())
            .setRequired(true)
            .setDefaultValue(new StorageMysql())
            .setMeta(new DataArguments(true));
        
        // Add options
        config.addOption(configServerName);
        config.addOption(configDevMode);
        config.addOption(configCurrencies);
        config.addOption(configBaseCurrency);
        config.addOption(configWebserverHost);
        config.addOption(configWebserverPort);
        config.addOption(configSslCertificate);
        config.addOption(configSslPrivateKey);
        config.addOption(configOrderEffect);
        config.addOption(configOrderTitle);
        config.addOption(configOrderSubtitle);
        config.addOption(configStorageType);
    }
    
    @Override
    public void load() throws Exception {
        config.setDefaultOptions();
        config.save();
    	
        ConsoleHelper.showStacktraces(configDevMode.getValue(config));
    }
    
    @Override
    public void ready() throws Exception {
        Map<String, String> setupErrors = getServiceErrors();
        
        if(!setupErrors.isEmpty()) {
            ConsoleHelper.printError("========================================================");
            ConsoleHelper.printError("Please fix the setup errors below to use Commerce.");
            ConsoleHelper.printError("");
            ConsoleHelper.printError("Errors:");
            
            for(Entry<String, String> error : setupErrors.entrySet()) {
                ConsoleHelper.printError(UI.TAB + Icon.SQUARE + " " + error.getKey() + ": " + error.getValue());
            }
            
            ConsoleHelper.printError("");
            ConsoleHelper.printError("========================================================");
        }
    }

    @Override
    public Service[] registerServices() throws Exception {
        CommandService commandService = new CommandService(this);

        // Register command
        commandService.register(new CommandCommerce());

        // Get storage driver
        StorageType storageType = configStorageType.getValue(config);
        
        return new Service[] {
    		storageType,
            new CacheService(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
            new UserService(),
            new OrderService(),
            new FieldService(),
            new PaymentService(),
            commandService,
            new WebService()
        };
    }
    
    public static Commerce getInstance() {
        return instance;
    }

    public YamlConfig getConfig() {
        return config;
    }
    
    public Set<StorageType> getStorageTypes() {
    	return storageTypes;
    }

    public StorageType getStorage() {
        return getService("storage");
    }

}
