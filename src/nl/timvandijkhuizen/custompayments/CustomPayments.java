package nl.timvandijkhuizen.custompayments;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.commands.CommandCustomPayments;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.custompayments.events.RegisterStorageTypesEvent;
import nl.timvandijkhuizen.custompayments.services.CacheService;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.custompayments.services.UserService;
import nl.timvandijkhuizen.custompayments.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CustomPayments extends PluginBase {
    
    public static final Material[] MENU_ICONS = Stream.of(Material.values()).filter(icon -> icon.isItem() && !icon.isAir()).toArray(Material[]::new);
    public static final StoreCurrency DEFAULT_CURRENCY = new StoreCurrency("USD", 1, new DecimalFormat("###,###,###.00"));
    
    private static CustomPayments instance;
    private YamlConfig config;
    
    // Configuration options
    ConfigOption<Boolean> configDevMode;
    ConfigOption<String> configStorageType;
    ConfigOption<List<StoreCurrency>> configCurrencies;
    ConfigOption<StoreCurrency> configBaseCurrency;

    @Override
    public void init() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Setup config
        config = new YamlConfig(this);
        
        // Create options
        configDevMode = new ConfigOption<>("general.devMode", ConfigTypes.BOOLEAN)
            .setIcon(new ConfigIcon(Material.REDSTONE, "Dev Mode"))
            .setRequired(true)
            .setDefaultValue(false);
        
        configStorageType = new ConfigOption<>("storage.type", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.ENDER_CHEST, "Storage Type"))
            .setRequired(true)
            .setReadOnly(true);
        
        configCurrencies = new ConfigOption<>("general.currencies", new ConfigTypeList<StoreCurrency>(StoreCurrency.class, "Currencies", Material.SUNFLOWER))
            .setIcon(new ConfigIcon(Material.SUNFLOWER, "Currencies"))
            .setRequired(true)
            .setDefaultValue(Arrays.asList(DEFAULT_CURRENCY));
        
        configBaseCurrency = new ConfigOption<>("general.baseCurrency", new ConfigTypeStoreCurrency())
            .setIcon(new ConfigIcon(Material.SUNFLOWER, "Base Currency"))
            .setRequired(true)
            .setDefaultValue(DEFAULT_CURRENCY);
        
        // Add options
        config.addOption(configDevMode);
        config.addOption(configStorageType);
        config.addOption(configCurrencies);
        config.addOption(configBaseCurrency);
    }
    
    @Override
    public void load() throws Exception {
        ConsoleHelper.showStacktraces(configDevMode.getValue(config));
        ConsoleHelper.printInfo("CustomPayments has been loaded.");
    }
    
    @Override
    public void ready() throws Exception {
        Map<String, String> setupErrors = getServiceErrors();
        
        if(!setupErrors.isEmpty()) {
            ConsoleHelper.printError("========================================================");
            ConsoleHelper.printError("Please fix the setup errors below to use CustomPayments.");
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
        ConsoleHelper.printInfo("CustomPayments has been unloaded.");
    }

    @Override
    public Service[] registerServices() throws Exception {
        CommandService commandService = new CommandService(this);

        commandService.register(new CommandCustomPayments());

        return new Service[] {
            getDatabase(),
            new CacheService(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
            new UserService(),
            new OrderService(),
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
    
    public static CustomPayments getInstance() {
        return instance;
    }

    public YamlConfig getConfig() {
        return config;
    }

    public Storage getStorage() {
        return getService("storage");
    }

}
