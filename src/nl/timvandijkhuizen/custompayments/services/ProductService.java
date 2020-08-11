package nl.timvandijkhuizen.custompayments.services;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.ConsoleHelper;
import nl.timvandijkhuizen.custompayments.storage.query.ProductQuery;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class ProductService implements Service {

	@Override
	public String getHandle() {
		return "products";
	}

	@Override
	public void load() throws Exception {

	}

	@Override
	public void unload() throws Exception {

	}
	
	/**
	 * Returns all products that match the specified query.
	 * 
	 * @param query
	 * @param callback
	 */
	public void getProducts(ProductQuery query, Consumer<List<Product>> callback) {
		Storage storage = CustomPayments.getInstance().getStorage();
		
		Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
			try {
				List<Product> products = storage.getProducts(null);
				MainThread.execute(() -> callback.accept(products));
			} catch(Exception e) {
				MainThread.execute(() -> callback.accept(null));
				ConsoleHelper.printError("Failed to load products: " + e.getMessage(), e);
			}
		});
	}
	
	/**
	 * Saves a product.
	 * 
	 * @param product
	 * @param callback
	 */
	public void saveProduct(Product product, Consumer<Boolean> callback) {
		Storage storage = CustomPayments.getInstance().getStorage();
		boolean isNew = product.getId() == null;
		
		// Validate the model
		if(!product.validate()) {
			callback.accept(false);
			return;
		}
		
		// Create or edit product
		Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
			try {
				if(isNew) {
					storage.createProduct(product);
				} else {
					storage.updateProduct(product);
				}
				
				MainThread.execute(() -> callback.accept(true));
			} catch(Exception e) {
				MainThread.execute(() -> callback.accept(false));
				ConsoleHelper.printError("Failed to create/update product: " + e.getMessage(), e);
			}
		});
	}
	
	public void deleteProduct(Product product, Consumer<Boolean> callback) {
		Storage storage = CustomPayments.getInstance().getStorage();
		
		// Delete product
		Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
			try {
				storage.deleteProduct(product);
				MainThread.execute(() -> callback.accept(true));
			} catch(Exception e) {
				MainThread.execute(() -> callback.accept(false));
				ConsoleHelper.printError("Failed to delete product: " + e.getMessage(), e);
			}
		});
	}

}
