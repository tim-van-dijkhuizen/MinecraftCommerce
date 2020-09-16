package nl.timvandijkhuizen.commerce.menu.content.products;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.CommandVariable;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductCommands implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        ProductService productService = Commerce.getInstance().getService("products");
        PagedMenu menu = new PagedMenu("Product Commands", 3, 7, 1, 1);
        Product product = args.get(0);

        // Add command buttons
        for (Command command : product.getCommands()) {
            MenuItemBuilder item = new MenuItemBuilder(Material.COMMAND_BLOCK);

            item.setName(UI.color(command.getCommand(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.setLore("", UI.color("Use right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                if (clickType == ClickType.RIGHT) {
                    UI.playSound(player, UI.SOUND_DELETE);
                    product.removeCommand(command);
                    menu.removePagedButton(item);
                    menu.refresh();
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player, product);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new command button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Command", UI.COLOR_SECONDARY, ChatColor.BOLD));
        createButton.setLore(UI.color("Add a command to be executed when this product", UI.COLOR_TEXT));
        createButton.addLore(UI.color("is purchased. You can add variables to the", UI.COLOR_TEXT));
        createButton.addLore(UI.color("command using the following format: ", UI.COLOR_TEXT) + UI.color("{variableKey}", UI.COLOR_SECONDARY));

        // Add variables to lore
        createButton.addLore("", UI.color("Available variables:", UI.COLOR_TEXT));

        for (CommandVariable variable : productService.getCommandVariables()) {
            createButton.addLore(UI.color(" - {" + variable.getKey() + "}", UI.COLOR_SECONDARY));
        }

        createButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("Type the command you want to add:", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    Command newCommand = new Command(input);

                    UI.playSound(player, UI.SOUND_SUCCESS);
                    product.addCommand(newCommand);
                    Menus.PRODUCT_COMMANDS.open(player, product);

                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}