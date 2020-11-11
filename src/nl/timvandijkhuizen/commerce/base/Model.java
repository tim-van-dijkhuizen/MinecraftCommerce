package nl.timvandijkhuizen.commerce.base;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class Model implements ModelInterface {

    private Map<String, Set<String>> errors = new HashMap<>();

    /**
     * Validates the model for the specified scenario.
     * This method should add errors using addError()
     * and return false.
     * 
     * @param scenario
     * @return
     */
    protected abstract boolean validate(String scenario);

    @Override
    public boolean isValid() {
        return isValid("default");
    }

    @Override
    public boolean isValid(String scenario) {
        errors.clear();
        return validate(scenario);
    }

    @Override
    public void addError(String attribute, String error) {
        Set<String> attributeErrors = errors.getOrDefault(attribute, new LinkedHashSet<>());

        attributeErrors.add(error);
        errors.put(attribute, attributeErrors);
    }

    @Override
    public Set<String> getErrors() {
        Set<String> all = new LinkedHashSet<>();

        for (Set<String> attributeErrors : errors.values()) {
            all.addAll(attributeErrors);
        }

        return all;
    }

    @Override
    public Set<String> getErrors(String attribute) {
        return errors.getOrDefault(attribute, new LinkedHashSet<String>());
    }

    @Override
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    @Override
    public boolean hasErrors(String attribute) {
        return !getErrors(attribute).isEmpty();
    }

}
