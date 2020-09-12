package nl.timvandijkhuizen.custompayments.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.custompayments.base.FieldType;

public class RegisterFieldTypesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<FieldType<?>> types = new HashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a field type.
     * 
     * @param type
     */
    public void addType(FieldType<?> type) {
        types.add(type);
    }

    /**
     * Returns all registered field types.
     * 
     * @return
     */
    public Set<FieldType<?>> getTypes() {
        return types;
    }

}
