package nl.timvandijkhuizen.custompayments.variables;

import nl.timvandijkhuizen.custompayments.base.CommandVariable;
import nl.timvandijkhuizen.custompayments.elements.Order;

public class VariableUsername implements CommandVariable {

	@Override
	public String getKey() {
		return "username";
	}

	@Override
	public String getValue(Order order) {
		return order.getPlayerName();
	}

}
