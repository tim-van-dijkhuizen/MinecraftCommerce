package nl.timvandijkhuizen.custompayments.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.custompayments.base.CommandVariable;

public class RegisterCommandVariablesEvent extends Event {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private Set<CommandVariable> variables = new HashSet<>();
	
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
			e.printStackTrace();
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