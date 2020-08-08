package nl.timvandijkhuizen.custompayments.base;

import java.util.List;

import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.storage.query.ProductQuery;
import nl.timvandijkhuizen.spigotutils.services.Service;

public abstract class Storage implements Service {
	
    @Override
    public String getHandle() {
    	return "storage";
    }
	
	// Products
    public abstract List<Product> getProducts(ProductQuery query) throws Exception;
	public abstract void createProduct(Product product) throws Exception;
	public abstract void updateProduct(Product product) throws Exception;
	public abstract void deleteProduct(Product product) throws Exception;
    
	// Fields
	public abstract void createField(Field<?> field) throws Exception;
	public abstract void updateField(Field<?> field) throws Exception;
	public abstract void deleteField(Field<?> field) throws Exception;
	
}
