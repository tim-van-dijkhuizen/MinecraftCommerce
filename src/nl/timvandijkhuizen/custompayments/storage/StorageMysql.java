package nl.timvandijkhuizen.custompayments.storage;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Field;
import nl.timvandijkhuizen.custompayments.base.Storage;
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
		connection.prepareStatement("CREATE TABLE IF NOT EXISTS fields (" +
            "id INTEGER PRIMARY KEY," +
            "name VARCHAR(50) NOT NULL," +
        ")").execute();
    }

	@Override
	public <T> boolean createField(Field<T> field) {
		return false;
	}

	@Override
	public <T> boolean editField(Field<T> field) {
		return false;
	}

	@Override
	public <T> boolean deleteField(Field<T> field) {
		return false;
	}

}
