# API - Order variables

Using order variables you can add variables to the commands that are executed when an order is completed.

### Create the order variable
With a standard variable can return static content.

```java
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;

public class OrderVariableTest implements OrderVariable {

    @Override
    public String getKey() {
        // This value must be globally unique, only one order variable can use this.
        return "test";
    }

    @Override
    public String getValue(Order order, LineItem item, String property) throws Throwable {
        return "Hello " + order.getPlayerName() + "!";
    }

}
```

### Create the order variable with properties
With properties a variable can return dynamic content. You use the variable like this: `test:test1`.

```java
import java.util.HashMap;

import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;

public class OrderVariableTest implements OrderVariable {

    private HashMap<String, String> myData = new HashMap<>();
    
    public OrderVariableTest() {
        myData.put("test1", "Hello");
        myData.put("test2", "World");
        myData.put("test3", "!");
    }
    
    @Override
    public String getKey() {
        // This value must be globally unique, only one order variable can use this.
        return "test";
    }

    @Override
    public String[] getProperties() {
        return myData.keySet().toArray(String[]::new);
    }
    
    @Override
    public String getValue(Order order, LineItem item, String property) throws Throwable {
        return "Data value: " + myData.getOrDefault(property, "Not found");
    }

}
```

### Register the order variable
```java
import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    CommerceApi.registerOrderVariable(new OrderVariableTest());
}
```