package nl.timvandijkhuizen.custompayments.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Model {

    private Map<String, Set<String>> errors = new HashMap<>();

    protected abstract boolean validate();

    /**
     * Returns whether the model is valid. All previous errors will be cleared
     * and any new errors will be added.
     * 
     * @return
     */
    public boolean isValid() {
        errors.clear();
        return validate();
    }

    /**
     * Adds a validation error to the specified attribute.
     * 
     * @param attribute
     * @param error
     */
    public void addError(String attribute, String error) {
        Set<String> attributeErrors = errors.getOrDefault(attribute, new HashSet<>());

        attributeErrors.add(error);
        errors.put(attribute, attributeErrors);
    }

    /**
     * Returns all errors.
     * 
     * @return
     */
    public Set<String> getErrors() {
        Set<String> all = new HashSet<>();

        for (Set<String> attributeErrors : errors.values()) {
            all.addAll(attributeErrors);
        }

        return all;
    }

    /**
     * Returns all errors belonging to a specific attribute.
     * 
     * @param attribute
     * @return
     */
    public Set<String> getErrors(String attribute) {
        return errors.getOrDefault(attribute, new HashSet<String>());
    }

    /**
     * Returns whether there are a validation errors.
     * 
     * @return
     */
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /**
     * Returns whether the specified attribute has any validation errors.
     * 
     * @param attribute
     * @return
     */
    public boolean hasErrors(String attribute) {
        return !getErrors(attribute).isEmpty();
    }

}
