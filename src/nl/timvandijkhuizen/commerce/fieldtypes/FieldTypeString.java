package nl.timvandijkhuizen.commerce.fieldtypes;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeString;

public class FieldTypeString extends ConfigTypeString implements FieldType<String> {

    @Override
    public String getHandle() {
        return "string";
    }
    
    @Override
    public String getDisplayName() {
        return "String";
    }

}
