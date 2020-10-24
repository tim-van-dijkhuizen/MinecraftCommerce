package nl.timvandijkhuizen.commerce.fieldtypes;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeBoolean;

public class FieldTypeBoolean extends ConfigTypeBoolean implements FieldType<Boolean> {

    @Override
    public String getHandle() {
        return "boolean";
    }
    
    @Override
    public String getDisplayName() {
        return "Boolean";
    }

}
