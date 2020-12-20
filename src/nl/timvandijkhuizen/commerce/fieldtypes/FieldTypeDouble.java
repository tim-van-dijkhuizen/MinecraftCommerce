package nl.timvandijkhuizen.commerce.fieldtypes;

import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeDouble;

public class FieldTypeDouble extends ConfigTypeDouble implements FieldType<Double> {

    @Override
    public String getHandle() {
        return "double";
    }

    @Override
    public String getDisplayName() {
        return "Double";
    }

}
