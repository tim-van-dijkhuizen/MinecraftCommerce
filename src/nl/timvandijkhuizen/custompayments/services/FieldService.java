package nl.timvandijkhuizen.custompayments.services;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.elements.Field;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class FieldService extends BaseService {

    private Set<Field> fields = new HashSet<>();
    
    @Override
    public String getHandle() {
        return "fields";
    }

    @Override
    public void load() throws Exception {
        Storage storage = CustomPayments.getInstance().getStorage();
        
        // Load fields into memory
        fields = storage.getFields();
    }

    @Override
    public void unload() throws Exception {
        fields = null;
    }
    
    public Set<Field> getFields() {
        return fields;
    }

    /**
     * Saves a field.
     * 
     * @param field
     * @param callback
     */
    public void saveField(Field field, Consumer<Boolean> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();
        boolean isNew = field.getId() == null;

        // Validate the model
        if (!field.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit field
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                if (isNew) {
                    storage.createField(field);
                    fields.add(field);
                } else {
                    storage.updateField(field);
                }
                
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to create/update field: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deletes a field.
     * 
     * @param field
     * @param callback
     */
    public void deleteField(Field field, Consumer<Boolean> callback) {
        Storage storage = CustomPayments.getInstance().getStorage();

        // Delete field
        Bukkit.getScheduler().runTaskAsynchronously(CustomPayments.getInstance(), () -> {
            try {
                storage.deleteField(field);
                fields.remove(field);
                MainThread.execute(() -> callback.accept(true));
            } catch (Exception e) {
                MainThread.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete field: " + e.getMessage(), e);
            }
        });
    }
    
}
