package nl.timvandijkhuizen.custompayments.base;

import nl.timvandijkhuizen.custompayments.elements.Order;

public interface CommandVariable {

	String getKey();
	String getValue(Order order);
	
}
