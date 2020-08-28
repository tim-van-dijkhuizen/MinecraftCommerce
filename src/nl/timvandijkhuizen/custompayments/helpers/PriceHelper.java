package nl.timvandijkhuizen.custompayments.helpers;

import java.text.NumberFormat;
import java.util.Currency;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class PriceHelper {

    public static String format(float price, Currency currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        
        format.setCurrency(currency);
        
        return format.format(price);
    }
    
    public static String format(float price) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<Currency> option = config.getOption("general.currency");
        return format(price, option.getValue(config));
    }
    
}
