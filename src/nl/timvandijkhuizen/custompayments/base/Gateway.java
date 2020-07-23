package nl.timvandijkhuizen.custompayments.base;

import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Transaction;
import nl.timvandijkhuizen.custompayments.elements.TransactionResponse;

public interface Gateway {

	public String getHandle();
	public boolean supportsCallback();
	
	// Actions
	public Transaction createTransaction(Order order);
	public void processCallback(TransactionResponse response);
	
}
