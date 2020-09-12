package nl.timvandijkhuizen.commerce.services;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.events.RegisterFieldTypesEvent;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeBoolean;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeInteger;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeString;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class FieldService extends BaseService {

    private Set<FieldType<?>> fieldTypes;
    private Set<Field> fields = new HashSet<>();
    
    @Override
    public String getHandle() {
        return "fields";
    }

    @Override
    public void init() throws Exception {
        Storage storage = Commerce.getInstance().getStorage();
        RegisterFieldTypesEvent event = new RegisterFieldTypesEvent();

        // Register field types
        event.addType(new FieldTypeString());
        event.addType(new FieldTypeInteger());
        event.addType(new FieldTypeBoolean());
        
        Bukkit.getServer().getPluginManager().callEvent(event);
        this.fieldTypes = event.getTypes();
        
        // Load fields from storage
        fields = storage.getFields();
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
        Storage storage = Commerce.getInstance().getStorage();
        boolean isNew = field.getId() == null;

        // Validate the model
        if (!field.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit field
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
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
        Storage storage = Commerce.getInstance().getStorage();

        // Delete field
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
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
    
    /**
     * Returns all registered field types.
     * 
     * @return
     */
    public Set<FieldType<?>> getFieldTypes() {
        return fieldTypes;
    }
    
    /**
     * Returns a field type by its handle or null.
     * 
     * @param handle
     * @return
     */
    public FieldType<?> getFieldTypeByHandle(String handle) {
        return fieldTypes.stream()
            .filter(i -> i.getHandle().equals(handle))
            .findFirst()
            .orElse(null);
    }
    
}
