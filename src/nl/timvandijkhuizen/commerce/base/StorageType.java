package nl.timvandijkhuizen.commerce.base;

public class StorageType {

    private String handle;
    private Class<? extends Storage> driver;
    
    public StorageType(String handle, Class<? extends Storage> driver) {
        this.handle = handle;
        this.driver = driver;
    }
    
    public String getHandle() {
        return handle;
    }
    
    public Class<? extends Storage> getDriver() {
        return driver;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj != null
            && obj instanceof StorageType 
            && ((StorageType) obj).getHandle().equals(getHandle());
    }
    
}
