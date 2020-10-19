package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.StorageType;

public class RegisterStorageTypesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<StorageType> types = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    
    public void addStorageType(StorageType type) {
    	types.add(type);
    }

    public Set<StorageType> getStorageTypes() {
        return types;
    }

}
