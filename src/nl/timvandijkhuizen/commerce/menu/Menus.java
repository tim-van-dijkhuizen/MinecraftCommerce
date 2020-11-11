package nl.timvandijkhuizen.commerce.menu;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.menu.content.MenuHome;
import nl.timvandijkhuizen.commerce.menu.content.categories.MenuCategoryEdit;
import nl.timvandijkhuizen.commerce.menu.content.categories.MenuCategoryIcon;
import nl.timvandijkhuizen.commerce.menu.content.categories.MenuCategoryList;
import nl.timvandijkhuizen.commerce.menu.content.config.MenuConfig;
import nl.timvandijkhuizen.commerce.menu.content.fields.MenuFieldEdit;
import nl.timvandijkhuizen.commerce.menu.content.fields.MenuFieldIcon;
import nl.timvandijkhuizen.commerce.menu.content.fields.MenuFieldList;
import nl.timvandijkhuizen.commerce.menu.content.fields.MenuFieldType;
import nl.timvandijkhuizen.commerce.menu.content.gateways.MenuGatewayConfig;
import nl.timvandijkhuizen.commerce.menu.content.gateways.MenuGatewayEdit;
import nl.timvandijkhuizen.commerce.menu.content.gateways.MenuGatewayList;
import nl.timvandijkhuizen.commerce.menu.content.gateways.MenuGatewayType;
import nl.timvandijkhuizen.commerce.menu.content.orders.MenuOrderFields;
import nl.timvandijkhuizen.commerce.menu.content.orders.MenuOrderItems;
import nl.timvandijkhuizen.commerce.menu.content.orders.MenuOrderList;
import nl.timvandijkhuizen.commerce.menu.content.orders.MenuOrderView;
import nl.timvandijkhuizen.commerce.menu.content.products.MenuProductCategory;
import nl.timvandijkhuizen.commerce.menu.content.products.MenuProductCommands;
import nl.timvandijkhuizen.commerce.menu.content.products.MenuProductEdit;
import nl.timvandijkhuizen.commerce.menu.content.products.MenuProductIcon;
import nl.timvandijkhuizen.commerce.menu.content.products.MenuProductList;
import nl.timvandijkhuizen.commerce.menu.content.shop.MenuShopCategories;
import nl.timvandijkhuizen.commerce.menu.content.shop.MenuShopCurrency;
import nl.timvandijkhuizen.commerce.menu.content.shop.MenuShopProducts;
import nl.timvandijkhuizen.commerce.menu.content.shop.account.MenuShopAccount;
import nl.timvandijkhuizen.commerce.menu.content.shop.account.MenuShopAccountOrders;
import nl.timvandijkhuizen.commerce.menu.content.shop.account.MenuShopPreferences;
import nl.timvandijkhuizen.commerce.menu.content.shop.checkout.MenuShopCart;
import nl.timvandijkhuizen.commerce.menu.content.shop.checkout.MenuShopFields;
import nl.timvandijkhuizen.commerce.menu.content.shop.checkout.MenuShopGateway;
import nl.timvandijkhuizen.commerce.menu.content.shop.checkout.MenuShopPayment;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
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
    GATEWAY_OPTIONS(new MenuGatewayConfig()),

    ORDER_LIST(new MenuOrderList()),
    ORDER_VIEW(new MenuOrderView()),
    ORDER_ITEMS(new MenuOrderItems()),
    ORDER_FIELDS(new MenuOrderFields()),

    // User
    SHOP_CATEGORIES(new MenuShopCategories()),
    SHOP_PRODUCTS(new MenuShopProducts()),
    SHOP_CURRENCY(new MenuShopCurrency()),
    
    SHOP_ACCOUNT(new MenuShopAccount()),
    SHOP_ACCOUNT_PREFERENCES(new MenuShopPreferences()),
    SHOP_ACCOUNT_ORDERS(new MenuShopAccountOrders()),

    SHOP_CART(new MenuShopCart()),
    SHOP_FIELDS(new MenuShopFields()),
    SHOP_GATEWAY(new MenuShopGateway()),
    SHOP_PAYMENT(new MenuShopPayment());

    private PredefinedMenu predefinedMenu;

    Menus(PredefinedMenu predefinedMenu) {
        this.predefinedMenu = predefinedMenu;
    }

    public void open(Player player, Object... rawArgs) {
        DataArguments args = new DataArguments(rawArgs);
        Menu menu = predefinedMenu.create(player, args);

        if (menu instanceof PagedMenu) {
            ((PagedMenu) menu).open(player, 0);
        } else {
            menu.open(player);
        }
    }

}
