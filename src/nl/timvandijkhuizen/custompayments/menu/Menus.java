package nl.timvandijkhuizen.custompayments.menu;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.menu.content.MenuHome;
import nl.timvandijkhuizen.custompayments.menu.content.categories.MenuCategoryEdit;
import nl.timvandijkhuizen.custompayments.menu.content.categories.MenuCategoryIcon;
import nl.timvandijkhuizen.custompayments.menu.content.categories.MenuCategoryList;
import nl.timvandijkhuizen.custompayments.menu.content.config.MenuConfig;
import nl.timvandijkhuizen.custompayments.menu.content.fields.MenuFieldEdit;
import nl.timvandijkhuizen.custompayments.menu.content.fields.MenuFieldIcon;
import nl.timvandijkhuizen.custompayments.menu.content.fields.MenuFieldList;
import nl.timvandijkhuizen.custompayments.menu.content.fields.MenuFieldType;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayEdit;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayList;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayOptions;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayType;
import nl.timvandijkhuizen.custompayments.menu.content.orders.MenuOrderItems;
import nl.timvandijkhuizen.custompayments.menu.content.orders.MenuOrderList;
import nl.timvandijkhuizen.custompayments.menu.content.orders.MenuOrderView;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductCategory;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductCommands;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductEdit;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductIcon;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductList;
import nl.timvandijkhuizen.custompayments.menu.content.shop.MenuShopCategories;
import nl.timvandijkhuizen.custompayments.menu.content.shop.MenuShopCurrency;
import nl.timvandijkhuizen.custompayments.menu.content.shop.MenuShopProducts;
import nl.timvandijkhuizen.custompayments.menu.content.shop.checkout.MenuShopCart;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;

public enum Menus {

    // Administrator
    HOME(new MenuHome()),

    CONFIG(new MenuConfig()),

    CATEGORY_LIST(new MenuCategoryList()),
    CATEGORY_EDIT(new MenuCategoryEdit()),
    CATEGORY_ICON(new MenuCategoryIcon()),

    PRODUCT_LIST(new MenuProductList()),
    PRODUCT_EDIT(new MenuProductEdit()),
    PRODUCT_ICON(new MenuProductIcon()),
    PRODUCT_CATEGORY(new MenuProductCategory()),
    PRODUCT_COMMANDS(new MenuProductCommands()),

    FIELD_LIST(new MenuFieldList()),
    FIELD_EDIT(new MenuFieldEdit()),
    FIELD_ICON(new MenuFieldIcon()),
    FIELD_TYPE(new MenuFieldType()),

    GATEWAY_LIST(new MenuGatewayList()),
    GATEWAY_EDIT(new MenuGatewayEdit()),
    GATEWAY_TYPE(new MenuGatewayType()),
    GATEWAY_OPTIONS(new MenuGatewayOptions()),
    
    ORDER_LIST(new MenuOrderList()),
    ORDER_VIEW(new MenuOrderView()),
    ORDER_ITEMS(new MenuOrderItems()),
    
    // User
    SHOP_CATEGORIES(new MenuShopCategories()),
    SHOP_PRODUCTS(new MenuShopProducts()),
    SHOP_CURRENCY(new MenuShopCurrency()),
    SHOP_CART(new MenuShopCart());

    private PredefinedMenu predefinedMenu;

    Menus(PredefinedMenu predefinedMenu) {
        this.predefinedMenu = predefinedMenu;
    }

    public void open(Player player, Object... rawArgs) {
        MenuArguments args = new MenuArguments(rawArgs);
        Menu menu = predefinedMenu.create(player, args);

        if (menu instanceof PagedMenu) {
            ((PagedMenu) menu).open(player, 0);
        } else {
            menu.open(player);
        }
    }

}
