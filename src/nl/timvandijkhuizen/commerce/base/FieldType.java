package nl.timvandijkhuizen.commerce.base;

import nl.timvandijkhuizen.spigotutils.config.ConfigType;

public interface FieldType<T> extends ConfigType<T> {

    /**
     * Returns the name of the field type.
     * 
     * @return
     */
    public String getName();
    
    /**
     * Returns the handle of the field type.
     * 
     * @return
     */
    public String getHandle();
    
}
