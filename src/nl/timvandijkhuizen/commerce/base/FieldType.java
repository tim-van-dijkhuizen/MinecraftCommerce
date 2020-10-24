package nl.timvandijkhuizen.commerce.base;

import nl.timvandijkhuizen.spigotutils.config.ConfigType;

public interface FieldType<T> extends ConfigType<T> {

    /**
     * Returns the handle. This value must be unique.
     * 
     * @return
     */
    public String getHandle();
    
    /**
     * Returns the display name.
     * 
     * @return
     */
    public String getDisplayName();

}
