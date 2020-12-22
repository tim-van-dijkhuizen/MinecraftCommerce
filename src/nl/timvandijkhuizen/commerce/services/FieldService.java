package nl.timvandijkhuizen.commerce.services;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.FieldType;
import nl.timvandijkhuizen.commerce.base.StorageType;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeBoolean;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeDouble;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeInteger;
import nl.timvandijkhuizen.commerce.fieldtypes.FieldTypeString;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class FieldService extends BaseService {

    private Set<FieldType<?>> fieldTypes = new LinkedHashSet<>();
    private Set<ConfigOption<?>> options = null;

    @Override
    public String getHandle() {
        return "fields";
    }

    @Override
    public void init() throws Throwable {
        fieldTypes.add(new FieldTypeString());
        fieldTypes.add(new FieldTypeInteger());
        fieldTypes.add(new FieldTypeDouble());
        fieldTypes.add(new FieldTypeBoolean());
    }

    @Override
    public void load() throws Throwable {
        getFields(fields -> {
            if (fields == null) {
                ConsoleHelper.printError("Failed to cache options, fields cannot be loaded.");
                return;
            }

            // Create option cache
            options = fields.stream()
                .map(i -> i.getOption())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        });
    }

    /**
     * Registers a new field type.
     * 
     * @param fieldType
     */
    public void registerFieldType(FieldType<?> fieldType) {
        fieldTypes.add(fieldType);
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
    
    public void getFields(Consumer<Set<Field>> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        ThreadHelper.getAsync(() -> storage.getFields(), callback, error -> {
            callback.accept(null);
            ConsoleHelper.printError("Failed to load fields: " + error.getMessage(), error);
        });
    }

    /**
     * Saves a field.
     * 
     * @param field
     * @param callback
     */
    public void saveField(Field field, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();
        boolean isNew = field.getId() == null;

        // Validate the model
        if (!field.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit field
        ThreadHelper.executeAsync(() -> {
            if (isNew) {
                storage.createField(field);
            } else {
                storage.updateField(field);
            }

            // Update option cache
            options = storage.getFields().stream()
                .map(i -> i.getOption())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }, () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to create/update field: " + error.getMessage(), error);
        });
    }

    /**
     * Deletes a field.
     * 
     * @param field
     * @param callback
     */
    public void deleteField(Field field, Consumer<Boolean> callback) {
        StorageType storage = Commerce.getInstance().getStorage();

        // Delete field
        ThreadHelper.executeAsync(() -> {
            storage.deleteField(field);

            // Update option cache
            options = storage.getFields().stream()
                .map(i -> i.getOption())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }, () -> callback.accept(true), error -> {
            callback.accept(false);
            ConsoleHelper.printError("Failed to delete field: " + error.getMessage(), error);
        });
    }

    public Set<ConfigOption<?>> getOptions() {
        return options;
    }

}
