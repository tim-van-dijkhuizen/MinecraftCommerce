package nl.timvandijkhuizen.custompayments.base;

import java.util.List;
import java.util.UUID;

import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.spigotutils.services.Service;

public abstract class Storage implements Service {

    @Override
    public String getHandle() {
        return "storage";
    }

    /**
     * Returns all categories.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Category> getCategories() throws Exception;

    /**
     * Creates a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void createCategory(Category category) throws Exception;

    /**
     * Updates a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void updateCategory(Category category) throws Exception;

    /**
     * Deletes a category.
     * 
     * @param category
     * @throws Exception
     */
    public abstract void deleteCategory(Category category) throws Exception;
    
    /**
     * Returns all products for the specified category.
     * All products will be returned if the category is null.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Product> getProducts(Category category) throws Exception;

    /**
     * Creates a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void createProduct(Product product) throws Exception;

    /**
     * Updates a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void updateProduct(Product product) throws Exception;

    /**
     * Deletes a product.
     * 
     * @param product
     * @throws Exception
     */
    public abstract void deleteProduct(Product product) throws Exception;

    /**
     * Returns all commands belonging to the specified product.
     * 
     * @param productId
     * @return
     * @throws Exception
     */
    public abstract List<Command> getCommandsByProductId(int productId) throws Exception;

    /**
     * Creates a command.
     * 
     * @param command
     * @throws Exception
     */
    public abstract void createCommand(Command command) throws Exception;

    /**
     * Deletes a command.
     * 
     * @param command
     * @throws Exception
     */
    public abstract void deleteCommand(Command command) throws Exception;

    /**
     * Creates a field
     * 
     * @param field
     * @throws Exception
     */
    public abstract void createField(Field<?> field) throws Exception;

    /**
     * Updates a field
     * 
     * @param field
     * @throws Exception
     */
    public abstract void updateField(Field<?> field) throws Exception;

    /**
     * Deletes a field.
     * 
     * @param field
     * @throws Exception
     */
    public abstract void deleteField(Field<?> field) throws Exception;
    
    /**
     * Returns the cart of the specified user.
     * 
     * @return
     * @throws Exception
     */
    public abstract Order getCart(UUID uuid) throws Exception;
    
    /**
     * Returns all orders.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Order> getOrders() throws Exception;

    /**
     * Creates an order.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void createOrder(Order order) throws Exception;

    /**
     * Updates an order.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void updateOrder(Order order) throws Exception;

    /**
     * Deletes an order.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void deleteOrder(Order order) throws Exception;
    
    /**
     * Returns all LineItems belonging to the specified order.
     * 
     * @param orderId
     * @return
     * @throws Exception
     */
    public abstract List<LineItem> getLineItemsByOrderId(int orderId) throws Exception;

    /**
     * Creates a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    public abstract void createLineItem(LineItem lineItem) throws Exception;

    /**
     * Updates a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    public abstract void updateLineItem(LineItem lineItem) throws Exception;
    
    /**
     * Deletes a LineItem.
     * 
     * @param lineItem
     * @throws Exception
     */
    public abstract void deleteLineItem(LineItem lineItem) throws Exception;
    
    /**
     * Returns all gateways.
     * 
     * @return
     * @throws Exception
     */
    public abstract List<Gateway> getGateways() throws Exception;

    /**
     * Creates a gateway.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void createGateway(Gateway gateway) throws Exception;

    /**
     * Updates a gateway.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void updateGateway(Gateway gateway) throws Exception;

    /**
     * Deletes a gateway.
     * 
     * @param order
     * @throws Exception
     */
    public abstract void deleteGateway(Gateway gateway) throws Exception;
    
    /**
     * Returns the UserPreferences of the specified user.
     * 
     * @param uuid
     * @return
     * @throws Exception
     */
    public abstract UserPreferences getUserPreferences(UUID uuid) throws Exception;

    /**
     * Saves the UserPreferences for the specified user.
     * 
     * @param uuid
     * @param preferences
     * @throws Exception
     */
    public abstract void saveUserPreferences(UUID uuid, UserPreferences preferences) throws Exception;
    
}
