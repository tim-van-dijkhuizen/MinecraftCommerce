package nl.timvandijkhuizen.custompayments.elements;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Command extends Element {

	private int productId;
	private String command;
	
	public Command(int productId, String command) {
		this.productId = productId;
		this.command = command;
	}
	
	public Command(int id, int productId, String command) {
		this.setId(id);
		this.productId = productId;
		this.command = command;
	}
	
	@Override
	public boolean validate() {
		return false;
	}
	
	public int getProductId() {
		return productId;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}

}
