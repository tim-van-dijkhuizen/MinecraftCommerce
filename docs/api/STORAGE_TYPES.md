# API - Storage types

Using storage types you can add other ways to store your data.

### Create the storage type
```java
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.elements.Transaction;

public class StorageTest implements StorageType {

    @Override
    public String getHandle() {
        // This value must be globally unique, only one storage type can use this.
        return "test";
    }
    
    @Override
    public String getDisplayName() {
        return "Test";
    }

    @Override
    public void init() throws Throwable {
        // Set up the storage type.
        // For example create the config options.
    }

    @Override
    public void load() throws Throwable {
        // Load the storage type
        // For example load the config values and connect to the DB
    }

    @Override
    public void unload() throws Throwable {
        // Unload the storage type
        // For example disconnect the DB
    }

    @Override
    public Set<Category> getCategories() throws Throwable {
        // Load categories
        return new HashSet<>();
    }

    @Override
    public void createCategory(Category category) throws Throwable {
        // Create category
    }

    @Override
    public void updateCategory(Category category) throws Throwable {
        // Update category
    }

    @Override
    public void deleteCategory(Category category) throws Throwable {
        // Delete category
    }

    ...

    @Override
    public Set<Transaction> getTransactionsByOrderId(int orderId) throws Throwable {
        // Load transactions for the specified order
        return new HashSet<>();
    }

    @Override
    public void createTransaction(Transaction transaction) throws Throwable {
        // Create transaction
    }

}
```

### Register the storage type
```java
import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    CommerceApi.registerStorageType(new StorageTypeTest());
}
```