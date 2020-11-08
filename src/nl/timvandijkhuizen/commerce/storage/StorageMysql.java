package nl.timvandijkhuizen.commerce.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.commerce.helpers.DbHelper;
import nl.timvandijkhuizen.commerce.helpers.JsonHelper;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class StorageMysql implements StorageType {

    // MySQL source
    private HikariDataSource dbSource;

    // Configuration options
    ConfigOption<String> configHost;
    ConfigOption<Integer> configPort;
    ConfigOption<String> configDatabase;
    ConfigOption<String> configUsername;
    ConfigOption<String> configPassword;
    ConfigOption<Integer> configMaxLifetime;

    @Override
    public String getType() {
        return "mysql";
    }

    @Override
    public String getDisplayName() {
        return "MySQL";
    }

    @Override
    public void init() throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();

        // Create configuration options
        configHost = new ConfigOption<>("storage.host", "Storage Host", XMaterial.BARREL, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("localhost");

        configPort = new ConfigOption<>("storage.port", "Storage Port", XMaterial.BARREL, ConfigTypes.INTEGER)
            .setRequired(true)
            .setDefaultValue(3306);

        configDatabase = new ConfigOption<>("storage.database", "Storage Database", XMaterial.BARREL, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("minecraft_commerce");

        configUsername = new ConfigOption<>("storage.username", "Storage Username", XMaterial.BARREL, ConfigTypes.STRING)
            .setRequired(true)
            .setDefaultValue("minecraft_commerce");

        configPassword = new ConfigOption<>("storage.password", "Storage Password", XMaterial.BARREL, ConfigTypes.PASSWORD);

        configMaxLifetime = new ConfigOption<>("storage.maxLifetime", "Max Lifetime", XMaterial.BARREL, ConfigTypes.INTEGER)
            .setRequired(true)
            .setDefaultValue(600);

        // Add configuration options
        config.addOption(configHost);
        config.addOption(configPort);
        config.addOption(configDatabase);
        config.addOption(configUsername);
        config.addOption(configPassword);
        config.addOption(configMaxLifetime);
    }

    @Override
    public void load() throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();
        String host = configHost.getValue(config);
        Integer port = configPort.getValue(config);
        String database = configDatabase.getValue(config);
        String username = configUsername.getValue(config);
        int maxLifetime = configMaxLifetime.getValue(config);

        // Create data pool
        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dbConfig.setUsername(username);
        dbConfig.setMaxLifetime(maxLifetime);
        
        // Set password if we've got one
        if(!configPassword.isValueEmpty(config)) {
            String password = configPassword.getValue(config);
            dbConfig.setPassword(password);
        }

        dbConfig.addDataSourceProperty("cachePrepStmts", "true");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Create source
        dbSource = new HikariDataSource(dbConfig);

        // Create tables
        createSchema();
    }

    @Override
    public void unload() throws Throwable {
        if (dbSource != null) {
            dbSource.close();
            dbSource = null;
        }
    }

    private void createSchema() throws SQLException {
        Connection connection = getConnection();

        // Create tables
        // ===========================

        // Create categories table
        if(!doesTableExist(connection, "categories")) {
            execute(connection, "CREATE TABLE categories ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "icon VARCHAR(255) NOT NULL,"
                + "name VARCHAR(40) NOT NULL,"
                + "description TEXT NOT NULL"
            + ");");
        }

        // Create products table
        if(!doesTableExist(connection, "products")) {
            execute(connection, "CREATE TABLE products ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "icon VARCHAR(255) NOT NULL,"
                + "name VARCHAR(40) NOT NULL,"
                + "description TEXT NOT NULL,"
                + "categoryId INTEGER NOT NULL,"
                + "price FLOAT NOT NULL,"
                + "FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE CASCADE"
            + ");");
            
            execute(connection, "CREATE INDEX idx_categoryid ON products (categoryId);");
        }

        // Create commands table
        if(!doesTableExist(connection, "commands")) {
            execute(connection, "CREATE TABLE commands ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "productId INTEGER NOT NULL,"
                + "command VARCHAR(255) NOT NULL,"
                + "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE"
            + ");");
            
            execute(connection, "CREATE INDEX idx_productid ON commands (productId);");
        }

        // Create fields table
        if(!doesTableExist(connection, "fields")) {
            execute(connection, "CREATE TABLE fields ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "icon VARCHAR(255) NOT NULL,"
                + "handle VARCHAR(40) NOT NULL,"
                + "name VARCHAR(40) NOT NULL,"
                + "description TEXT NOT NULL,"
                + "type VARCHAR(255) NOT NULL,"
                + "required BOOLEAN NOT NULL"
            + ");");
        }

        // Create gateways table
        if(!doesTableExist(connection, "gateways")) {
            execute(connection, "CREATE TABLE gateways ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "displayName VARCHAR(40) NOT NULL,"
                + "type VARCHAR(50) NOT NULL,"
                + "config JSON NOT NULL"
            + ");");
        }
        
        // Create user_preferences table
        if(!doesTableExist(connection, "user_preferences")) {
            execute(connection, "CREATE TABLE user_preferences ("
                + "playerUniqueId BINARY(16) PRIMARY KEY,"
                + "preferences JSON NOT NULL"
            + ");");
        }
        
        // Create orders table
        if(!doesTableExist(connection, "orders")) {
            execute(connection, "CREATE TABLE orders ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "uniqueId BINARY(16) UNIQUE NOT NULL,"
                + "playerUniqueId BINARY(16) NOT NULL,"
                + "playerName VARCHAR(16) NOT NULL,"
                + "currency VARCHAR(255) NOT NULL,"
                + "fields JSON NOT NULL,"
                + "gatewayId INTEGER,"
                + "completed BOOLEAN,"
                + "FOREIGN KEY(gatewayId) REFERENCES gateways(id) ON DELETE SET NULL"
            + ");");
            
            execute(connection, "CREATE INDEX idx_uniqueid ON orders (uniqueId);");
            execute(connection, "CREATE INDEX idx_playeruniqueid ON orders (playerUniqueId);");
            execute(connection, "CREATE INDEX idx_gatewayid ON orders (gatewayId);");
            execute(connection, "CREATE INDEX idx_completed ON orders (completed);");
        }

        // Create line_items table
        if(!doesTableExist(connection, "line_items")) {
            execute(connection, "CREATE TABLE line_items ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "orderId INTEGER NOT NULL,"
                + "productId INTEGER,"
                + "product JSON NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE SET NULL,"
                + "FOREIGN KEY(orderId) REFERENCES orders(id) ON DELETE CASCADE"
            + ");");
            
            execute(connection, "CREATE INDEX idx_orderid ON line_items (orderId);");
            execute(connection, "CREATE INDEX idx_productid ON line_items (productId);");
        }
        
        // Create transactions table
        if(!doesTableExist(connection, "transactions")) {
            execute(connection, "CREATE TABLE transactions ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                + "orderId INTEGER NOT NULL,"
                + "gatewayId INTEGER,"
                + "currency CHAR(3) NOT NULL,"
                + "reference VARCHAR(255) NOT NULL,"
                + "amount FLOAT NOT NULL,"
                + "meta JSON,"
                + "dateCreated BIGINT,"
                + "FOREIGN KEY(orderId) REFERENCES orders(id) ON DELETE CASCADE,"
                + "FOREIGN KEY(gatewayId) REFERENCES gateways(id) ON DELETE SET NULL"
            + ");");
            
            execute(connection, "CREATE INDEX idx_orderid ON transactions (orderId);");
            execute(connection, "CREATE INDEX idx_gatewayid ON transactions (gatewayId);");
        }

        // Close connection
        connection.close();
    }

    public Connection getConnection() throws SQLException {
        return dbSource != null ? dbSource.getConnection() : null;
    }

    /**
     * Categories
     */

    @Override
    public Set<Category> getCategories() throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT * FROM categories";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Category> categories = new LinkedHashSet<>();

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
    public void createCategory(Category category) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO categories (icon, name, description) VALUES (?, ?, ?);";
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
    public void updateCategory(Category category) throws Throwable {
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
    public void deleteCategory(Category category) throws Throwable {
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
    public Set<Product> getProducts(Category source) throws Throwable {
        Connection connection = getConnection();
        PreparedStatement statement;

        // Get all products or products that belong to a category
        if (source == null) {
            statement = connection.prepareStatement("SELECT products.id, products.icon, products.name, products.description, products.price, "
                + "categories.id, categories.icon, categories.name, categories.description "
                + "FROM products "
                + "LEFT JOIN categories ON products.categoryId = categories.id;");
        } else {
            statement = connection.prepareStatement("SELECT products.id, products.icon, products.name, products.description, products.price, "
                + "categories.id, categories.icon, categories.name, categories.description "
                + "FROM products "
                + "LEFT JOIN categories ON products.categoryId = categories.id "
                + "WHERE categoryId=?;");
            statement.setInt(1, source.getId());
        }

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Product> products = new LinkedHashSet<>();

        while (result.next()) {
            products.add(parseProduct(result));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return products;
    }

    @Override
    public Product getProductById(int id) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT products.id, products.icon, products.name, products.description, products.price, "
            + "categories.id, categories.icon, categories.name, categories.description "
            + "FROM products "
            + "LEFT JOIN categories ON products.categoryId = categories.id "
            + "WHERE products.id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, id);
        
        // Get result
        ResultSet result = statement.executeQuery();
        Product product = null;

        if (result.next()) {
            product = parseProduct(result);
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return product;
    }
    
    @Override
    public void createProduct(Product product) throws Throwable {
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
    public void updateProduct(Product product) throws Throwable {
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
    public void deleteProduct(Product product) throws Throwable {
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
    public Set<Command> getCommandsByProductId(int productId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT * FROM commands WHERE productId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, productId);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Command> commands = new LinkedHashSet<>();

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
    public void createCommand(Command command) throws Throwable {
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
    public void deleteCommand(Command command) throws Throwable {
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
    public Set<Field> getFields() throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT * FROM fields";
        PreparedStatement statement = connection.prepareStatement(sql);
        FieldService fieldService = Commerce.getInstance().getService("fields");

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Field> fields = new LinkedHashSet<>();

        while (result.next()) {
            int id = result.getInt(1);
            Material icon = DbHelper.parseMaterial(result.getString(2));
            String handle = result.getString(3);
            String name = result.getString(4);
            String description = result.getString(5);
            String typeHandle = result.getString(6);
            Boolean required = result.getBoolean(7);

            // Parse field type
            FieldType<?> type = fieldService.getFieldTypeByHandle(typeHandle);

            if (type != null) {
                fields.add(new Field(id, icon, handle, name, description, type, required));
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return fields;
    }

    @Override
    public void createField(Field field) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO fields (icon, handle, name, description, type, required) VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(field.getIcon()));
        statement.setString(2, field.getHandle());
        statement.setString(3, field.getName());
        statement.setString(4, field.getDescription());
        statement.setString(5, field.getType().getHandle());
        statement.setBoolean(6, field.isRequired());

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            field.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }

    @Override
    public void updateField(Field field) throws Throwable {
        Connection connection = getConnection();
        String sql = "UPDATE fields SET icon=?, handle=?, name=?, description=?, type=?, required=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(field.getIcon()));
        statement.setString(2, field.getHandle());
        statement.setString(3, field.getName());
        statement.setString(4, field.getDescription());
        statement.setString(5, field.getType().getHandle());
        statement.setBoolean(6, field.isRequired());
        statement.setInt(7, field.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteField(Field field) throws Throwable {
        Connection connection = getConnection();
        String sql = "DELETE FROM fields WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, field.getId());

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
    public Set<Gateway> getGateways() throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT * FROM gateways";
        PreparedStatement statement = connection.prepareStatement(sql);
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Gateway> gateways = new LinkedHashSet<>();

        while (result.next()) {
            int id = result.getInt(1);
            String displayName = result.getString(2);
            String typeHandle = result.getString(3);
            String json = result.getString(4);

            // Get type by handle
            GatewayType type = gatewayService.getTypeByHandle(typeHandle);

            if (type != null) {
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
    public void createGateway(Gateway gateway) throws Throwable {
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
    public void updateGateway(Gateway gateway) throws Throwable {
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
    public void deleteGateway(Gateway gateway) throws Throwable {
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
    
    /**
     * User preferences
     */
    
    @Override
    public UserPreferences getUserPreferences(UUID playerUniqueId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT preferences FROM user_preferences WHERE playerUniqueId=? LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setBytes(1, DbHelper.prepareUniqueId(playerUniqueId));

        // Get result
        ResultSet result = statement.executeQuery();
        UserPreferences preferences = null;

        if (result.next()) {
            String json = result.getString(1);
            preferences = DbHelper.parseUserPreferences(json);
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return preferences;
    }

    @Override
    public void saveUserPreferences(UUID playerUniqueId, UserPreferences preferences) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO user_preferences VALUES (?, ?) ON DUPLICATE KEY UPDATE preferences=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set parameters
        String json = DbHelper.prepareJsonConfig(preferences);

        statement.setBytes(1, DbHelper.prepareUniqueId(playerUniqueId));
        statement.setString(2, json);
        statement.setString(3, json);

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }
    
    /**
     * Orders
     */

    @Override
    public Order getCart(UUID playerUniqueId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.fields, orders.completed, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId "
            + "WHERE orders.playerUniqueId=? AND completed=0 LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setBytes(1, DbHelper.prepareUniqueId(playerUniqueId));

        // Get result
        ResultSet result = statement.executeQuery();
        Order order = null;

        if (result.next()) {
            order = parseOrder(result);
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return order;
    }

    @Override
    public Order getOrderById(int id) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.fields, orders.completed, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId "
            + "WHERE orders.id=? LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, id);

        // Get result
        ResultSet result = statement.executeQuery();
        Order order = null;

        if (result.next()) {
            order = parseOrder(result);
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return order;
    }
    
    @Override
    public Order getOrderByUniqueId(UUID uniqueId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.fields, orders.completed, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId "
            + "WHERE orders.uniqueId=? LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setBytes(1, DbHelper.prepareUniqueId(uniqueId));

        // Get result
        ResultSet result = statement.executeQuery();
        Order order = null;

        if (result.next()) {
            order = parseOrder(result);
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return order;
    }

    @Override
    public Set<Order> getOrders() throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.fields, orders.completed, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId "
            + "WHERE completed=1";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Order> orders = new LinkedHashSet<>();

        while (result.next()) {
            Order order = parseOrder(result);

            if (order != null) {
                orders.add(order);
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return orders;
    }

    @Override
    public Set<Order> getOrdersByPlayer(UUID playerUniqueId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.fields, orders.completed, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId "
            + "WHERE orders.playerUniqueId=? AND completed=1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setBytes(1, DbHelper.prepareUniqueId(playerUniqueId));

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Order> orders = new LinkedHashSet<>();

        while (result.next()) {
            Order order = parseOrder(result);

            if (order != null) {
                orders.add(order);
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return orders;
    }

    @Override
    public void createOrder(Order order) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO orders (uniqueId, playerUniqueId, playerName, currency, fields, gatewayId, completed) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        Gateway gateway = order.getGateway();
        StoreCurrency currency = order.getCurrency();

        // Set arguments
        statement.setBytes(1, DbHelper.prepareUniqueId(order.getUniqueId()));
        statement.setBytes(2, DbHelper.prepareUniqueId(order.getPlayerUniqueId()));
        statement.setString(3, order.getPlayerName());
        statement.setString(4, currency.getCode().getCurrencyCode());
        statement.setString(5, DbHelper.prepareJsonConfig(order.getFieldData()));

        if (gateway != null) {
            statement.setInt(6, gateway.getId());
        } else {
            statement.setNull(6, Types.INTEGER);
        }
        
        statement.setBoolean(7, order.isCompleted());

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
    public void updateOrder(Order order) throws Throwable {
        Connection connection = getConnection();
        String sql = "UPDATE orders SET uniqueId=?, playerUniqueId=?, playerName=?, currency=?, fields=?, gatewayId=?, completed=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        Gateway gateway = order.getGateway();
        StoreCurrency currency = order.getCurrency();

        // Set arguments
        statement.setBytes(1, DbHelper.prepareUniqueId(order.getUniqueId()));
        statement.setBytes(2, DbHelper.prepareUniqueId(order.getPlayerUniqueId()));
        statement.setString(3, order.getPlayerName());
        statement.setString(4, currency.getCode().getCurrencyCode());
        statement.setString(5, DbHelper.prepareJsonConfig(order.getFieldData()));

        if (gateway != null) {
            statement.setInt(6, gateway.getId());
        } else {
            statement.setNull(6, Types.INTEGER);
        }

        statement.setBoolean(7, order.isCompleted());
        statement.setInt(8, order.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteOrder(Order order) throws Throwable {
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
    public Set<LineItem> getLineItemsByOrderId(int orderId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT * FROM line_items WHERE orderId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, orderId);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<LineItem> lineItems = new LinkedHashSet<>();

        while (result.next()) {
            int id = result.getInt(1);
            Integer productId = DbHelper.getInteger(result, 3);
            String rawJson = result.getString(4);
            int quantity = result.getInt(5);

            // Parse product snapshot
            JsonObject json = DbHelper.parseJson(rawJson);
            ProductSnapshot product = new ProductSnapshot(json);

            lineItems.add(new LineItem(id, orderId, productId, product, quantity));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return lineItems;
    }

    @Override
    public void createLineItem(LineItem lineItem) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO line_items (orderId, productId, product, quantity) VALUES (?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Prepare product snapshot
        JsonObject json = lineItem.getProduct().toJson();

        // Set arguments
        statement.setInt(1, lineItem.getOrderId());
        
        if(lineItem.getProductId() != null) {
            statement.setInt(2, lineItem.getProductId());
        } else {
            statement.setNull(2, Types.INTEGER);
        }
        
        statement.setString(3, DbHelper.prepareJson(json));
        statement.setInt(4, lineItem.getQuantity());

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
    public void updateLineItem(LineItem lineItem) throws Throwable {
        Connection connection = getConnection();
        String sql = "UPDATE line_items SET orderId=?, productId=?, product=?, quantity=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Prepare product snapshot
        JsonObject json = lineItem.getProduct().toJson();

        // Set arguments
        statement.setInt(1, lineItem.getOrderId());

        if(lineItem.getProductId() != null) {
            statement.setInt(2, lineItem.getProductId());
        } else {
            statement.setNull(2, Types.INTEGER);
        }
        
        statement.setString(3, DbHelper.prepareJson(json));
        statement.setInt(4, lineItem.getQuantity());
        statement.setInt(5, lineItem.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteLineItem(LineItem lineItem) throws Throwable {
        Connection connection = getConnection();
        String sql = "DELETE FROM line_items WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setInt(1, lineItem.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public Set<Transaction> getTransactionsByOrderId(int orderId) throws Throwable {
        Connection connection = getConnection();
        String sql = "SELECT transactions.id, transactions.currency, transactions.reference, transactions.amount, transactions.meta, transactions.dateCreated, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config "
            + "FROM transactions "
            + "LEFT JOIN gateways ON gateways.id = transactions.gatewayId "
            + "WHERE orderId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, orderId);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<Transaction> transactions = new LinkedHashSet<>();

        while (result.next()) {
            int id = result.getInt(1);
            StoreCurrency currency = DbHelper.parseCurrency(result.getString(2));
            String reference = result.getString(3);
            float amount = result.getFloat(4);
            JsonObject meta = JsonHelper.fromJson(result.getString(5));
            long dateCreated = result.getLong(6);

            // Get Gateway
            GatewayService gatewayService = Commerce.getInstance().getService("gateways");
            Integer gatewayId = DbHelper.getInteger(result, 7);
            String gatewayDisplayName = result.getString(8);
            String gatewayTypeHandle = result.getString(9);
            String gatewayJson = result.getString(10);
            Gateway gateway = null;

            if (gatewayId != null) {
                GatewayType gatewayType = gatewayService.getTypeByHandle(gatewayTypeHandle);

                if (gatewayType != null) {
                    GatewayConfig gatewayConfig = DbHelper.parseGatewayConfig(gatewayJson, gatewayType);
                    gateway = new Gateway(gatewayId, gatewayDisplayName, gatewayType, gatewayConfig);
                }
            }
            
            if(currency == null) {
                ConsoleHelper.printError("Failed to load transaction with id " + id + ", currency does not exist.");
                continue;
            }
            
            transactions.add(new Transaction(id, orderId, gateway, currency, reference, amount, meta, dateCreated));
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return transactions;
    }
    
    @Override
    public void createTransaction(Transaction transaction) throws Throwable {
        Connection connection = getConnection();
        String sql = "INSERT INTO transactions (orderId, gatewayId, currency, reference, amount, dateCreated, meta) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Add arguments
        Gateway gateway = transaction.getGateway();
        StoreCurrency currency = transaction.getCurrency();
        
        statement.setInt(1, transaction.getOrderId());
        statement.setInt(2, gateway.getId());
        statement.setString(3, DbHelper.prepareCurrency(currency));
        statement.setString(4, transaction.getReference());
        statement.setFloat(5, transaction.getAmount());
        statement.setLong(6, transaction.getDateCreated());
        
        // Add optional meta
        JsonObject meta = transaction.getMeta();
        
        if(meta != null) {
            statement.setString(7, JsonHelper.toJson(meta));
        } else {
            statement.setString(7, null);
        }

        // Execute query
        statement.executeUpdate();

        // Set ID
        ResultSet ids = statement.getGeneratedKeys();

        if (ids.next()) {
            transaction.setId(ids.getInt(1));
        }

        // Cleanup
        ids.close();
        statement.close();
        connection.close();
    }
    
    private Product parseProduct(ResultSet result) throws Throwable {
        int id = result.getInt(1);
        Material icon = DbHelper.parseMaterial(result.getString(2));
        String name = result.getString(3);
        String description = result.getString(4);
        float price = result.getFloat(5);
        
        // Create the category
        int categoryId = result.getInt(6);
        Material categoryIcon = DbHelper.parseMaterial(result.getString(7));
        String categoryName = result.getString(8);
        String categoryDescription = result.getString(9);
        Category category = new Category(categoryId, categoryIcon, categoryName, categoryDescription);

        // Get commands
        Set<Command> rawCommands = getCommandsByProductId(id);
        DataList<Command> commands = new DataList<>(rawCommands);

        // Add product to list
        return new Product(id, icon, name, description, category, price, commands);
    }
    
    private Order parseOrder(ResultSet result) throws Throwable {
        int id = result.getInt(1);
        UUID uniqueId = DbHelper.parseUniqueId(result.getBytes(2));
        UUID playerUniqueId = DbHelper.parseUniqueId(result.getBytes(3));
        String playerName = result.getString(4);
        OrderFieldData fields = DbHelper.parseOrderFields(result.getString(6));
        boolean completed = result.getBoolean(7);

        // Get currency
        String currencyCode = result.getString(5);
        StoreCurrency currency = DbHelper.parseCurrency(currencyCode);

        if(currency == null) {
            ConsoleHelper.printError("Failed to load order with id " + id + ", currency does not exist.");
            return null;
        }
        
        // Get Gateway
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Integer gatewayId = DbHelper.getInteger(result, 8);
        String gatewayDisplayName = result.getString(9);
        String gatewayTypeHandle = result.getString(10);
        String gatewayJson = result.getString(11);
        Gateway gateway = null;

        if (gatewayId != null) {
            GatewayType gatewayType = gatewayService.getTypeByHandle(gatewayTypeHandle);

            if (gatewayType != null) {
                GatewayConfig gatewayConfig = DbHelper.parseGatewayConfig(gatewayJson, gatewayType);
                gateway = new Gateway(gatewayId, gatewayDisplayName, gatewayType, gatewayConfig);
            }
        }
        
        // Get line items
        Set<LineItem> rawLineItems = getLineItemsByOrderId(id);
        DataList<LineItem> lineItems = new DataList<>(rawLineItems);
        
        // Get transactions
        Set<Transaction> transactions = getTransactionsByOrderId(id);

        return new Order(id, uniqueId, playerUniqueId, playerName, currency, lineItems, fields, gateway, completed, transactions);
    }
    
    /**
     * Returns whether the specified table exists.
     * 
     * @param connection
     * @param table
     * @return
     */
    private boolean doesTableExist(Connection connection, String table) {
        try {
            PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE ?;");
            
            statement.setString(1, table);

            // Get result and close
            ResultSet result = statement.executeQuery();
            boolean exists = result.next();
            
            statement.close();
            
            return exists;
        } catch(SQLException e) {
            return false;
        }
    }
    
    /**
     * Executes a query and catches any errors.
     * 
     * @param connection
     * @param sql
     * @return
     * @throws SQLException 
     */
    private void execute(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        
        statement.execute();
        statement.close();
    }

}
