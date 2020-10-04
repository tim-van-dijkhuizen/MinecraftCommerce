package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
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
        Storage storage = Commerce.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                Set<Category> categories = storage.getCategories();
                ThreadHelper.execute(() -> callback.accept(categories));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to load categories: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Saves a category.
     * 
     * @param category
     * @param callback
     */
    public void saveCategory(Category category, Consumer<Boolean> callback) {
        Storage storage = Commerce.getInstance().getStorage();
        boolean isNew = category.getId() == null;

        // Validate the model
        if (!category.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit category
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                if (isNew) {
                    storage.createCategory(category);
                } else {
                    storage.updateCategory(category);
                }

                ThreadHelper.execute(() -> callback.accept(true));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to create/update category: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deletes a category.
     * 
     * @param category
     * @param callback
     */
    public void deleteCategory(Category category, Consumer<Boolean> callback) {
        Storage storage = Commerce.getInstance().getStorage();

        // Delete category
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                storage.deleteCategory(category);
                ThreadHelper.execute(() -> callback.accept(true));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete category: " + e.getMessage(), e);
            }
        });
    }

}
