package nl.timvandijkhuizen.custompayments.services;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class CategoryService implements Service {

    @Override
    public String getHandle() {
        return "categories";
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() throws Exception {

    }

    /**
     * Returns all categories.
     * 
     * @param callback
     */
    public void getCategories(Consumer<List<Category>> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                List<Category> categories = storage.getCategories();
                MainThread.execute(() -> callback.accept(categories));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(null));
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
        Storage storage = CustomPayments.getInstance().getStorage();
        boolean isNew = category.getId() == null;

        // Validate the model
        if (!category.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit category
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                if (isNew) {
                    storage.createCategory(category);
                } else {
                    storage.updateCategory(category);
                }

                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
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
        Storage storage = CustomPayments.getInstance().getStorage();

        // Delete category
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                storage.deleteCategory(category);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete category: " + e.getMessage(), e);
            }
        });
    }

}
