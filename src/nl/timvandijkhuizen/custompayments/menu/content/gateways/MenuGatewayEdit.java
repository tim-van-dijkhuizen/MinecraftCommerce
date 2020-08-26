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
import nl.timvandijkhuizen.custompayments.base.GatewayConfig;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenGatewayList;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuGatewayEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");
        Gateway gateway = args.length == 1 ? args[0].as(Gateway.class) : new Gateway();
        Menu menu = new Menu((gateway.getId() != null ? "Edit" : "Create") + " Gateway", MenuSize.LG);

        // Display name button
        // ===========================
        MenuItemBuilder displayNameButton = new MenuItemBuilder(Material.NAME_TAG);

        displayNameButton.setName(UI.color("Display Name", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (gateway.getDisplayName().length() > 0) {
            displayNameButton.addLore(UI.color(gateway.getDisplayName(), UI.SECONDARY_COLOR));
        } else {
            displayNameButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        displayNameButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("displayName")) {
            displayNameButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            displayNameButton.addEnchantGlow();

            for (String error : gateway.getErrors("displayName")) {
                displayNameButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        displayNameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

            UI.playSound(player, UI.CLICK_SOUND);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the display name of the gateway?", UI.PRIMARY_COLOR);
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

        typeButton.setName(UI.color("Type", UI.PRIMARY_COLOR, ChatColor.BOLD));

        if (gateway.getType() != null) {
            typeButton.addLore(UI.color(gateway.getType().getName(), UI.SECONDARY_COLOR));
        } else {
            typeButton.addLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
        }
        
        typeButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("type")) {
            typeButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            typeButton.addEnchantGlow();

            for (String error : gateway.getErrors("type")) {
                typeButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        typeButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.GATEWAY_TYPE.open(player, gateway, gateway.getType());
        });

        menu.setButton(typeButton, 13);
        
        // Option button
        // ===========================
        MenuItemBuilder optionButton = new MenuItemBuilder(Material.COMPARATOR);

        optionButton.setName(UI.color("Options", UI.PRIMARY_COLOR, ChatColor.BOLD));

        // Add configuration to lore
        if(gateway.getType() != null) {
            Collection<ConfigOption<?>> options = gateway.getType().getOptions();
            
            if(options.size() > 0) {
                for(ConfigOption<?> option : options) {
                    GatewayConfig config = gateway.getConfig();
                    String value = option.getValueLore(config);
                    ConfigIcon icon = option.getIcon();
                    
                    if(icon != null) {
                        optionButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.TEXT_COLOR) + UI.color(value, UI.SECONDARY_COLOR));
                    }
                }
            } else {
                optionButton.addLore(UI.color(UI.TAB + "None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }
        } else {
            optionButton.addLore(UI.color("Select a gateway type first.", UI.ERROR_COLOR, ChatColor.ITALIC));
        }
        
        optionButton.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

        // Add validation errors to lore
        if (gateway.hasErrors("config")) {
            optionButton.addLore("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
            optionButton.addEnchantGlow();

            for (String error : gateway.getErrors("config")) {
                optionButton.addLore(UI.color(" - " + error, UI.ERROR_COLOR));
            }
        }

        // Set click listener
        optionButton.setClickListener(event -> {
            if(gateway.getType() != null) {
                UI.playSound(player, UI.CLICK_SOUND);
                Menus.GATEWAY_OPTIONS.open(player, gateway);
            } else {
                UI.playSound(player, UI.ERROR_SOUND);
            }
        });

        menu.setButton(optionButton, 15);

        // Set bottom line
        // ===========================
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(Menu.BACKGROUND_BUTTON, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = Menu.CANCEL_BUTTON.clone();

        cancelButton.setClickListener(new OpenGatewayList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = Menu.SAVE_BUTTON.clone();

        if (gateway.hasErrors()) {
            saveButton.setLore(UI.color("Error: The glowing fields have invalid values.", UI.ERROR_COLOR));
        }

        saveButton.setClickListener(event -> {
            ClickType clickType = event.getClickType();

            UI.playSound(player, UI.CLICK_SOUND);
            saveButton.setLore(UI.color("Saving...", UI.TEXT_COLOR));
            menu.refresh();

            // Save gateway
            gatewayService.saveGateway(gateway, success -> {
                if (success) {
                    OpenGatewayList action = new OpenGatewayList(false);
                    
                    UI.playSound(player, UI.SUCCESS_SOUND);
                    action.onClick(new MenuItemClick(player, menu, saveButton, clickType));
                } else {
                    UI.playSound(player, UI.ERROR_SOUND);
                    Menus.GATEWAY_EDIT.open(player, gateway);
                }
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
