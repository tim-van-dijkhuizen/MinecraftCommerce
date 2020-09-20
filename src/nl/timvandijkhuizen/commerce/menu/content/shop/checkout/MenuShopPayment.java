package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.services.PaymentService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopPayment implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Cart " + Icon.ARROW_RIGHT + " Payment", MenuSize.XL);
        PaymentService paymentService = Commerce.getInstance().getService("payments");

        // Get cart
        Order cart = args.get(0);
        
        // Pay button
        MenuItemBuilder payButton = new MenuItemBuilder(Material.COMPARATOR);

        payButton.setName(UI.color("Pay Now", UI.COLOR_PRIMARY, ChatColor.BOLD));
        payButton.setLore(UI.color("Click here to pay now", UI.COLOR_TEXT));

        payButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            payButton.setLore(UI.color("Loading...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();
            
            paymentService.createPaymentUrl(cart, url -> {
            	menu.enableButtons();
            	
            	if(url != null) {
            		UI.playSound(player, UI.SOUND_SUCCESS);
            		player.closeInventory();
            		player.sendMessage(url);
            	} else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    payButton.setLore(UI.color("Failed to create payment url.", UI.COLOR_ERROR));
                    menu.refresh();
            	}
            });
        });

        menu.setButton(payButton, 11);

        return menu;
	}

}
