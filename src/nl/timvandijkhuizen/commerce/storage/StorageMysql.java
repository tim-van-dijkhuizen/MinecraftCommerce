package nl.timvandijkhuizen.commerce.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;

import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.base.PaymentUrl;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.base.Storage;
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
import nl.timvandijkhuizen.commerce.helpers.DbHelper;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;

public class StorageMysql extends Storage {
    
    // MySQL source
    private HikariDataSource dbSource;
    
    // Configuration options
    ConfigOption<String> configHost;
    ConfigOption<Integer> configPort;
    ConfigOption<String> configDatabase;
    ConfigOption<String> configUsername;
    ConfigOption<String> configPassword;

    @Override
    public void init() throws Exception {
        YamlConfig config = Commerce.getInstance().getConfig();

        // Create configuration options
        configHost = new ConfigOption<>("storage.host", "Storage Host", Material.BARREL, ConfigTypes.STRING)
            .setRequired(true);
        
        configPort = new ConfigOption<>("storage.port", "Storage Port", Material.BARREL, ConfigTypes.INTEGER)
            .setRequired(true);
        
        configDatabase = new ConfigOption<>("storage.database", "Storage Database", Material.BARREL, ConfigTypes.STRING)
            .setRequired(true);
        
        configUsername = new ConfigOption<>("storage.username", "Storage Username", Material.BARREL, ConfigTypes.STRING)
            .setRequired(true);
        
        configPassword = new ConfigOption<>("storage.password", "Storage Password", Material.BARREL, ConfigTypes.PASSWORD)
            .setRequired(true);
        
        // Add configuration options
        config.addOption(configHost);
        config.addOption(configPort);
        config.addOption(configDatabase);
        config.addOption(configUsername);
        config.addOption(configPassword);
    }
    
    @Override
    public void load() throws Exception {
        YamlConfig config = Commerce.getInstance().getConfig();
        String host = configHost.getValue(config);
        Integer port = configPort.getValue(config);
        String database = configDatabase.getValue(config);
        String username = configUsername.getValue(config);
        String password = configPassword.getValue(config);
        
        // Create data pool
        HikariConfig dbConfig = new HikariConfig();
        
        dbConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dbConfig.setUsername(username);
        dbConfig.setPassword(password);

        dbConfig.addDataSourceProperty("cachePrepStmts", "true");
        dbConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Create source
        dbSource = new HikariDataSource(dbConfig);
        
        // Create tables
        this.createTables();
    }

    @Override
    public void unload() throws Exception {
        if(dbSource != null) {
            dbSource.close();
            dbSource = null;
        }
    }
    
    private void createTables() throws SQLException {
        Connection connection = getConnection();
        
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
            + "icon VARCHAR(255) NOT NULL,"
            + "name VARCHAR(40) NOT NULL,"
            + "description TEXT NOT NULL,"
            + "type VARCHAR(255) NOT NULL,"
            + "required BOOLEAN NOT NULL"
        + ");");
        
