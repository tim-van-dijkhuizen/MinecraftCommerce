package nl.timvandijkhuizen.custompayments.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Model {

	private Map<String, Set<String>> errors = new HashMap<>();
	
	protected abstract boolean validate();
	
	public boolean isValid() {
		errors.clear();
		return validate();
	}
	
	public void addError(String attribute, String error) {
		Set<String> attributeErrors = errors.getOrDefault(attribute, new HashSet<>());
		
		attributeErrors.add(error);
		errors.put(attribute, attributeErrors);
	}
	
	public Set<String> getErrors() {
		Set<String> all = new HashSet<>();
		
		for(Set<String> attributeErrors : errors.values()) {
			all.addAll(attributeErrors);
		}
		
		return all;
	}
	
	public Set<String> getErrors(String attribute) {
		return errors.getOrDefault(attribute, new HashSet<String>());
	}
	
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}
	
	public boolean hasErrors(String attribute) {
		return !getErrors(attribute).isEmpty();
	}
	
}
