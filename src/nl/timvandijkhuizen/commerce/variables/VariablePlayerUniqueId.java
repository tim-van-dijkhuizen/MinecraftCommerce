package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.commerce.elements.Order;

public class VariablePlayerUniqueId implements CommandVariable {

    @Override
    public String getKey() {
        return "playerUniqueId";
    }

    @Override
    public String getValue(Order order) {
        return order.getPlayerUniqueId().toString();
    }

}