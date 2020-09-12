package nl.timvandijkhuizen.custompayments.fieldtypes;

import nl.timvandijkhuizen.custompayments.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeBoolean;

public class FieldTypeBoolean extends ConfigTypeBoolean implements FieldType<Boolean> {

    @Override
    public String getName() {
        return "Boolean";
    }

    @Override
    public String getHandle() {
        return "boolean";
    }

}
