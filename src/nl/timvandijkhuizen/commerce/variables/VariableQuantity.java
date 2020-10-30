package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;

public class VariableQuantity implements OrderVariable {

    @Override
    public String getKey() {
        return "quantity";
    }

    @Override
    public String getValue(Order order, LineItem item, String property) throws Throwable {
        return String.valueOf(item.getQuantity());
    }

}
