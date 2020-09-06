package nl.timvandijkhuizen.custompayments.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Field;
import nl.timvandijkhuizen.custompayments.base.GatewayType;
import nl.timvandijkhuizen.custompayments.base.ProductSnapshot;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.sources.GatewayConfig;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.DbHelper;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class StorageMysql extends Storage {

    private HikariConfig dbConfig = new HikariConfig();
    private HikariDataSource dbSource;

    @Override
    public void load() throws Exception {
        YamlConfig config = CustomPayments.getInstance().getConfig();

        // Create configuration options
        ConfigOption<String> optionHost = new ConfigOption<>("storage.host", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.CHEST, "Storage Host"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<Integer> optionPort = new ConfigOption<>("storage.port", ConfigTypes.INTEGER)
            .setIcon(new ConfigIcon(Material.CHEST, "Storage Port"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<String> optionDatabase = new ConfigOption<>("storage.database", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.CHEST, "Storage Database"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<String> optionUsername = new ConfigOption<>("storage.username", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.CHEST, "Storage Username"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<String> optionPassword = new ConfigOption<>("storage.password", ConfigTypes.PASSWORD)
            .setIcon(new ConfigIcon(Material.CHEST, "Storage Password"))
            .setRequired(true)
            .setReadOnly(true);
        
        // Add configuration options
        config.addOption(optionHost);
        config.addOption(optionPort);
        config.addOption(optionDatabase);
        config.addOption(optionUsername);
        config.addOption(optionPassword);
        
        // Get MySQL option values
        String host = optionHost.getValue(config);
        Integer port = optionPort.getValue(config);
        String database = optionDatabase.getValue(config);
        String username = optionUsername.getValue(config);
        String password = optionPassword.getValue(config);

        // Validate option values
        if (host == null || port == null || database == null || username == null || password == null) {
            throw new RuntimeException("Missing required MySQL configuration");
        }
        
        // Create MySQL configuration and datasource
        dbConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dbConfig.setUsername(username);
        dbConfig.setPassword(password);

        dbConfig.addDataSourceProperty("cachePrepStmts", "true");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dbSource = new HikariDataSource(dbConfig);

        // Setup database
        this.setup();
    }

    @Override
    public void unload() throws Exception {
        dbSource.close();
    }

    public Connection getConnection() {
        try {
            return dbSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setup() throws Exception {
        Connection connection = getConnection();

        // Make sure we've got a connection
        if (connection == null) {
            throw new Exception("Failed to connect to MySQL database");
        }

        // Create statements
        // ===========================
        PreparedStatement createCategories = connection.prepareStatement("CREATE TABLE IF NOT EXISTS categories ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "icon VARCHAR(255) NOT NULL,"
            + "name VARCHAR(40) NOT NULL,"
            + "description TEXT NOT NULL"
        + ");");

        PreparedStatement createProducts = connection.prepareStatement("CREATE TABLE IF NOT EXISTS products ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "icon VARCHAR(255) NOT NULL,"
            + "name VARCHAR(40) NOT NULL,"
            + "description TEXT NOT NULL,"
            + "categoryId INTEGER NOT NULL,"
            + "price FLOAT NOT NULL,"
            + "FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE CASCADE"
        + ");");

        PreparedStatement createCommands = connection.prepareStatement("CREATE TABLE IF NOT EXISTS commands ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "productId INTEGER NOT NULL,"
            + "command VARCHAR(255) NOT NULL,"
            + "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE"
        + ");");

        PreparedStatement createFields = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fields ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "name VARCHAR(50) NOT NULL"
        + ");");
        
        PreparedStatement createOrders = connection.prepareStatement("CREATE TABLE IF NOT EXISTS orders ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "number VARCHAR(20) NOT NULL,"
            + "playerUniqueId VARCHAR(36) NOT NULL,"
            + "playerName VARCHAR(16) NOT NULL,"
            + "currency VARCHAR(255) NOT NULL,"
            + "completed BOOLEAN NOT NULL"
        + ");");
        
        PreparedStatement createLineItems = connection.prepareStatement("CREATE TABLE IF NOT EXISTS lineItems ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "orderId INTEGER NOT NULL,"
            + "product JSON NOT NULL,"
            + "quantity INTEGER NOT NULL,"
            + "FOREIGN KEY(orderId) REFERENCES orders(id) ON DELETE CASCADE"
        + ");");
        
        PreparedStatement createGateways = connection.prepareStatement("CREATE TABLE IF NOT EXISTS gateways ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "displayName VARCHAR(40) NOT NULL,"
            + "type VARCHAR(50) NOT NULL,"
            + "config JSON NOT NULL"
        + ");");

        PreparedStatement createUserPreferences = connection.prepareStatement("CREATE TABLE IF NOT EXISTS user_preferences ("
            + "uuid VARCHAR(36) PRIMARY KEY,"
            + "preferences JSON NOT NULL"
        + ");");
        
        // Execute queries
        // ===========================
        createCategories.execute();
        createProducts.execute();
        createCommands.execute();
        createFields.execute();
        createOrders.execute();
        createLineItems.execute();
        createGateways.execute();
        createUserPreferences.execute();

        // Cleanup
        // ===========================
        createCategories.close();
        createProducts.close();
        createCommands.close();
        createFields.close();
        createOrders.close();
        createLineItems.close();
        createGateways.close();
        createUserPreferences.close();

        connection.close();
    }

    /**
     * Categories
     */

    @Override
    public List<Category> getCategories() throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM categories";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Get result
        ResultSet result = statement.executeQuery();
        List<Category> categories = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            Material icon = DbHelper.parseMaterial(result.getString(2));
            String name = result.getString(3);
            String description = result.getString(4);
            

            categories.add(new Category(id, icon, name, description));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return categories;
    }

    @Override
    public void createCategory(Category category) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO categories (icon, name, description) VALUES (?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(category.getIcon()));
        statement.setString(2, category.getName());
        statement.setString(3, category.getDescription());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            category.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateCategory(Category category) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE categories SET icon=?, name=?, description=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(category.getIcon()));
        statement.setString(2, category.getName());
        statement.setString(3, category.getDescription());
        statement.setInt(4, category.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteCategory(Category category) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM categories WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, category.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    /**
     * Products
     */
    
    @Override
    public List<Product> getProducts(Category category) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement;
        
        // Get all products or products that belong to a category
        if(category == null) {
            statement = connection.prepareStatement("SELECT products.id, products.icon, products.name, products.description, products.price, categories.id, categories.icon, categories.name, categories.description FROM products LEFT JOIN categories ON products.categoryId = categories.id;");
        } else {
            statement = connection.prepareStatement("SELECT * FROM products WHERE categoryId=?;");
            statement.setInt(1, category.getId());
        }
        
        // Get result
        ResultSet result = statement.executeQuery();
        List<Product> products = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            Material icon = DbHelper.parseMaterial(result.getString(2));
            String name = result.getString(3);
            String description = result.getString(4);
            float price = result.getFloat(5);

            // Get category data
            if(category == null) {
                int categoryId = result.getInt(6);
                Material categoryIcon = DbHelper.parseMaterial(result.getString(7));
                String categoryName = result.getString(8);
                String categoryDescription = result.getString(9);
                category = new Category(categoryId, categoryIcon, categoryName, categoryDescription);
            }

            // Get commands
            List<Command> rawCommands = this.getCommandsByProductId(id);
            DataList<Command> commands = new DataList<>(rawCommands);

            // Add product to list
            products.add(new Product(id, icon, name, description, category, price, commands));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return products;
    }

    @Override
    public void createProduct(Product product) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO products (icon, name, description, categoryId, price) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(product.getIcon()));
        statement.setString(2, product.getName());
        statement.setString(3, product.getDescription());
        statement.setInt(4, product.getCategory().getId());
        statement.setFloat(5, product.getPrice());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            product.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateProduct(Product product) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE products SET icon=?, name=?, description=?, categoryId=?, price=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(product.getIcon()));
        statement.setString(2, product.getName());
        statement.setString(3, product.getDescription());
        statement.setInt(4, product.getCategory().getId());
        statement.setFloat(5, product.getPrice());
        statement.setInt(6, product.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteProduct(Product product) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM products WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, product.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    /**
     * Product commands
     */

    @Override
    public List<Command> getCommandsByProductId(int productId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM commands WHERE productId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, productId);

        // Get result
        ResultSet result = statement.executeQuery();
        List<Command> commands = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            String command = result.getString(3);

            commands.add(new Command(id, productId, command));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return commands;
    }

    @Override
    public void createCommand(Command command) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO commands (productId, command) VALUES (?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setInt(1, command.getProductId());
        statement.setString(2, command.getCommand());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            command.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void deleteCommand(Command command) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM commands WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, command.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    /**
     * Fields
     */

    @Override
    public void createField(Field<?> field) throws Exception {

    }

    @Override
    public void updateField(Field<?> field) throws Exception {

    }

    @Override
    public void deleteField(Field<?> field) throws Exception {

    }
    
    /**
     * Orders
     */

    @Override
    public Order getCart(UUID uuid) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM orders WHERE playerUniqueId=? AND completed=0 LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, uuid.toString());
        
        // Get currencies
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);
        
        // Get result
        ResultSet result = statement.executeQuery();
        Order order = null;

        if (result.next()) {
            int id = result.getInt(1);
            String number = result.getString(2);
            UUID playerUniqueId = UUID.fromString(result.getString(3));
            String playerName = result.getString(4);
            String currencyCode = result.getString(5);
            boolean completed = result.getBoolean(6);

            // Get currency
            StoreCurrency currency = currencies.stream()
                .filter(i -> i.getCode().equals(currencyCode))
                .findFirst()
                .orElse(null);
            
            if(currency != null) {
                List<LineItem> rawLineItems = this.getLineItemsByOrderId(id);
                DataList<LineItem> lineItems = new DataList<>(rawLineItems);
                
                // Add order to list
                order = new Order(id, number, playerUniqueId, playerName, currency, completed, lineItems);
            } else {
                ConsoleHelper.printError("Failed to load order with id " + id + ", invalid currency: " + currencyCode);
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return order;
    }
    
    @Override
    public List<Order> getOrders() throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM orders";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Get currencies
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);
        
        // Get result
        ResultSet result = statement.executeQuery();
        List<Order> orders = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            String number = result.getString(2);
            UUID playerUniqueId = UUID.fromString(result.getString(3));
            String playerName = result.getString(4);
            String currencyCode = result.getString(5);
            boolean completed = result.getBoolean(6);

            // Get currency
            StoreCurrency currency = currencies.stream()
                .filter(i -> i.getCode().equals(currencyCode))
                .findFirst()
                .orElse(null);
            
            if(currency == null) {
                ConsoleHelper.printError("Failed to load order with id " + id + ", invalid currency: " + currencyCode);
                continue;
            }
            
            // Get LineItems's
            List<LineItem> rawLineItems = this.getLineItemsByOrderId(id);
            DataList<LineItem> lineItems = new DataList<>(rawLineItems);
            
            // Add order to list
            orders.add(new Order(id, number, playerUniqueId, playerName, currency, completed, lineItems));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return orders;
    }

    @Override
    public void createOrder(Order order) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO orders (number, playerUniqueId, playerName, currency, completed) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, order.getNumber());
        statement.setString(2, order.getPlayerUniqueId().toString());
        statement.setString(3, order.getPlayerName());
        statement.setString(4, order.getCurrency().getCode());
        statement.setBoolean(5, order.isCompleted());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            order.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateOrder(Order order) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE orders SET number=?, playerUniqueId=?, playerName=?, currency=?, completed=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, order.getNumber());
        statement.setString(2, order.getPlayerUniqueId().toString());
        statement.setString(3, order.getPlayerName());
        statement.setString(4, order.getCurrency().getCode());
        statement.setBoolean(5, order.isCompleted());
        statement.setInt(6, order.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteOrder(Order order) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM orders WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, order.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }
    
    /**
     * LineItems
     */
    
    @Override
    public List<LineItem> getLineItemsByOrderId(int orderId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM lineItems WHERE orderId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, orderId);

        // Get result
        ResultSet result = statement.executeQuery();
        List<LineItem> lineItems = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            int quantity = result.getInt(4);
            
            // Parse product snapshot
            String rawJson = result.getString(3);
            JsonObject json = DbHelper.parseJson(rawJson);
            ProductSnapshot product = new ProductSnapshot(json);

            lineItems.add(new LineItem(id, orderId, product, quantity));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return lineItems;
    }

    @Override
    public void createLineItem(LineItem lineItem) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO lineItems (orderId, product, quantity) VALUES (?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Prepare product snapshot
        JsonObject json = lineItem.getProduct().toJson();
        
        // Set arguments
        statement.setInt(1, lineItem.getOrderId());
        statement.setString(2, DbHelper.prepareJson(json));
        statement.setInt(3, lineItem.getQuantity());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            lineItem.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateLineItem(LineItem lineItem) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE lineItems SET orderId=?, product=?, quantity=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Prepare product snapshot
        JsonObject json = lineItem.getProduct().toJson();
        
        // Set arguments
        statement.setInt(1, lineItem.getOrderId());
        statement.setString(2, DbHelper.prepareJson(json));
        statement.setInt(3, lineItem.getQuantity());
        statement.setInt(4, lineItem.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }
    
    @Override
    public void deleteLineItem(LineItem lineItem) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM lineItems WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, lineItem.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    /**
     * Gateways
     */

    @Override
    public List<Gateway> getGateways() throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM gateways";
        PreparedStatement statement = connection.prepareStatement(sql);
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");

        // Get result
        ResultSet result = statement.executeQuery();
        List<Gateway> gateways = new ArrayList<>();

        while (result.next()) {
            int id = result.getInt(1);
            String displayName = result.getString(2);
            String typeHandle = result.getString(3);
            String json = result.getString(4);

            // Get type by handle
            GatewayType type = gatewayService.getTypeByHandle(typeHandle);
            
            if(type != null) {
                GatewayConfig config = DbHelper.parseGatewayConfig(json, type);
                gateways.add(new Gateway(id, displayName, type, config));
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return gateways;
    }

    @Override
    public void createGateway(Gateway gateway) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO gateways (displayName, type, config) VALUES (?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, gateway.getDisplayName());
        statement.setString(2, gateway.getType().getHandle());
        statement.setString(3, DbHelper.prepareJsonConfig(gateway.getConfig()));

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            gateway.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateGateway(Gateway gateway) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE gateways SET displayName=?, type=?, config=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, gateway.getDisplayName());
        statement.setString(2, gateway.getType().getHandle());
        statement.setString(3, DbHelper.prepareJsonConfig(gateway.getConfig()));
        statement.setInt(4, gateway.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteGateway(Gateway gateway) throws Exception {
        Connection connection = getConnection();
        String sql = "DELETE FROM gateways WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, gateway.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public UserPreferences getUserPreferences(UUID uuid) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT preferences FROM user_preferences WHERE uuid=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, uuid.toString());
        
        // Get result
        ResultSet result = statement.executeQuery();
        UserPreferences preferences;

        if (result.next()) {
            String json = result.getString(1);
            preferences = DbHelper.parseUserPreferences(json);
        } else {
            preferences = new UserPreferences();
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return preferences;
    }

    @Override
    public void saveUserPreferences(UUID uuid, UserPreferences preferences) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO user_preferences VALUES (?, ?) ON DUPLICATE KEY UPDATE preferences=?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        // Set parameters
        String json = DbHelper.prepareJsonConfig(preferences);
        
        statement.setString(1, uuid.toString());
        statement.setString(2, json);
        statement.setString(3, json);

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

}
