package nl.timvandijkhuizen.custompayments.elements;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Category extends Element {

    private String name;
    private String description;

    public Category() {
        this.name = "";
        this.description = "";
    }

    public Category(int id, String name, String description) {
        this.setId(id);
        this.name = name;
        this.description = description;
    }

    @Override
    protected boolean validate() {
        if (name.length() == 0) {
            addError("name", "Name is required");
            return false;
        }

        if (name.length() > 40) {
            addError("name", "Name cannot be longer than 40 characters");
            return false;
        }

        if (description.length() == 0) {
            addError("description", "Description is required");
            return false;
        }

        if (description.length() > 500) {
            addError("description", "Description cannot be longer than 500 characters");
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

}
