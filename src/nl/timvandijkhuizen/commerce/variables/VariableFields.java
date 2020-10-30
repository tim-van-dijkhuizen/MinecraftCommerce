package nl.timvandijkhuizen.commerce.variables;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;

public class VariableFields implements OrderVariable {

    @Override
    public String getKey() {
        return "field";
    }

    @Override
    public String[] getProperties() {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        
        return fieldService.getOptions().stream()
            .map(option -> option.getPath())
            .toArray(String[]::new);
    }
    
    @Override
    public String getValue(Order order, LineItem item, String property) throws Throwable {
        OrderFieldData fieldData = order.getFieldData();
        ConfigOption<?> option = fieldData.getOption(property);
        
        if(option == null) {
            throw new Exception("Field data option with path " + property + " does not exist.");
        }
        
        return option.getRawValue(fieldData);
    }
    
    
    
}
