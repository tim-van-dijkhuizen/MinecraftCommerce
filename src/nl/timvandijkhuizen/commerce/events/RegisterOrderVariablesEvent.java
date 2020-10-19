package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.OrderVariable;

public class RegisterOrderVariablesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<OrderVariable> variables = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a order variable.
     * 
     * @param orderVariable
     */
    public void addVariable(OrderVariable variable) {
        variables.add(variable);
    }

    /**
     * Returns all registered order variables.
     * 
     * @return
     */
    public Set<OrderVariable> getVariables() {
        return variables;
    }

}