package nl.timvandijkhuizen.commerce.base;

public interface ElementInterface extends ModelInterface {

    /**
     * Returns the element id. This can be null.
     * 
     * @return
     */
    Integer getId();

    /**
     * Sets the element id. Throws an exception if the id is already set.
     * 
     * @param id
     */
    void setId(int id);

}
