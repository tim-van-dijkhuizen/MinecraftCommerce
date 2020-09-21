package nl.timvandijkhuizen.commerce;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.bukkit.Material;

import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.commands.CommandCommerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
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
import nl.timvandijkhuizen.commerce.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeInteger;
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
    private YamlConfig config;
    
    // Configuration options
    private ConfigOption<String> configServerName;
    private ConfigOption<Boolean> configDevMode;
    private ConfigOption<String> configStorageType;
    private ConfigOption<List<StoreCurrency>> configCurrencies;
    private ConfigOption<StoreCurrency> configBaseCurrency;
    private ConfigOption<String> configWebAddress;
    private ConfigOption<Integer> configWebPort;

    @Override
    public void init() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Setup config
        config = new YamlConfig(this);
        
        // Create options
        configServerName = new ConfigOption<>("general.serverName", "Server Name", Material.PAPER, new ConfigTypeString())
            .setRequired(true)
            .setDefaultValue("Minecraft Commerce");
        
        configDevMode = new ConfigOption<>("general.devMode", "Dev Mode", Material.REDSTONE, ConfigTypes.BOOLEAN)
            .setRequired(true)
            .setDefaultValue(false);
        
        configStorageType = new ConfigOption<>("storage.type", "Storage Type", Material.ENDER_CHEST, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("mysql")
            .setMeta(new DataArguments(true));
        
        configCurrencies = new ConfigOption<>("general.currencies", "Currencies", Material.SUNFLOWER, new ConfigTypeList<StoreCurrency>(StoreCurrency.class, "Currencies", Material.SUNFLOWER))
            .setRequired(true)
            .setDefaultValue(Arrays.asList(DEFAULT_CURRENCY));
        
        configBaseCurrency = new ConfigOption<>("general.baseCurrency", "Base Currency", Material.SUNFLOWER, new ConfigTypeStoreCurrency())
            .setRequired(true)
            .setDefaultValue(DEFAULT_CURRENCY);
        
        configWebAddress = new ConfigOption<>("general.webUrl", "Web Address", Material.FISHING_ROD, ConfigTypes.DOMAIN)
            .setRequired(true)
            .setDefaultValue(getServer().getIp());
        
        configWebPort = new ConfigOption<>("general.webPort", "Web Port", Material.FISHING_ROD, ConfigTypes.INTEGER)
            .setRequired(true)
            .setDefaultValue(8080);
        
        // Add options
        config.addOption(configServerName);
        config.addOption(configDevMode);
        config.addOption(configStorageType);
        config.addOption(configCurrencies);
        config.addOption(configBaseCurrency);
        config.addOption(configWebAddress);
        config.addOption(configWebPort);
        
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

        commandService.register(new CommandCommerce());

        return new Service[] {
            getDatabase(),
            new CacheService(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
            new UserService(),
            new OrderService(),
            new FieldService(),
            new PaymentService(),
            commandService
        };
    }

    private Storage getDatabase() throws Exception {
        RegisterStorageTypesEvent event = new RegisterStorageTypesEvent();

        event.addStorageType("mysql", StorageMysql.class);
        getServer().getPluginManager().callEvent(event);

        // Register chosen storage type
        Map<String, Class<? extends Storage>> storageTypes = event.getStorageTypes();
        ConfigOption<String> optionType = config.getOption("storage.type");
        String storageTypeKey = optionType.getValue(config);

        if (storageTypes.containsKey(storageTypeKey)) {
            Class<? extends Storage> storageClass = storageTypes.get(storageTypeKey);
            return storageClass.newInstance();
        } else {
            throw new RuntimeException("Unsupported database driver");
        }
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
    	String webUrl = plugin.configWebAddress.getValue(config);
    	int webPort = plugin.configWebPort.getValue(config);
    	
    	// Return webUrl + action
    	return "http://" + webUrl + ":" + webPort + "/" + action;
    }

}
