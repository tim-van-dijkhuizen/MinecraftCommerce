package nl.timvandijkhuizen.custompayments.base;

import java.util.List;

import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.spigotutils.services.Service;

public abstract class Storage implements Service {
	
    @Override
    public String getHandle() {
    	return "storage";
    }
	
    // Categories
    public abstract List<Category> getCategories() throws Exception;
	public abstract void createCategory(Category category) throws Exception;
	public abstract void updateCategory(Category category) throws Exception;
	public abstract void deleteCategory(Category category) throws Exception;
    
	// Products
    public abstract List<Product> getProducts() throws Exception;
	public abstract void createProduct(Product product) throws Exception;
	public abstract void updateProduct(Product product) throws Exception;
	public abstract void deleteProduct(Product product) throws Exception;
    
	// Fields
	public abstract void createField(Field<?> field) throws Exception;
	public abstract void updateField(Field<?> field) throws Exception;
	public abstract void deleteField(Field<?> field) throws Exception;
	
}
