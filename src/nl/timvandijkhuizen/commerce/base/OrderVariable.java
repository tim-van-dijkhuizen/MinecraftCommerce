package nl.timvandijkhuizen.commerce.base;

import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;

public interface OrderVariable {

    /**
     * Returns the variable key. The key is what the user adds between the
     * brackets.
     * 
     * @return
     */
    String getKey();

    /**
     * Returns an array of all available properties.
     * If a variable has any properties you will no
     * longer be allowed to only use the key.
     * 
     * @return
     */
    default public String[] getProperties() {
        return new String[] {};
    }
    
    /**
     * Returns the string value of a variable. This can be a dynamic value based
     * on order/item properties and conditions.
     * 
     * @param order
     * @param item
     * @param property
     * @return
     * @throws Throwable
     */
    String getValue(Order order, LineItem item, String property) throws Throwable;

}
