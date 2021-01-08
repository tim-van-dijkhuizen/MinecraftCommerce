package nl.timvandijkhuizen.commerce.base;

import java.util.Set;
import java.util.UUID;

import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.elements.Transaction;

public interface StorageType {

    /**
     * Returns the handle. This value must be unique.
     * 
     * @return
     */
    public String getHandle();

    /**
     * Returns the display name used in menu's.
     * 
     * @return
     */
    public String getDisplayName();
    
    /**
     * Called when the storage type is created.
     * 
     * @throws Throwable
     */
    public void init() throws Throwable;

    /**
     * Called when the storage type is loaded.
     * 
     * @throws Throwable
     */
    public void load() throws Throwable;

    /**
     * Called when the storage type is unloaded.
     * 
     * @throws Throwable
     */
    public void unload() throws Throwable;

    /**
     * Returns all categories.
     * 
     * @return
     * @throws Throwable
     */
    Set<Category> getCategories() throws Throwable;

    /**
     * Creates a category.
     * 
     * @param category
     * @throws Throwable
     */
    void createCategory(Category category) throws Throwable;

    /**
     * Updates a category.
     * 
     * @param category
     * @throws Throwable
     */
    void updateCategory(Category category) throws Throwable;

    /**
     * Deletes a category.
     * 
     * @param category
     * @throws Throwable
     */
    void deleteCategory(Category category) throws Throwable;

    /**
     * Returns products that belong to the specified category or all of them.
     * 
     * @param category The category to get products for or null.
     * @return A set of products.
     * @throws Throwable
     */
    Set<Product> getProducts(Category category) throws Throwable;

    /**
     * Returns a product by its id.
     * 
     * @param id The product id.
     * @return The product.
     * @throws Throwable
     */
    Product getProductById(int id) throws Throwable;
    
    /**
     * Creates a product.
     * 
     * @param product
     * @throws Throwable
     */
    void createProduct(Product product) throws Throwable;

    /**
     * Updates a product.
     * 
     * @param product
     * @throws Throwable
     */
    void updateProduct(Product product) throws Throwable;

    /**
     * Deletes a product.
     * 
     * @param product
     * @throws Throwable
     */
    void deleteProduct(Product product) throws Throwable;

    /**
     * Returns all commands belonging to the specified product.
     * 
     * @param productId
     * @return
     * @throws Throwable
     */
    Set<Command> getCommandsByProductId(int productId) throws Throwable;

    /**
     * Creates a command.
     * 
     * @param command
     * @throws Throwable
     */
    void createCommand(Command command) throws Throwable;

    /**
     * Deletes a command.
     * 
     * @param command
     * @throws Throwable
     */
    void deleteCommand(Command command) throws Throwable;

    /**
     * Returns all fields.
     * 
     * @return
     * @throws Throwable
     */
    Set<Field> getFields() throws Throwable;

    /**
     * Creates a field
     * 
     * @param field
     * @throws Throwable
     */
    void createField(Field field) throws Throwable;

    /**
     * Updates a field
     * 
     * @param field
     * @throws Throwable
     */
    void updateField(Field field) throws Throwable;

    /**
     * Deletes a field.
     * 
     * @param field
     * @throws Throwable
     */
    void deleteField(Field field) throws Throwable;

    /**
     * Returns all gateways.
     * 
     * @return
     * @throws Throwable
     */
    Set<Gateway> getGateways() throws Throwable;

    /**
     * Creates a gateway.
     * 
     * @param gateway
     * @throws Throwable
     */
    void createGateway(Gateway gateway) throws Throwable;

    /**
     * Updates a gateway.
     * 
     * @param gateway
     * @throws Throwable
     */
    void updateGateway(Gateway gateway) throws Throwable;

    /**
     * Deletes a gateway.
     * 
     * @param gateway
     * @throws Throwable
     */
    void deleteGateway(Gateway gateway) throws Throwable;
    
    /**
     * Returns the UserPreferences of the specified user.
     * 
     * @param playerUniqueId
     * @return
     * @throws Throwable
     */
    UserPreferences getUserPreferences(UUID playerUniqueId) throws Throwable;

    /**
     * Saves the UserPreferences for the specified user.
     * 
     * @param playerUniqueId
     * @param preferences
     * @throws Throwable
     */
    void saveUserPreferences(UUID playerUniqueId, UserPreferences preferences) throws Throwable;
    
    /**
     * Returns the cart of the specified user.
     * 
     * @param playerUniqueId
     * @return
     * @throws Throwable
     */
    Order getCart(UUID playerUniqueId) throws Throwable;

    /**
     * Returns an order by its id.
     * 
     * @param id
     * @return
     * @throws Throwable
     */
    Order getOrderById(int id) throws Throwable;
    
    /**
     * Returns an order by its UUID.
     * 
     * @param uniqueId
     * @return
     * @throws Throwable
     */
    Order getOrderByUniqueId(UUID uniqueId) throws Throwable;

    /**
     * Returns all orders.
     * 
     * @return
     * @throws Throwable
     */
    Set<Order> getOrders() throws Throwable;

    /**
     * Returns all orders that belong to a player.
     * 
     * @param playerUniqueId
     * @return
     * @throws Throwable
     */
    Set<Order> getOrdersByPlayer(UUID playerUniqueId) throws Throwable;

    /**
     * Creates an order.
     * 
     * @param order
     * @throws Throwable
     */
    void createOrder(Order order) throws Throwable;

    /**
     * Updates an order.
     * 
     * @param order
     * @throws Throwable
     */
    void updateOrder(Order order) throws Throwable;

    /**
     * Deletes an order.
     * 
     * @param order
     * @throws Throwable
     */
    void deleteOrder(Order order) throws Throwable;

    /**
     * Returns all LineItems belonging to the specified order.
     * 
     * @param orderId
     * @return
     * @throws Throwable
     */
    Set<LineItem> getLineItemsByOrderId(int orderId) throws Throwable;

    /**
     * Creates a LineItem.
     * 
     * @param lineItem
     * @throws Throwable
     */
    void createLineItem(LineItem lineItem) throws Throwable;

    /**
     * Updates a LineItem.
     * 
     * @param lineItem
     * @throws Throwable
     */
    void updateLineItem(LineItem lineItem) throws Throwable;

    /**
     * Deletes a LineItem.
     * 
     * @param lineItem
     * @throws Throwable
     */
    void deleteLineItem(LineItem lineItem) throws Throwable;

    /**
     * Returns all Transactions belonging to the specified order.
     * 
     * @param orderId
     * @return
     * @throws Throwable
     */
    public Set<Transaction> getTransactionsByOrderId(int orderId) throws Throwable;
    
    /**
     * Creates a Transaction.
     * 
     * @param transaction
     * @throws Throwable
     */
    void createTransaction(Transaction transaction) throws Throwable;

}
