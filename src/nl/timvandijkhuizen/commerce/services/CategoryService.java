package nl.timvandijkhuizen.commerce.services;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.editable.EditableCategory;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class CategoryService extends BaseService {

    private Set<Category> categories;
    
    @Override
    public String getHandle() {
        return "categories";
    }

    @Override
    public void load() throws Exception {
        Storage storage = Commerce.getInstance().getStorage();
        
        try {
            categories = storage.getCategories();
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load categories: " + e.getMessage(), e);
        }
    }
    
    /**
     * Returns all categories.
     * 
     * @return
     */
    public Set<Category> getCategories() {
        return categories;
    }
    
    /**
     * Returns a category by its id.
     * 
     * @param id
     * @return
     */
    public Category getCategoryById(int id) {
        return categories.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Saves a category.
     * 
     * @param category
     * @param callback
     */
    public void saveCategory(EditableCategory category, Consumer<Boolean> callback) {
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
                    categories.add(category);
                } else {
                    storage.updateCategory(category);
                    
                    // Update source
                    Category course = getCategoryById(category.getId());
                    
                    if(course != null) {
                        course.updateFromCopy(category);
                    }
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
        Storage storage = Commerce.getInstance().getStorage();

        // Delete category
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                storage.deleteCategory(category);
                categories.remove(category);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete category: " + e.getMessage(), e);
            }
        });
    }

}
