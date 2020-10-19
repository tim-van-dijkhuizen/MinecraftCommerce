package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.function.Consumer;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class CategoryService extends BaseService {

    @Override
    public String getHandle() {
        return "categories";
    }

    /**
     * Returns all categories.
     * 
     * @param callback
     */
    public void getCategories(Consumer<Set<Category>> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getCategories(), callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load categories: " + error.getMessage(), error);
        });
    }

    /**
     * Saves a category.
     * 
     * @param category
     * @param callback
     */
    public void saveCategory(Category category, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();
        boolean isNew = category.getId() == null;

        // Validate the model
        if (!category.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit category
        ThreadHelper.executeAsync(() -> {
            if (isNew) {
                storage.createCategory(category);
            } else {
                storage.updateCategory(category);
            }
        }, () ->  callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to create/update category: " + error.getMessage(), error);
        });
    }

    /**
     * Deletes a category.
     * 
     * @param category
     * @param callback
     */
    public void deleteCategory(Category category, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        // Delete category
        ThreadHelper.executeAsync(() -> storage.deleteCategory(category), () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete category: " + error.getMessage(), error);
        });
    }

}
