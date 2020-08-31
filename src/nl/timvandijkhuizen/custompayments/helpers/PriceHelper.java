package nl.timvandijkhuizen.custompayments.helpers;

import java.text.DecimalFormat;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.StoreCurrency;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class PriceHelper {

    public static String localize(float price, StoreCurrency currency) {
        DecimalFormat format = currency.getFormat();
        return format.format(price *= currency.getConversionRate());
    }
    
    public static String localize(float price) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<StoreCurrency> option = config.getOption("general.baseCurrency");
        StoreCurrency baseCurrency = option.getValue(config);
        
        return localize(price, baseCurrency);
    }
    
}
