package nl.timvandijkhuizen.custompayments.menu.content.gateways;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.config.sources.GatewayConfig;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenGatewayList;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");
        Gateway gateway = args.get(0, new Gateway());
        Menu menu = new Menu((gateway.getId() != null ? "Edit" : "Create") + " Gateway", MenuSize.LG);

        // Display name button
        // ===========================
        MenuItemBuilder displayNameButton = new MenuItemBuilder(Material.NAME_TAG);

        displayNameButton.setName(UI.color("Display Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (gateway.getDisplayName().length() > 0) {
            displayNameButton.addLore(UI.color(gateway.getDisplayName(), UI.COLOR_SECONDARY));
        } else {
            displayNameButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        displayNameButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("displayName")) {
            displayNameButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            displayNameButton.addEnchantGlow();

            for (String error : gateway.getErrors("displayName")) {
                displayNameButton.addLore(UI.color(" - " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        displayNameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the display name of the gateway?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    gateway.setDisplayName(input);
                    Menus.GATEWAY_EDIT.open(player, gateway);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            player.closeInventory();
            conversation.begin();
        });

        menu.setButton(displayNameButton, 11);
        
        // Type button
        // ===========================
        MenuItemBuilder typeButton = new MenuItemBuilder(Material.OAK_FENCE_GATE);

        typeButton.setName(UI.color("Type", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (gateway.getType() != null) {
            typeButton.addLore(UI.color(gateway.getType().getName(), UI.COLOR_SECONDARY));
        } else {
            typeButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        typeButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("type")) {
            typeButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            typeButton.addEnchantGlow();

            for (String error : gateway.getErrors("type")) {
                typeButton.addLore(UI.color(" - " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        typeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_TYPE.open(player, gateway);
        });

        menu.setButton(typeButton, 13);
        
        // Option button
        // ===========================
        MenuItemBuilder optionButton = new MenuItemBuilder(Material.COMPARATOR);

        optionButton.setName(UI.color("Options", UI.COLOR_PRIMARY, ChatColor.BOLD));

        // Add configuration to lore
        if(gateway.getType() != null) {
            Collection<ConfigOption<?>> options = gateway.getType().getOptions();
            
            if(options.size() > 0) {
                for(ConfigOption<?> option : options) {
                    GatewayConfig config = gateway.getConfig();
                    ConfigIcon icon = option.getIcon();
                    
                    // Ignore options without an icon
                    if(icon == null) {
                        continue;
                    }
                    
                    if(!option.isValueEmpty(config)) {
                        for(String line : option.getValueLore(config)) {
                            optionButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color(line, UI.COLOR_SECONDARY));
                        }
                    } else {
                        optionButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                    }
                }
            } else {
                optionButton.addLore(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
        } else {
            optionButton.addLore(UI.color("Select a gateway type first.", UI.COLOR_ERROR, ChatColor.ITALIC));
        }
        
        optionButton.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("config")) {
            optionButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
            optionButton.addEnchantGlow();

            for (String error : gateway.getErrors("config")) {
                optionButton.addLore(UI.color(" - " + error, UI.COLOR_ERROR));
            }
        }

        // Set click listener
        optionButton.setClickListener(event -> {
            if(gateway.getType() != null) {
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.GATEWAY_OPTIONS.open(player, gateway);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
            }
        });

        menu.setButton(optionButton, 15);

        // Set bottom line
        // ===========================
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new OpenGatewayList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        if (gateway.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.COLOR_ERROR));
        }

        saveButton.setClickListener(event -> {
            ClickType clickType = event.getClickType();

            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Save gateway
            gatewayService.saveGateway(gateway, success -> {
                menu.enableButtons();
                
                if (success) {
                    OpenGatewayList action = new OpenGatewayList(false);
                    
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    action.onClick(new MenuItemClick(player, menu, saveButton, clickType));
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    Menus.GATEWAY_EDIT.open(player, gateway);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
