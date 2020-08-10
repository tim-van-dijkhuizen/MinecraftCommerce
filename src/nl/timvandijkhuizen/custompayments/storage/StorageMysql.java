package nl.timvandijkhuizen.custompayments.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.ElementQuery;
import nl.timvandijkhuizen.custompayments.base.Field;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.DbHelper;
import nl.timvandijkhuizen.custompayments.storage.query.ProductQuery;
import nl.timvandijkhuizen.spigotutils.config.ConfigurationException;
import nl.timvandijkhuizen.spigotutils.config.YamlConfig;

public class StorageMysql extends Storage {

    private HikariConfig dbConfig = new HikariConfig();
    private HikariDataSource dbSource;
    
	@Override
	public void load() throws Exception {
		YamlConfig config = CustomPayments.getInstance().getConfig();
		
		// Get config values
		if(!config.contains("storage.host") || !config.contains("storage.port") || !config.contains("storage.database") || !config.contains("storage.username") || !config.contains("storage.password")) {
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
		if(connection == null) {
			throw new Exception("Failed to connect to MySQL database");
		}
		
		// Create tables
		connection.prepareStatement("CREATE TABLE IF NOT EXISTS products (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "icon VARCHAR(255) NOT NULL," +
            "name VARCHAR(50) NOT NULL," +
            "description TEXT NOT NULL," +
            "price FLOAT NOT NULL" +
        ");").execute();
		
		connection.prepareStatement("CREATE TABLE IF NOT EXISTS fields (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(50) NOT NULL" +
        ");").execute();
    }

    
    /**
     * Products
     */
    
	@Override
	public List<Product> getProducts(ProductQuery query) throws Exception {
		PreparedStatement statement = createSelect("products", query);
		
		// Get result
        ResultSet result = statement.executeQuery();
        List<Product> products = new ArrayList<>();

        while (result.next()) {
        	int id = result.getInt(1);
        	Material icon = DbHelper.parseMaterial(result.getString(2));
        	String name = result.getString(3);
        	String description = result.getString(4);
        	float price = result.getFloat(5);
        	
            products.add(new Product(id, icon, name, description, price));
        }
        
        return products;
	}
    
	@Override
	public void createProduct(Product product) throws Exception {
		Connection connection = getConnection();
		
		// Create statement
		PreparedStatement statement = connection.prepareStatement("INSERT INTO products (name, icon, description, price) VALUES (?, ?, ?, ?);");
		
		// Set arguments
		statement.setString(1, DbHelper.prepareMaterial(product.getIcon()));
		statement.setString(2, product.getName());
		statement.setString(3, product.getDescription());
		statement.setFloat(4, product.getPrice());
		
		// Execute query
		statement.execute();
	}
	
	@Override
	public void updateProduct(Product product) throws Exception {
		Connection connection = getConnection();
		
		// Create statement
		PreparedStatement statement = connection.prepareStatement("UPDATE products SET icon=?, name=?, description=?, price=? WHERE id=?;");
		
		// Set arguments
		statement.setString(1, DbHelper.prepareMaterial(product.getIcon()));
		statement.setString(2, product.getName());
		statement.setString(3, product.getDescription());
		statement.setFloat(4, product.getPrice());
		statement.setInt(5, product.getId());
		
		// Execute query
		statement.execute();
	}
	
	@Override
	public void deleteProduct(Product product) throws Exception {
		Connection connection = getConnection();
		
		// Create statement
		PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE id=?;");
		
		// Set arguments
		statement.setInt(1, product.getId());
		
		// Execute query
		statement.execute();
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
	
	private PreparedStatement createSelect(String table, ElementQuery query) throws SQLException {
		Connection connection = getConnection();
		String sql = "SELECT * FROM " + table;
		
		return connection.prepareStatement(sql);
	}

}
