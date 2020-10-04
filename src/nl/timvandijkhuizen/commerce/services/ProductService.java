package nl.timvandijkhuizen.commerce.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.commerce.base.Storage;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.events.RegisterCommandVariablesEvent;
import nl.timvandijkhuizen.commerce.variables.VariableUniqueId;
import nl.timvandijkhuizen.commerce.variables.VariableUsername;
import nl.timvandijkhuizen.spigotutils.data.DataAction;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.services.BaseService;

public class ProductService extends BaseService {

    private Set<CommandVariable> commandVariables;

    @Override
    public String getHandle() {
        return "products";
    }

    @Override
    public void init() throws Exception {
        RegisterCommandVariablesEvent event = new RegisterCommandVariablesEvent();

        event.addVariable(VariableUsername.class);
        event.addVariable(VariableUniqueId.class);
        Bukkit.getServer().getPluginManager().callEvent(event);

        commandVariables = event.getVariables();
    }

    /**
     * Returns all products.
     * 
     * @param callback
     */
    public void getProducts(Consumer<Set<Product>> callback) {
        Storage storage = Commerce.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                Set<Product> products = storage.getProducts(null);
                ThreadHelper.execute(() -> callback.accept(products));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to load products: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Returns all products that belong to a category.
     * 
     * @param callback
     */
    public void getProducts(Category category, Consumer<Set<Product>> callback) {
        Storage storage = Commerce.getInstance().getStorage();

        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                Set<Product> products = storage.getProducts(category);
                ThreadHelper.execute(() -> callback.accept(products));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(null));
                ConsoleHelper.printError("Failed to load products: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Saves a product.
     * 
     * @param product
     * @param callback
     */
    public void saveProduct(Product product, Consumer<Boolean> callback) {
        Storage storage = Commerce.getInstance().getStorage();
        boolean isNew = product.getId() == null;

        // Validate the model
        if (!product.isValid()) {
            callback.accept(false);
            return;
        }

        // Create or edit product
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            DataList<Command> commands = product.getCommands();

            try {
                if (isNew) {
                    storage.createProduct(product);
                } else {
                    storage.updateProduct(product);
                }

                // Set product id on commands
                for (Command command : commands) {
                    command.setProductId(product.getId());
                }

                // Update commands
                for (Command command : commands.getByAction(DataAction.CREATE)) {
                    storage.createCommand(command);
                }

                for (Command command : commands.getByAction(DataAction.DELETE)) {
                    storage.deleteCommand(command);
                }

                // Remove pending from data list
                commands.clearPending();
                
                ThreadHelper.execute(() -> callback.accept(true));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to create/update product: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Deletes a product.
     * 
     * @param product
     * @param callback
     */
    public void deleteProduct(Product product, Consumer<Boolean> callback) {
        Storage storage = Commerce.getInstance().getStorage();

        // Delete product
        Bukkit.getScheduler().runTaskAsynchronously(Commerce.getInstance(), () -> {
            try {
                storage.deleteProduct(product);
                ThreadHelper.execute(() -> callback.accept(true));
            } catch (Exception e) {
                ThreadHelper.execute(() -> callback.accept(false));
                ConsoleHelper.printError("Failed to delete product: " + e.getMessage(), e);
            }
        });
    }

    public void executeCommand(Command command, Order order) {
        String parsedCommand = command.getCommand();

        // Replace variables inside command
        for (CommandVariable variable : getCommandVariables()) {
            parsedCommand.replace("{" + variable.getKey() + "}", variable.getValue(order));
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        ConsoleHelper.printInfo("Executed command: " + parsedCommand);
    }

    /**
     * Returns all available command variables.
     * 
     * @return
     */
    public Collection<CommandVariable> getCommandVariables() {
        return Collections.unmodifiableSet(commandVariables);
    }

}
