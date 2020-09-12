package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.commerce.elements.Order;

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