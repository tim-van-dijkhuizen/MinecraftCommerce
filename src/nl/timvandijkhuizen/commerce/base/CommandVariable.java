package nl.timvandijkhuizen.commerce.base;

import nl.timvandijkhuizen.commerce.elements.Order;

public interface CommandVariable {

    /**
     * Returns the variable key. The key is what the user adds between the
     * brackets.
     * 
     * @return
     */
    String getKey();

    /**
     * Returns the string value of a variable. This can be a dynamic value based
     * of order properties and conditions.
     * 
     * @param order
     * @return
     */
    String getValue(Order order);

}
