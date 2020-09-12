package nl.timvandijkhuizen.custompayments.fieldtypes;

import nl.timvandijkhuizen.custompayments.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeString;

public class FieldTypeString extends ConfigTypeString implements FieldType<String> {

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public String getHandle() {
        return "string";
    }

}
