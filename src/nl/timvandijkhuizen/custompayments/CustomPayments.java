package nl.timvandijkhuizen.custompayments;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.commands.CommandCustomPayments;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.types.ConfigTypeStoreCurrency;
import nl.timvandijkhuizen.custompayments.events.RegisterStorageTypesEvent;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.custompayments.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeList;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class CustomPayments extends PluginBase {
    
    public static final StoreCurrency DEFAULT_CURRENCY = new StoreCurrency("USD", 1, new DecimalFormat("###,###,###.00"));
    
    private static CustomPayments instance;
    private YamlConfig config;

    @Override
    public void load() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Setup config
        config = new YamlConfig(this);
        
        // Create options
        ConfigOption<String> optionStorageType = new ConfigOption<>("storage.type", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.ENDER_CHEST, "Storage Type"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<List<StoreCurrency>> optionCurrencies = new ConfigOption<>("general.currencies", new ConfigTypeList<StoreCurrency>(StoreCurrency.class, "Currencies", Material.SUNFLOWER))
                .setIcon(new ConfigIcon(Material.SUNFLOWER, "Currencies"))
                .setRequired(true)
                .setDefaultValue(Arrays.asList(DEFAULT_CURRENCY));
        
        ConfigOption<StoreCurrency> optionBaseCurrency = new ConfigOption<>("general.baseCurrency", new ConfigTypeStoreCurrency())
            .setIcon(new ConfigIcon(Material.SUNFLOWER, "Base Currency"))
            .setRequired(true)
            .setDefaultValue(DEFAULT_CURRENCY);
        
        // Add options
        config.addOption(optionStorageType);
        config.addOption(optionCurrencies);
        config.addOption(optionBaseCurrency);
    }

    @Override
    public void unload() throws Exception {

    }

    @Override
    public Service[] registerServices() throws Exception {
        CommandService commandService = new CommandService(this);

        commandService.register(new CommandCustomPayments());

        return new Service[] {
            getDatabase(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
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
