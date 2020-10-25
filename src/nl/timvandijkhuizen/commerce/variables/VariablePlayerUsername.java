package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.Order;

public class VariablePlayerUsername implements OrderVariable {

    @Override
    public String getKey() {
        return "playerUsername";
    }

    @Override
    public String getValue(Order order, String property) throws Throwable {
        return order.getPlayerName();
    }

}
