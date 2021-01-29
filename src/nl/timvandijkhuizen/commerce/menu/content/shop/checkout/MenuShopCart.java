package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopCategories;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopFields;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCart implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Cart (1/4)", 3, 7, 1, 1, 2, 5, 6);

        // Create LineItem buttons
        Order cart = args.get(0);

        for (LineItem lineItem : cart.getLineItems()) {
            ProductSnapshot product = lineItem.getProduct();
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon(), lineItem.getQuantity());

            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                String[] description = WordUtils.wrap(product.getDescription(), 40).split("\n");
                List<String> lore = new ArrayList<>();
                
                // Create lore
                for (String line : description) {
                    lore.add(UI.color(line, UI.COLOR_TEXT));
                }

                lore.add("");
                lore.add(UI.color(ShopHelper.formatPrice(lineItem.getPrice(), cart.getCurrency()), UI.COLOR_SECONDARY));
                lore.add("");
                
                lore.add(UI.color("Left-click to increase the quantity.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                lore.add(UI.color("Right-click to decrease the quantity.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                
                return lore;
            });

            item.setClickListener(event -> {
                ClickType type = event.getClickType();
                List<String> oldLore = item.getLore();

                UI.playSound(player, UI.SOUND_CLICK);
                item.setLore(UI.color("Saving...", UI.COLOR_TEXT));
                menu.refresh();
                menu.disableItems();

                // Update LineItem
                if (type == ClickType.LEFT) {
                    lineItem.setQuantity(lineItem.getQuantity() + 1);
                } else if (type == ClickType.RIGHT) {
                    lineItem.setQuantity(lineItem.getQuantity() - 1);
                }

                // Save cart
                orderService.saveOrder(cart, success -> {
                    menu.enableItems();

                    if (success) {
                        UI.playSound(player, UI.SOUND_SUCCESS);

                        if (lineItem.getQuantity() > 0) {
                            item.setAmount(lineItem.getQuantity());
                            item.setLore(oldLore);
                        } else {
                            menu.removePagedItem(item);
                        }

                        menu.refresh();
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        item.setLore(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                        menu.refresh();
                    }
                });
            });

            menu.addPagedItem(item);
        }

        // Cart button
        menu.setItem(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 3);

        // Previous button
        MenuItemBuilder previousButton = MenuItems.BACK.clone();

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Shop Home", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopCategories());

        menu.setItem(previousButton, menu.getSize().getSlots() - 9);

        // Next (fields) button
        MenuItemBuilder nextButton = new MenuItemBuilder(XMaterial.OAK_SIGN);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));

        nextButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();

            lore.add(UI.color("Fields", UI.COLOR_TEXT));

            if (cart.getLineItems().size() == 0) {
                lore.add("");
                lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
                lore.add(UI.color(UI.TAB + Icon.SQUARE + " Add at least one item to your cart.", UI.COLOR_ERROR));
            }

            return lore;
        });

        nextButton.setClickListener(event -> {
            if (cart.getLineItems().size() > 0) {
                new ActionShopFields().onClick(event);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
            }
        });

        menu.setItem(nextButton, menu.getSize().getSlots() - 1);

        return menu;
    }

}
