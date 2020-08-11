package nl.timvandijkhuizen.custompayments.menu.content.products;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Command;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenProductList;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductEdit implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataValue... args) {
		ProductService productService = CustomPayments.getInstance().getService("products");
		Product product = args.length == 1 ? args[0].as(Product.class) : new Product(Material.DIAMOND, "", "", 1);
		Menu menu = new Menu((product.getId() != null ? "Edit" : "Create") + " product", MenuSize.XXL);
		
		// Icon button
		// ===========================
		MenuItemBuilder iconButton = new MenuItemBuilder(product.getIcon());
		
		iconButton.setName(UI.color("Icon", UI.PRIMARY_COLOR, ChatColor.BOLD));
		iconButton.setLore(UI.color("Click to set the product price.", UI.TEXT_COLOR));
		
		// Add validation errors to lore
		if(product.hasErrors("icon")) {
			iconButton.addLoreLines("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
			
			for(String error : product.getErrors("icon")) {
				iconButton.addLoreLine(UI.color(" - " + error, UI.ERROR_COLOR));
			}
		}
		
		// Set click listener
		iconButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			Menus.PRODUCT_ICON.open(player, product);
		});
		
		menu.setButton(iconButton, 11);
		
		// Name button
		// ===========================
		MenuItemBuilder nameButton = new MenuItemBuilder(Material.NAME_TAG);
		
		nameButton.setName(UI.color("Name", UI.PRIMARY_COLOR, ChatColor.BOLD));
		nameButton.setLore(UI.color("Click to set the product name.", UI.TEXT_COLOR), "", UI.color("Current value:", UI.TEXT_COLOR));
		
		if(product.getName().length() > 0) {
			nameButton.addLoreLine(UI.color(product.getName(), UI.SECONDARY_COLOR));
		} else {
			nameButton.addLoreLine(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
		}
		
		// Add validation errors to lore
		if(product.hasErrors("name")) {
			nameButton.addLoreLines("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
			
			for(String error : product.getErrors("name")) {
				nameButton.addLoreLine(UI.color(" - " + error, UI.ERROR_COLOR));
			}
		}
		
		// Set click listener
		nameButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());
			
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			
		    Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
				@Override
				public String getPromptText(ConversationContext context) {
					return UI.color("What should be the name of the product?", UI.PRIMARY_COLOR);
				}
				
				@Override
				public Prompt acceptInput(ConversationContext context, String input) {
					product.setName(input);
					Menus.PRODUCT_EDIT.open(player, product);
					return null;
				}
			}).withLocalEcho(false).buildConversation(whoClicked);
		    
		    whoClicked.closeInventory();
		    conversation.begin();
		});
		
		menu.setButton(nameButton, 13);
		
		// Description button
		// ===========================
		MenuItemBuilder descriptionButton = new MenuItemBuilder(Material.PAPER);
		
		descriptionButton.setName(UI.color("Description", UI.PRIMARY_COLOR, ChatColor.BOLD));
		descriptionButton.setLore(UI.color("Click to set the product description.", UI.TEXT_COLOR), "", UI.color("Current value:", UI.TEXT_COLOR));
		
		if(product.getDescription().length() > 0) {
			String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");
			
			for(String line : lines) {
				descriptionButton.addLoreLine(UI.color(line, UI.SECONDARY_COLOR));
			}
		} else {
			descriptionButton.addLoreLine(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
		}
		
		// Add validation errors to lore
		if(product.hasErrors("description")) {
			descriptionButton.addLoreLines("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
			
			for(String error : product.getErrors("description")) {
				descriptionButton.addLoreLine(UI.color(" - " + error, UI.ERROR_COLOR));
			}
		}
		
		// Set click listener
		descriptionButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());
			
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			
		    Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
				@Override
				public String getPromptText(ConversationContext context) {
					return UI.color("What should be the description of the product?", UI.PRIMARY_COLOR);
				}
				
				@Override
				public Prompt acceptInput(ConversationContext context, String input) {
					product.setDescription(input);
					Menus.PRODUCT_EDIT.open(player, product);
					return null;
				}
			}).withLocalEcho(false).buildConversation(whoClicked);
		    
		    whoClicked.closeInventory();
		    conversation.begin();
		});
		
		menu.setButton(descriptionButton, 15);
		
		// Price button
		// ===========================
		MenuItemBuilder priceButton = new MenuItemBuilder(Material.SUNFLOWER);
		
		priceButton.setName(UI.color("Price", UI.PRIMARY_COLOR, ChatColor.BOLD));
		priceButton.setLore(UI.color("Click to set the product price.", UI.TEXT_COLOR), "", UI.color("Current value:", UI.TEXT_COLOR), UI.color("" + product.getPrice(), UI.SECONDARY_COLOR));
		
		// Add validation errors to lore
		if(product.hasErrors("price")) {
			priceButton.addLoreLines("", UI.color("Errors:", UI.ERROR_COLOR, ChatColor.BOLD));
			
			for(String error : product.getErrors("price")) {
				priceButton.addLoreLine(UI.color(" - " + error, UI.ERROR_COLOR));
			}
		}
		
		// Set click listener
		priceButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());
			
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			
		    Conversation conversation = factory.withFirstPrompt(new NumericPrompt() {
				@Override
				public String getPromptText(ConversationContext context) {
					return UI.color("What should be the price of the product?", UI.PRIMARY_COLOR);
				}

				@Override
				protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
					product.setPrice(input.floatValue());
					Menus.PRODUCT_EDIT.open(player, product);
					return null;
				}
			}).withLocalEcho(false).buildConversation(whoClicked);
		    
		    whoClicked.closeInventory();
		    conversation.begin();
		});
		
		menu.setButton(priceButton, 30);
		
		// Commands button
		// ===========================
		MenuItemBuilder commandsButton = new MenuItemBuilder(Material.COMMAND_BLOCK);
		
		commandsButton.setName(UI.color("Commands", UI.PRIMARY_COLOR, ChatColor.BOLD));
		commandsButton.setLore(UI.color("Click to set the product commands.", UI.TEXT_COLOR), "", UI.color("Current value:", UI.TEXT_COLOR));
		
		// Get commands
		List<Command> commands = new ArrayList<>();
		
		if(commands.size() > 0) {
			for(Command command : commands) {
				descriptionButton.addLoreLine(UI.color(command.getCommand(), UI.SECONDARY_COLOR));
			}
		} else {
			commandsButton.addLoreLine(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
		}
		
		commandsButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
			Menus.PRODUCT_COMMANDS.open(player, product);
		});
		
		menu.setButton(commandsButton, 32);
		
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
		
		cancelButton.setClickListener(new OpenProductList());
		
		menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);
		
		// Save button
		// ===========================
		MenuItemBuilder saveButton = Menu.SAVE_BUTTON.clone();
		
		saveButton.setClickListener((whoClicked, activeMenu, clickedItem, clickType) -> {
			saveButton.setLore(UI.color("Saving...", UI.TEXT_COLOR));
			menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);
			
			// Save product
			productService.saveProduct(product, success -> {
				if(success) {
					OpenProductList action = new OpenProductList();
					action.onClick(whoClicked, menu, clickedItem, clickType);
					return;
				}
				
				// Refresh menu
				whoClicked.playSound(whoClicked.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
				Menus.PRODUCT_EDIT.open(player, product);
			});
		});
		
		menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);
		
		return menu;
	}

}