        PreparedStatement createOrders = connection.prepareStatement("CREATE TABLE IF NOT EXISTS orders ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "number CHAR(36) UNIQUE NOT NULL,"
            + "playerUniqueId CHAR(36) NOT NULL,"
            + "playerName VARCHAR(16) NOT NULL,"
            + "currency VARCHAR(255) NOT NULL,"
            + "completed BOOLEAN NOT NULL,"
            + "fields JSON NOT NULL,"
            + "gatewayId INTEGER,"
            + "paymentUrl VARCHAR(255),"
            + "paymentUrlExpire BIGINT,"
            + "FOREIGN KEY(gatewayId) REFERENCES gateways(id) ON DELETE SET NULL"
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
            + "playerUniqueId CHAR(36) PRIMARY KEY,"
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

    public Connection getConnection() throws SQLException {
        return dbSource != null ? dbSource.getConnection() : null;
    }

    /**
     * Categories
     */

    @Override
    public Set<Category> getCategories() throws Exception {
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
    public void createCategory(Category category) throws Exception {
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
    public Set<Product> getProducts(Category category) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement;
        
        // Get all products or products that belong to a category
        if(category == null) {
            statement = connection.prepareStatement("SELECT products.id, products.icon, products.name, products.description, products.price, categories.id, categories.icon, categories.name, categories.description FROM products LEFT JOIN categories ON products.categoryId = categories.id;");
        } else {
            statement = connection.prepareStatement("SELECT id, icon, name, description, price FROM products WHERE categoryId=?;");
            statement.setInt(1, category.getId());
        }
        
        // Get result
        ResultSet result = statement.executeQuery();
        Set<Product> products = new LinkedHashSet<>();

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
            Set<Command> rawCommands = this.getCommandsByProductId(id);
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
    public Set<Command> getCommandsByProductId(int productId) throws Exception {
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
    public Set<Field> getFields() throws Exception {
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
            String name = result.getString(3);
            String description = result.getString(4);
            String typeHandle = result.getString(5);
            Boolean required = result.getBoolean(6);

            // Parse field type
            FieldType<?> type = fieldService.getFieldTypeByHandle(typeHandle);
            
            if(type != null) {
                fields.add(new Field(id, icon, name, description, type, required));
            }
        }

        // Cleanup
        result.close();
        statement.close();
        connection.close();

        return fields;
    }
    
    @Override
    public void createField(Field field) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO fields (icon, name, description, type, required) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(field.getIcon()));
        statement.setString(2, field.getName());
        statement.setString(3, field.getDescription());
        statement.setString(4, field.getType().getHandle());
        statement.setBoolean(5, field.isRequired());

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
    public void updateField(Field field) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE fields SET icon=?, name=?, description=?, type=?, required=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, DbHelper.prepareMaterial(field.getIcon()));
        statement.setString(2, field.getName());
        statement.setString(3, field.getDescription());
        statement.setString(4, field.getType().getHandle());
        statement.setBoolean(5, field.isRequired());
        statement.setInt(6, field.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }

    @Override
    public void deleteField(Field field) throws Exception {
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
     * Orders
     */

    @Override
    public Order getCart(UUID playerUniqueId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.completed, orders.fields, orders.paymentUrl, orders.paymentUrlExpire, "
    		+ "gateways.id, gateways.displayName, gateways.type, gateways.config FROM orders "
    		+ "LEFT JOIN gateways ON gateways.id = orders.gatewayId WHERE orders.playerUniqueId=? AND orders.completed=0 LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, playerUniqueId.toString());
        
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
    public Order getOrderByUniqueId(UUID uniqueId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.completed, orders.fields, orders.paymentUrl, orders.paymentUrlExpire, "
    		+ "gateways.id, gateways.displayName, gateways.type, gateways.config FROM orders "
    		+ "LEFT JOIN gateways ON gateways.id = orders.gatewayId WHERE orders.uniqueId=? LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, uniqueId.toString());
        
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
    public Set<Order> getOrders() throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.completed, orders.fields, orders.paymentUrl, orders.paymentUrlExpire, "
    		+ "gateways.id, gateways.displayName, gateways.type, gateways.config FROM orders "
    		+ "LEFT JOIN gateways ON gateways.id = orders.gatewayId WHERE orders.completed=1";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        // Get result
        ResultSet result = statement.executeQuery();
        Set<Order> orders = new LinkedHashSet<>();

        while (result.next()) {
            Order order = parseOrder(result);
            
            if(order != null) {
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
    public Set<Order> getOrdersByPlayer(UUID uuid) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT orders.id, orders.uniqueId, orders.playerUniqueId, orders.playerName, orders.currency, orders.completed, orders.fields, orders.paymentUrl, orders.paymentUrlExpire, "
            + "gateways.id, gateways.displayName, gateways.type, gateways.config FROM orders "
            + "LEFT JOIN gateways ON gateways.id = orders.gatewayId WHERE orders.completed=1 AND orders.playerUniqueId=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        statement.setString(1, uuid.toString());
        
        // Get result
        ResultSet result = statement.executeQuery();
        Set<Order> orders = new LinkedHashSet<>();

        while (result.next()) {
            Order order = parseOrder(result);
            
            if(order != null) {
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
    public void createOrder(Order order) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO orders (uniqueId, playerUniqueId, playerName, currency, completed, fields, gatewayId, paymentUrl, paymentUrlExpire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        Gateway gateway = order.getGateway();
        PaymentUrl paymentUrl = order.getPaymentUrl();
        
        // Set arguments
        statement.setString(1, order.getUniqueId().toString());
        statement.setString(2, order.getPlayerUniqueId().toString());
        statement.setString(3, order.getPlayerName());
        statement.setString(4, order.getCurrency().getCode());
        statement.setBoolean(5, order.isCompleted());
        statement.setString(6, DbHelper.prepareJsonConfig(order.getFieldData()));

        if(gateway != null) {
            statement.setInt(7, gateway.getId());
        } else {
            statement.setNull(7, Types.INTEGER);
        }
        
        if(paymentUrl != null) {
            statement.setString(8, paymentUrl.getUrl());
            statement.setLong(9, paymentUrl.getExpiryTime());
        } else {
        	statement.setNull(8, Types.VARCHAR);
        	statement.setNull(9, Types.BIGINT);
        }

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
        String sql = "UPDATE orders SET uniqueId=?, playerUniqueId=?, playerName=?, currency=?, completed=?, fields=?, gatewayId=?, paymentUrl=?, paymentUrlExpire=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        Gateway gateway = order.getGateway();
        PaymentUrl paymentUrl = order.getPaymentUrl();

        // Set arguments
        statement.setString(1, order.getUniqueId().toString());
        statement.setString(2, order.getPlayerUniqueId().toString());
        statement.setString(3, order.getPlayerName());
        statement.setString(4, order.getCurrency().getCode());
        statement.setBoolean(5, order.isCompleted());
        statement.setString(6, DbHelper.prepareJsonConfig(order.getFieldData()));
        
        if(gateway != null) {
            statement.setInt(7, gateway.getId());
        } else {
            statement.setNull(7, Types.INTEGER);
        }
        
        if(paymentUrl != null) {
            statement.setString(8, paymentUrl.getUrl());
            statement.setLong(9, paymentUrl.getExpiryTime());
        } else {
        	statement.setNull(8, Types.VARCHAR);
        	statement.setNull(9, Types.BIGINT);
        }
        
        statement.setInt(10, order.getId());

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }
    
    @Override
    public void completeOrder(Order order) throws Exception {
        Connection connection = getConnection();
        String sql = "UPDATE orders SET completed=?, paymentUrl=?, paymentUrlExpire=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setBoolean(1, true);
        statement.setNull(2, Types.VARCHAR);
        statement.setNull(3, Types.BIGINT);
        statement.setInt(4, order.getId());

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
    public Set<LineItem> getLineItemsByOrderId(int orderId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT * FROM lineItems WHERE orderId=?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, orderId);

        // Get result
        ResultSet result = statement.executeQuery();
        Set<LineItem> lineItems = new LinkedHashSet<>();

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
    public Set<Gateway> getGateways() throws Exception {
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
    public UserPreferences getUserPreferences(UUID playerUniqueId) throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT preferences FROM user_preferences WHERE playerUniqueId=? LIMIT 1";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, playerUniqueId.toString());
        
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
    public void saveUserPreferences(UUID playerUniqueId, UserPreferences preferences) throws Exception {
        Connection connection = getConnection();
        String sql = "INSERT INTO user_preferences VALUES (?, ?) ON DUPLICATE KEY UPDATE preferences=?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        
        // Set parameters
        String json = DbHelper.prepareJsonConfig(preferences);
        
        statement.setString(1, playerUniqueId.toString());
        statement.setString(2, json);
        statement.setString(3, json);

        // Execute query
        statement.execute();

        // Cleanup
        statement.close();
        connection.close();
    }
    
    private Order parseOrder(ResultSet result) throws Exception {
        int id = result.getInt(1);
        UUID uniqueId = UUID.fromString(result.getString(2));
        UUID playerUniqueId = UUID.fromString(result.getString(3));
        String playerName = result.getString(4);
        String currencyCode = result.getString(5);
        boolean completed = result.getBoolean(6);
        OrderFieldData fields = DbHelper.parseOrderFields(result.getString(7));

        // Get currency
        YamlConfig config = Commerce.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);
        
        StoreCurrency currency = currencies.stream()
            .filter(i -> i.getCode().equals(currencyCode))
            .findFirst()
            .orElse(null);
        
        // Get Gateway
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Integer gatewayId = result.getInt(10);
        String gatewayDisplayName = result.getString(11);
        String gatewayTypeHandle = result.getString(12);
        String gatewayJson = result.getString(13);
        Gateway gateway = null;
        
        if(gatewayId != null) {
            GatewayType gatewayType = gatewayService.getTypeByHandle(gatewayTypeHandle);
            
            if(gatewayType != null) {
                GatewayConfig gatewayConfig = DbHelper.parseGatewayConfig(gatewayJson, gatewayType);
                gateway = new Gateway(gatewayId, gatewayDisplayName, gatewayType, gatewayConfig);
            }
        }
        
        // Get payment URL
        String paymentUrlRaw = result.getString(8);
        long paymentUrlExpire = result.getLong(9);
        PaymentUrl paymentUrl = null;
        
        if(paymentUrlRaw != null) {
        	paymentUrl = new PaymentUrl(paymentUrlRaw, paymentUrlExpire);
        }
        
        if(currency != null) {
            Set<LineItem> rawLineItems = this.getLineItemsByOrderId(id);
            DataList<LineItem> lineItems = new DataList<>(rawLineItems);
            
            // Add order to list
            return new Order(id, uniqueId, playerUniqueId, playerName, currency, completed, lineItems, fields, gateway, paymentUrl);
        } else {
            ConsoleHelper.printError("Failed to load order with id " + id + ", invalid currency: " + currencyCode);
        }
        
        return null;
    }

}
