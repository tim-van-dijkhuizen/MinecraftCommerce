package nl.timvandijkhuizen.custompayments.gateways;

import nl.timvandijkhuizen.custompayments.base.Gateway;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Transaction;
import nl.timvandijkhuizen.custompayments.elements.TransactionResponse;

public class GatewayMollie implements Gateway {

	@Override
	public String getHandle() {
		return "mollie";
	}

	@Override
	public boolean supportsCallback() {
		return true;
	}

	@Override
	public Transaction createTransaction(Order order) {
		return null;
	}

	@Override
	public void processCallback(TransactionResponse response) {
		
	}

}
