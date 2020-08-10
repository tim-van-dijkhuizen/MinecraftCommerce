package nl.timvandijkhuizen.custompayments.elements;

import java.util.Set;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Product extends Element {

	// Properties
	private Material icon;
	private String name;
	private String description;
	private float price;
	
	// Caches
	private Set<Command> commands;
	
	public Product(Material icon, String name, String description, float price) {
		this.icon = icon;
		this.name = name;
		this.description = description;
		this.price = price;
	}
	
	public Product(int id, Material icon, String name, String description, float price) {
		this.setId(id);
		this.icon = icon;
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
	
	public Material getIcon() {
		return icon;
	}
	
	public void setIcon(Material icon) {
		this.icon = icon;
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
