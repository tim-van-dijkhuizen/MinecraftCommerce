package nl.timvandijkhuizen.custompayments.base;

import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Transaction;
import nl.timvandijkhuizen.custompayments.elements.TransactionResponse;

public interface Gateway {

    /**
     * Returns the handle of the gateway.
     * 
     * @return
     */
    public String getHandle();

    /**
     * Creates a transaction.
     * 
     * @param order
     * @return
     */
    public Transaction createTransaction(Order order);

    /**
     * Returns whether the gateway supports callback's.
     * 
     * @return
     */
    public boolean supportsCallback();

    /**
     * Processes a transaction response.
     * 
     * @param response
     */
    public void processCallback(TransactionResponse response);

}
