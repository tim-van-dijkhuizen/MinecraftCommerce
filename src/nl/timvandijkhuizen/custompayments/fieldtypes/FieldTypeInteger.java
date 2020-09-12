package nl.timvandijkhuizen.custompayments.fieldtypes;

import nl.timvandijkhuizen.custompayments.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeInteger;

public class FieldTypeInteger extends ConfigTypeInteger implements FieldType<Integer> {

    @Override
    public String getName() {
        return "Integer";
    }

    @Override
    public String getHandle() {
        return "integer";
    }

}
