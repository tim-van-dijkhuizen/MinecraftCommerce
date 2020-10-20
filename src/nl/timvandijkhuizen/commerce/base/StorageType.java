package nl.timvandijkhuizen.commerce.base;

import java.util.Set;
import java.util.UUID;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.spigotutils.services.Service;

public interface StorageType extends Service {

	default public String getHandle() {
		return "storage";
	}
	
    public String getType();
    public XMaterial getIcon();
    public String getName();
    
    /**
     * Returns all categories.
     * 
     * @return
     * @throws Exception
     */
    Set<Category> getCategories() throws Exception;

    /**
     * Creates a category.
     * 
     * @param category
     * @throws Exception
     */
    void createCategory(Category category) throws Exception;

    /**
     * Updates a category.
     * 
     * @param category
     * @throws Exception
     */
    void updateCategory(Category category) throws Exception;

    /**
     * Deletes a category.
     * 
     * @param category
     * @throws Exception
     */
    void deleteCategory(Category category) throws Exception;
    
    /**
     * Returns all products for the specified category.
     * All products will be returned if the category is null.
     * 
     * @return
     * @throws Exception
     */
    Set<Product> getProducts(Category category) throws Exception;

    /**
     * Creates a product.
     * 
     * @param product
     * @throws Exception
     */
    void createProduct(Product product) throws Exception;

    /**
     * Updates a product.
     * 
     * @param product
     * @throws Exception
     */
    void updateProduct(Product product) throws Exception;

    /**
     * Deletes a product.
     * 
     * @param product
     * @throws Exception
     */
    void deleteProduct(Product product) throws Exception;

    /**
     * Returns all commands belonging to the specified product.
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    Set<Command> getCommandsByProductId(int productId) throws Exception;

    /**
     * Creates a command.
     * 
     * @param command
     * @throws Exception
     */
    void createCommand(Command command) throws Exception;

    /**
     * Deletes a command.
     * 
     * @param command
     * @throws Exception
     */
    void deleteCommand(Command command) throws Exception;

    /**
     * Returns all fields.
     * 
     * @return
     * @throws Exception
     */
    Set<Field> getFields() throws Exception;
    
    /**
     * Creates a field
     * 
     * @param field
     * @throws Exception
     */
    void createField(Field field) throws Exception;

    /**
     * Updates a field
     * 
     * @param field
     * @throws Exception
     */
    void updateField(Field field) throws Exception;

    /**
     * Deletes a field.
     * 
     * @param field
     * @throws Exception
     */
    void deleteField(Field field) throws Exception;
    
    /**
     * Returns the cart of the specified user.
     * 
     * @return
     * @throws Exception
     */
    Order getCart(UUID playerUniqueId) throws Exception;
    
    /**
     * Returns an order by its UUID.
     * 
     * @return
     * @throws Exception
     */
    Order getOrderByUniqueId(UUID uniqueId) throws Exception;
    
    /**
     * Returns all orders.
     * 
     * @return
     * @throws Exception
     */
    Set<Order> getOrders() throws Exception;

    /**
     * Returns all orders that belong to a player.
     * 
     * @param playerUniqueId
     * @return
     * @throws Exception
     */
    Set<Order> getOrdersByPlayer(UUID playerUniqueId) throws Exception;
    
    /**
     * Creates an order.
     * 
     * @param order
     * @throws Exception
     */
    void createOrder(Order order) throws Exception;

    /**
     * Updates an order.
     * 
     * @param order
     * @throws Exception
     */
    void updateOrder(Order order) throws Exception;
    
    /**
     * Completes an order.
     * 
     * @param order
     * @throws Exception
     */
    void completeOrder(Order order) throws Exception;

    /**
     * Deletes an order.
     * 
     * @param order
     * @throws Exception
     */
    void deleteOrder(Order order) throws Exception;
    
    /**
     * Returns all LineItems belonging to the specified order.
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    Set<LineItem> getLineItemsByOrderId(int orderId) throws Exception;

    /**
     * Creates a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    void createLineItem(LineItem lineItem) throws Exception;

    /**
     * Updates a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    void updateLineItem(LineItem lineItem) throws Exception;
    
    /**
     * Deletes a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    void deleteLineItem(LineItem lineItem) throws Exception;
    
    /**
     * Returns all gateways.
     * 
     * @return
     * @throws Exception
     */
    Set<Gateway> getGateways() throws Exception;
    
    /**
     * Creates a gateway.
     * 
     * @param order
     * @throws Exception
     */
    void createGateway(Gateway gateway) throws Exception;

    /**
     * Updates a gateway.
     * 
     * @param order
     * @throws Exception
     */
    void updateGateway(Gateway gateway) throws Exception;

    /**
     * Deletes a gateway.
     * 
     * @param order
     * @throws Exception
     */
    void deleteGateway(Gateway gateway) throws Exception;
    
    /**
     * Returns the UserPreferences of the specified user.
     * 
     * @param playerUniqueId
     * @return
     * @throws Exception
     */
    UserPreferences getUserPreferences(UUID playerUniqueId) throws Exception;

    /**
     * Saves the UserPreferences for the specified user.
     * 
     * @param playerUniqueId
     * @param preferences
     * @throws Exception
     */
    void saveUserPreferences(UUID playerUniqueId, UserPreferences preferences) throws Exception;
    
}
