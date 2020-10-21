package nl.timvandijkhuizen.commerce.webserver.templating;

import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.spigotutils.helpers.ExceptionHelper;

public class TemplateFunctions {

    public String[] getStackTrace(Exception e) {
        return ExceptionHelper.getStackTrace(e);
    }

    public String formatPrice(float price, StoreCurrency currency) {
        return ShopHelper.formatPrice(price, currency);
    }

}
