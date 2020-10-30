package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;

public class VariableUniqueId implements OrderVariable {

    @Override
    public String getKey() {
        return "uniqueId";
    }

    @Override
    public String getValue(Order order, LineItem item, String property) throws Throwable {
        return order.getUniqueId().toString();
    }

}