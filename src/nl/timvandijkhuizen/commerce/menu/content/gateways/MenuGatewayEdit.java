package nl.timvandijkhuizen.commerce.menu.content.gateways;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionGatewayList;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Gateway gateway = args.get(0, new Gateway());
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " " + (gateway.getId() != null ? "Edit" : "Create") + " Gateway", MenuSize.LG);

        // Display name button
        // ===========================
        MenuItemBuilder displayNameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        displayNameButton.setName(UI.color("Display Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        displayNameButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (gateway.getDisplayName().length() > 0) {
                lore.add(UI.color(gateway.getDisplayName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, gateway, "displayName");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        displayNameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the display name of the gateway?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    gateway.setDisplayName(input);
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setItem(displayNameButton, 11);

        // Type button
        // ===========================
        MenuItemBuilder typeButton = new MenuItemBuilder(XMaterial.OAK_FENCE_GATE);

        typeButton.setName(UI.color("Type", UI.COLOR_PRIMARY, ChatColor.BOLD));

        typeButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (gateway.getType() != null) {
                lore.add(UI.color(gateway.getType().getDisplayName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, gateway, "type");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        typeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_TYPE.open(player, gateway, menu);
        });

        menu.setItem(typeButton, 13);

        // Config button
        // ===========================
        MenuItemBuilder configButton = new MenuItemBuilder(XMaterial.COMPARATOR);

        configButton.setName(UI.color("Config", UI.COLOR_PRIMARY, ChatColor.BOLD));

        configButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            // Add configuration to lore
            if (gateway.getType() != null) {
                Collection<ConfigOption<?>> options = gateway.getType().getOptions();

                if (options.size() > 0) {
                    for (ConfigOption<?> option : options) {
                        GatewayConfig config = gateway.getConfig();

                        if (!option.isValueEmpty(config)) {
                            lore.add(UI.color(Icon.SQUARE + " " + option.getName() + ": " + option.getDisplayValue(config), UI.COLOR_TEXT));
                        } else {
                            lore.add(UI.color(Icon.SQUARE + " " + option.getName() + ": ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
                        }
                    }
                } else {
                    lore.add(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }
            } else {
                lore.add(UI.color("Select a gateway type first.", UI.COLOR_ERROR, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, gateway, "config");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        configButton.setClickListener(event -> {
            if (gateway.getType() != null) {
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.GATEWAY_OPTIONS.open(player, gateway, menu);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
            }
        });

        menu.setItem(configButton, 15);

        // Set bottom line
        // ===========================
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new ActionGatewayList());

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Save gateway
            gatewayService.saveGateway(gateway, success -> {
                menu.enableItems();

                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.setLore("");
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Error: Field contains an invalid value.", UI.COLOR_ERROR));
                }
                
                menu.refresh();
            });
        });

        menu.setItem(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
