package nl.timvandijkhuizen.custompayments.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Field;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.DbHelper;
import nl.timvandijkhuizen.spigotutils.config.ConfigurationException;
import nl.timvandijkhuizen.spigotutils.config.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;

public class StorageMysql extends Storage {

    private HikariConfig dbConfig = new HikariConfig();
    private HikariDataSource dbSource;

    @Override
    public void load() throws Exception {
        YamlConfig config = CustomPayments.getInstance().getConfig();

        // Get config values
        if (!config.contains("storage.host") || !config.contains("storage.port") || !config.contains("storage.database") || !config.contains("storage.username") || !config.contains("storage.password")) {
            throw new ConfigurationException("Missing required MySQL configuration");
        }

        String host = config.getString("storage.host");
        int port = config.getInt("storage.port");
        String database = config.getString("storage.database");
        String username = config.getString("storage.username");
        String password = config.getString("storage.password");

        // Create config and datasource
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
            +"id INTEGER PRIMARY KEY AUTO_INCREMENT,"
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
            + "productId INTEGER NOT NULL," + "command VARCHAR(255) NOT NULL,"
            + "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE"
        + ");");

        PreparedStatement createFields = connection.prepareStatement("CREATE TABLE IF NOT EXISTS fields ("
            + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
            + "name VARCHAR(50) NOT NULL"
        + ");");

        // Execute queries
        // ===========================
        createCategories.execute();
        createProducts.execute();
        createCommands.execute();
        createFields.execute();

        // Cleanup
        // ===========================
        createCategories.close();
        createProducts.close();
        createCommands.close();
        createFields.close();

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
            String name = result.getString(2);
            String description = result.getString(3);

            categories.add(new Category(id, name, description));
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
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set arguments
        statement.setString(1, category.getName());
        statement.setString(2, category.getDescription());

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
        String sql = "UPDATE categories SET name=?, description=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(sql);

        // Set arguments
        statement.setString(1, category.getName());
        statement.setString(2, category.getDescription());
        statement.setInt(3, category.getId());

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
    public List<Product> getProducts() throws Exception {
        Connection connection = getConnection();
        String sql = "SELECT products.id, products.icon, products.name, products.description, products.price, categories.id, categories.name, categories.description FROM products"
                + " LEFT JOIN categories ON products.categoryId = categories.id;";
        PreparedStatement statement = connection.prepareStatement(sql);

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
            int categoryId = result.getInt(6);
            String categoryName = result.getString(7);
            String categoryDescription = result.getString(8);
            Category category = new Category(categoryId, categoryName, categoryDescription);

            // Get commands
            List<Command> rawCommands = this.getCommandsByProductId(id);
            DataList<Command> commands = new DataList<Command>(rawCommands);

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

}
