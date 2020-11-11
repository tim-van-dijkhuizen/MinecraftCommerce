package nl.timvandijkhuizen.commerce.menu.content.products;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderVariable;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductCommands implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Product Commands", 3, 7, 1, 1);
        
        // Get arguments
        Product product = args.get(0);
        Menu returnMenu = args.get(1);

        // Add command buttons
        for (Command command : product.getCommands()) {
            addCommandButton(player, menu, product, command);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            returnMenu.open(player);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new command button
        MenuItemBuilder createButton = new MenuItemBuilder(XMaterial.NETHER_STAR);

        createButton.setName(UI.color("Create Command", UI.COLOR_SECONDARY, ChatColor.BOLD));
        createButton.setLore(UI.color("Add a command to be executed when this product", UI.COLOR_TEXT));
        createButton.addLore(UI.color("is purchased. You can add variables to the", UI.COLOR_TEXT));
        createButton.addLore(UI.color("command using the following format: ", UI.COLOR_TEXT) + UI.color("{variableKey}", UI.COLOR_SECONDARY));

        // Add variables to lore
        createButton.addLore("", UI.color("Available variables:", UI.COLOR_TEXT));

        for (OrderVariable variable : orderService.getOrderVariables()) {
            String key = variable.getKey();
            String[] properties = variable.getProperties();
            
            if(properties.length == 0) {
                createButton.addLore(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + UI.color(" {" + key + "}", UI.COLOR_SECONDARY));
                continue;
            }
            
            for(String property : properties) {
                createButton.addLore(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + UI.color(" {" + key + ":" + property + "}", UI.COLOR_SECONDARY));
            }
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
                    addCommandButton(player, menu, product, newCommand);
                    menu.open(player);

                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setItem(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }
    
    private void addCommandButton(Player player, PagedMenu menu, Product product, Command command) {
        MenuItemBuilder item = new MenuItemBuilder(XMaterial.COMMAND_BLOCK);

        item.setName(UI.color(command.getCommand(), UI.COLOR_PRIMARY, ChatColor.BOLD));
        item.setLore("", UI.color("Right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        item.setClickListener(event -> {
            ClickType clickType = event.getClickType();

            if (clickType == ClickType.RIGHT) {
                UI.playSound(player, UI.SOUND_DELETE);
                product.removeCommand(command);
                menu.removePagedItem(item);
                menu.refresh();
            }
        });
        
        menu.addPagedItem(item);
    }

}