package nl.timvandijkhuizen.custompayments;

import java.util.HashMap;

import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.commands.CommandCustomPayments;
import nl.timvandijkhuizen.custompayments.events.RegisterStorageTypesEvent;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.custompayments.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigConverter;
import nl.timvandijkhuizen.spigotutils.config.ConfigurationException;
import nl.timvandijkhuizen.spigotutils.config.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class CustomPayments extends PluginBase {
	
	private static CustomPayments instance;
	private YamlConfig config;

	@Override
	public void load() throws Exception {
		instance = this;
		config = new YamlConfig(this);
		MainThread.setPlugin(this);
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
			new ProductService(),
			commandService
		};
	}

	@Override
	public ConfigConverter<?>[] registerConfigConverters() throws Exception {
		return new ConfigConverter<?>[] {};
	}
	
	private Storage getDatabase() throws Exception {
		HashMap<String, Class<? extends Storage>> storageTypes = new HashMap<>();
		RegisterStorageTypesEvent storageTypeEvent = new RegisterStorageTypesEvent(storageTypes);
		
		storageTypeEvent.addStorageType("mysql", StorageMysql.class);
		getServer().getPluginManager().callEvent(storageTypeEvent);
		
		// Register chosen storage type
		String storageTypeKey = config.getString("storage.type");
		
		if(storageTypes.containsKey(storageTypeKey)) {
			Class<? extends Storage> storageClass = storageTypes.get(storageTypeKey);
			return storageClass.newInstance();
		} else {
			throw new ConfigurationException("Unsupported database driver");
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
