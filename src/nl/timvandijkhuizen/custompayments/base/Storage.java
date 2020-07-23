package nl.timvandijkhuizen.custompayments.base;

import nl.timvandijkhuizen.spigotutils.services.Service;

public abstract class Storage implements Service {
	
    @Override
    public String getHandle() {
    	return "storage";
    }
	
	// Fields
	public abstract <T> boolean createField(Field<T> field);
	public abstract <T> boolean editField(Field<T> field);
	public abstract <T> boolean deleteField(Field<T> field);
	
}
