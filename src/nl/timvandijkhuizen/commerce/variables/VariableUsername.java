package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.commerce.elements.Order;

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
