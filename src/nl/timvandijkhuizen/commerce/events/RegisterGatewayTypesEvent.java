package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.GatewayType;

public class RegisterGatewayTypesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<GatewayType> types = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a gateway type.
     * 
     * @param type
     */
    public void addType(GatewayType type) {
        types.add(type);
    }

    /**
     * Returns all registered gateway types.
     * 
     * @return
     */
    public Set<GatewayType> getTypes() {
        return types;
    }

}