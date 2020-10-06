package nl.timvandijkhuizen.commerce.elements;

import nl.timvandijkhuizen.commerce.base.Element;

public class Command extends Element {

    private Integer productId;
    private String command;

    public Command(String command) {
        this.command = command;
    }

    public Command(int id, int productId, String command) {
        this.setId(id);
        this.productId = productId;
        this.command = command;
    }

    @Override
    protected boolean validate(String scenario) {
        return true;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
