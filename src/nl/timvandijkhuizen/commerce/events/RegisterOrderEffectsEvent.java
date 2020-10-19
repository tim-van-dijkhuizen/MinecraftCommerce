package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.OrderEffect;

public class RegisterOrderEffectsEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<OrderEffect> effects = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a order variable.
     * 
     * @param orderVariable
     */
    public void addEffect(OrderEffect effect) {
    	effects.add(effect);
    }

    /**
     * Returns all registered order effects.
     * 
     * @return
     */
    public Set<OrderEffect> getEffects() {
        return effects;
    }

}