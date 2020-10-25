package nl.timvandijkhuizen.commerce.base;

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
     * of order properties and conditions.
     * 
     * @param order
     * @return
     */
    String getValue(Order order, String property) throws Throwable;

}
