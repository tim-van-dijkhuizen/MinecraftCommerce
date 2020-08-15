package nl.timvandijkhuizen.custompayments.base;

public abstract class Element extends Model {

    private Integer id;

    /**
     * Returns the element id. This can be null.
     * 
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the element id. Throws an exception if the id is already set.
     * 
     * @param id
     */
    public void setId(int id) {
        if (this.id != null) {
            throw new RuntimeException("Id already set");
        }

        this.id = id;
    }

}