package nl.timvandijkhuizen.commerce.services;

import java.util.HashSet;
import java.util.Set;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class StorageService extends BaseService {

    private Set<StorageType> storageTypes = new HashSet<>();
    private StorageType storage;
    
    @Override
    public String getHandle() {
        return "storage";
    }
    
    @Override
    public void init() throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();

        // Initialize storage
        storage = config.getOptionValue("storage.type");
        storage.init();
    }
    
    @Override
    public void load() throws Throwable {
        if(storage != null) {
            storage.load();
        }
    }
    
    @Override
    public void unload() throws Throwable {
        if(storage != null) {
            storage.unload();
        }
    }
    
    public void registerStorageType(StorageType storageType) {
        storageTypes.add(storageType);
    }
    
    public Set<StorageType> getStorageTypes() {
        return storageTypes;
    }
    
    public StorageType getStorage() {
        return storage;
    }

}
