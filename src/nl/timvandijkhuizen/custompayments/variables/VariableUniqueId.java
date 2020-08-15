package nl.timvandijkhuizen.custompayments.variables;

import nl.timvandijkhuizen.custompayments.base.CommandVariable;
import nl.timvandijkhuizen.custompayments.elements.Order;

public class VariableUniqueId implements CommandVariable {

	@Override
	public String getKey() {
		return "uuid";
	}

	@Override
	public String getValue(Order order) {
		return order.getPlayerUniqueId().toString();
	}

}