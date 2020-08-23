package nl.timvandijkhuizen.custompayments.menu.content.products;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.CommandVariable;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductCommands implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        ProductService productService = CustomPayments.getInstance().getService("products");
        PagedMenu menu = new PagedMenu("Product Commands", 3, 7, 1, 1);
        Product product = args[0].as(Product.class);

        // Add command buttons
        for (Command command : product.getCommands()) {
            MenuItemBuilder item = new MenuItemBuilder(Material.COMMAND_BLOCK);

            item.setName(UI.color(command.getCommand(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            item.setLore("", UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                if (clickType == ClickType.RIGHT) {
                    product.removeCommand(command);
                    menu.removePagedButton(item);
                    menu.refresh();
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.PRODUCT_EDIT.open(player, product);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new command button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Command", UI.SECONDARY_COLOR, ChatColor.BOLD));
        createButton.setLore(UI.color("Add a command to be executed when this product", UI.TEXT_COLOR));
        createButton.addLore(UI.color("is purchased. You can add variables to the", UI.TEXT_COLOR));
        createButton.addLore(UI.color("command using the following format: ", UI.TEXT_COLOR) + UI.color("{variableKey}", UI.SECONDARY_COLOR));

        // Add variables to lore
        createButton.addLore("", UI.color("Available variables:", UI.TEXT_COLOR));

        for (CommandVariable variable : productService.getCommandVariables()) {
            createButton.addLore(UI.color(" - {" + variable.getKey() + "}", UI.SECONDARY_COLOR));
        }

        createButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("Type the command you want to add:", UI.PRIMARY_COLOR);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    Command newCommand = new Command(input);

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