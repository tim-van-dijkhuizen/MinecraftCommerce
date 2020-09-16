package nl.timvandijkhuizen.commerce.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Model implements ModelInterface {

    public static final String SCENARIO_DEFAULT = "default";
    
    private String scenario = "default";
    private Map<String, Set<String>> errors = new HashMap<>();
    
    @Override
    public boolean isValid() {
        errors.clear();
        return validate(scenario);
    }
    
    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    @Override
    public void addError(String attribute, String error) {
        Set<String> attributeErrors = errors.getOrDefault(attribute, new HashSet<>());

        attributeErrors.add(error);
        errors.put(attribute, attributeErrors);
    }

    @Override
    public Set<String> getErrors() {
        Set<String> all = new HashSet<>();
        
        for (Set<String> attributeErrors : errors.values()) {
            all.addAll(attributeErrors);
        }

        return all;
    }

    @Override
    public Set<String> getErrors(String attribute) {
        return errors.getOrDefault(attribute, new HashSet<String>());
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
