package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class RegisterCommandVariablesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<CommandVariable> variables = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a command variable.
     * 
     * @param commandVariable
     */
    public void addVariable(Class<? extends CommandVariable> variable) {
        try {
            CommandVariable instance = variable.newInstance();
            variables.add(instance);
        } catch (InstantiationException | IllegalAccessException e) {
            ConsoleHelper.printError("Failed to register command variable.", e);
        }
    }

    /**
     * Returns all registered command variables.
     * 
     * @return
     */
    public Set<CommandVariable> getVariables() {
        return variables;
    }

}