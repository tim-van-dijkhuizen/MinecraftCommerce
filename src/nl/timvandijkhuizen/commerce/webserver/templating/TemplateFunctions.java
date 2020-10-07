package nl.timvandijkhuizen.commerce.webserver.templating;

import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;

public class TemplateFunctions {

    public String formatPrice(float price, StoreCurrency currency) {
        return ShopHelper.formatPrice(price, currency);
    }
    
}
