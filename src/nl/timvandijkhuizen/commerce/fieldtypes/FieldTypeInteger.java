package nl.timvandijkhuizen.commerce.fieldtypes;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeInteger;

public class FieldTypeInteger extends ConfigTypeInteger implements FieldType<Integer> {

    @Override
    public String getHandle() {
        return "integer";
    }
    
    @Override
    public String getDisplayName() {
        return "Integer";
    }

}
