package nl.timvandijkhuizen.custompayments;

import java.util.Map;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.commands.CommandCustomPayments;
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
import nl.timvandijkhuizen.spigotutils.config.ConfigurationException;
import nl.timvandijkhuizen.spigotutils.config.PluginConfiguration;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class CustomPayments extends PluginBase {
    
    private static CustomPayments instance;
    private PluginConfiguration config;

    @Override
    public void load() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Setup config
        config = new PluginConfiguration(this);
        
        config.addOption(new ConfigOption<>("storage.type", ConfigTypes.STRING).setIcon(new ConfigIcon(Material.CHEST, "Storage Type")).setReadOnly(true));
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
        ConfigOption<?> optionType = config.getOption("storage.type");
        String storageTypeKey = optionType.getValueLore(config);

        if (storageTypes.containsKey(storageTypeKey)) {
            Class<? extends Storage> storageClass = storageTypes.get(storageTypeKey);
            return storageClass.newInstance();
        } else {
            throw new ConfigurationException("Unsupported database driver");
        }
    }

    public static CustomPayments getInstance() {
        return instance;
    }

    public PluginConfiguration getConfig() {
        return config;
    }

    public Storage getStorage() {
        return getService("storage");
    }

}
