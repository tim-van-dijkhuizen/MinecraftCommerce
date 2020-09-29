package nl.timvandijkhuizen.commerce;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.commands.CommandCommerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStorageType;
import nl.timvandijkhuizen.commerce.config.types.ConfigTypeStoreCurrency;
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
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeList;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeString;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class Commerce extends PluginBase {
    
    public static final Material[] MENU_ICONS = Stream.of(Material.values()).filter(icon -> icon.isItem() && !icon.isAir()).toArray(Material[]::new);
    public static final StoreCurrency DEFAULT_CURRENCY = new StoreCurrency("USD", 1, new DecimalFormat("###,###,###.00"));
    
    private static Commerce instance;
    private Set<StorageType> storageTypes;
    private YamlConfig config;
    
    // Configuration options
    private ConfigOption<String> configServerName;
    private ConfigOption<Boolean> configDevMode;
    private ConfigOption<StorageType> configStorageType;
    private ConfigOption<List<StoreCurrency>> configCurrencies;
    private ConfigOption<StoreCurrency> configBaseCurrency;
    private ConfigOption<String> configWebserverAddress;
    private ConfigOption<Integer> configWebserverPort;

    @Override
    public void init() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Register storage types
        RegisterStorageTypesEvent event = new RegisterStorageTypesEvent();
        StorageType typeMysql = new StorageType("mysql", StorageMysql.class);
        
        event.addStorageType(typeMysql);
        
        getServer().getPluginManager().callEvent(event);
        storageTypes = event.getStorageTypes();
        
        // Setup configuration options
        config = new YamlConfig(this);
        
        // Create options
        configServerName = new ConfigOption<>("general.serverName", "Server Name", Material.PAPER, new ConfigTypeString())
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
        
        configWebserverAddress = new ConfigOption<>("general.webserverUrl", "Webserver Address", Material.COBWEB, ConfigTypes.DOMAIN)
            .setRequired(true)
            .setDefaultValue(getServer().getIp());
        
        configWebserverPort = new ConfigOption<>("general.webserverPort", "Webserver Port", Material.COBWEB, ConfigTypes.INTEGER)
            .setRequired(true)
            .setDefaultValue(8080)
            .setMeta(new DataArguments(true));
        
        configStorageType = new ConfigOption<>("storage.type", "Storage Type", Material.BARREL, new ConfigTypeStorageType(storageTypes))
            .setRequired(true)
            .setDefaultValue(typeMysql)
            .setMeta(new DataArguments(true));
        
        // Add options
        config.addOption(configServerName);
        config.addOption(configDevMode);
        config.addOption(configCurrencies);
        config.addOption(configBaseCurrency);
        config.addOption(configWebserverAddress);
        config.addOption(configWebserverPort);
        config.addOption(configStorageType);
        
        // Make sure all options exist
        config.setDefaultOptions();
        config.save();
    }
    
    @Override
    public void load() throws Exception {
        ConsoleHelper.showStacktraces(configDevMode.getValue(config));
        ConsoleHelper.printInfo("Commerce has been loaded.");
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
    public void unload() throws Exception {
        ConsoleHelper.printInfo("Commerce has been unloaded.");
    }

    @Override
    public Service[] registerServices() throws Exception {
        CommandService commandService = new CommandService(this);

        // Register command
        commandService.register(new CommandCommerce());

        // Get storage driver
        StorageType storageType = configStorageType.getValue(config);
        Class<? extends Storage> storageDriver = storageType.getDriver();
        
        return new Service[] {
            storageDriver.newInstance(),
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

    public Storage getStorage() {
        return getService("storage");
    }
    
	/**
	 * Creates a formatted URL from the specified action.
	 * Uses the configured webUrl and webPort as base 
	 * and then adds the action at the end.
	 * 
	 * 1. Replace backspaces with regular slash.
	 * 2. Remove trailing slash.
	 * 3. Add port and trailing slash.
	 * 
	 * @param action
	 * @return The formatted url
	 */
    public static String createWebUrl(String action) {
    	Commerce plugin = Commerce.getInstance();
    	YamlConfig config = plugin.getConfig();
    	
    	// Get configuration values
    	String webUrl = plugin.configWebserverAddress.getValue(config);
    	int webPort = plugin.configWebserverPort.getValue(config);
    	
    	// Return webUrl + action
    	return "http://" + webUrl + ":" + webPort + "/" + action;
    }

}
