package nl.timvandijkhuizen.custompayments.elements;

import java.util.Set;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Product extends Element {

	// Properties
	private String name;
	private String description;
	private float price;
	
	// Caches
	private Set<Command> commands;
	
	public Product(String name, String description, float price) {
		this.name = name;
		this.description = description;
		this.price = price;
	}
	
	public Product(int id, String name, String description, float price) {
		this.setId(id);
		this.name = name;
		this.description = description;
		this.price = price;
	}
	
	@Override
	public boolean validate() {
		if(name == null || name.length()  == 0) {
			addError("name", "Name is required");
			return false;
		}
		
		if(description == null || description.length()  == 0) {
			addError("description", "Description is required");
			return false;
		}
		
		if(price < 0) {
			addError("price", "Price cannot be negative");
			return false;
		}
		
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public float getPrice() {
		return price;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
	
	public Set<Command> getCommands() {
		if(commands == null) {
			// TODO: Load commands from command service
		}
		
		return commands;
	}
	
}
