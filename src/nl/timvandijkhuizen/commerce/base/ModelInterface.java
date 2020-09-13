package nl.timvandijkhuizen.commerce.base;

import java.util.Set;

public interface ModelInterface {

    boolean validate(String scenario);

    /**
     * Returns whether the model is valid. All previous errors will be cleared
     * and any new errors will be added.
     * 
     * @return
     */
    boolean isValid();

    /**
     * Adds a validation error to the specified attribute.
     * 
     * @param attribute
     * @param error
     */
    void addError(String attribute, String error);

    /**
     * Returns all errors.
     * 
     * @return
     */
    Set<String> getErrors();

    /**
     * Returns all errors belonging to a specific attribute.
     * 
     * @param attribute
     * @return
     */
    Set<String> getErrors(String attribute);

    /**
     * Returns whether there are a validation errors.
     * 
     * @return
     */
    boolean hasErrors();

    /**
     * Returns whether the specified attribute has any validation errors.
     * 
     * @param attribute
     * @return
     */
    boolean hasErrors(String attribute);
    
}
