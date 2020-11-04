package nl.timvandijkhuizen.commerce.config.types;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeStoreCurrency implements ConfigType<StoreCurrency> {

    private boolean showDetails;
    
    public ConfigTypeStoreCurrency(boolean showDetails) {
        this.showDetails = showDetails;
    }
    
    public ConfigTypeStoreCurrency() { }
    
    @Override
    public StoreCurrency getValue(OptionConfig config, ConfigOption<StoreCurrency> option) {
        YamlConfig pluginConfig = Commerce.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = pluginConfig.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(pluginConfig);

        // Get store currency
        String code = config.getString(option.getPath());

        if (code == null) {
            return null;
        }

        Optional<StoreCurrency> currency = currencies.stream()
            .filter(i -> i.getCode().getCurrencyCode().equals(code))
            .findFirst();

        return currency.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<StoreCurrency> option, StoreCurrency value) {
        config.set(option.getPath(), value != null ? value.getCode().getCurrencyCode() : null);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<StoreCurrency> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getItemName() : "";
    }
    
    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<StoreCurrency> option) {
        return getRawValue(config, option);
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<StoreCurrency> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<StoreCurrency> option, MenuClick event, Consumer<StoreCurrency> callback) {
        PagedMenu menu = new PagedMenu("Choose a currency", 3, 7, 1, 1, 1, 5, 7);
        Player player = event.getPlayer();
        StoreCurrency selected = getValue(config, option);

        // Get available currencies
        YamlConfig pluginConfig = Commerce.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = pluginConfig.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(pluginConfig);

        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.SUNFLOWER);

            item.setName(UI.color(currency.getItemName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            if(showDetails) {
                item.addLore(UI.color("Conversion rate: ", UI.COLOR_TEXT) + UI.color(String.valueOf(currency.getConversionRate()), UI.COLOR_SECONDARY));
                item.addLore(UI.color("Pattern: ", UI.COLOR_TEXT) + UI.color(currency.getPattern(), UI.COLOR_SECONDARY));
                item.addLore(UI.color("Group separator: ", UI.COLOR_TEXT) + UI.color(String.valueOf(currency.getGroupSeparator()), UI.COLOR_SECONDARY));
                item.addLore(UI.color("Decimal separator: ", UI.COLOR_TEXT) + UI.color(String.valueOf(currency.getDecimalSeparator()), UI.COLOR_SECONDARY));
            }

            if (currency.equals(selected)) {
                item.addEnchantGlow();
            }

            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(currency);
            });

            menu.addPagedItem(item);
        }
        
        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(backEvent -> {
            UI.playSound(player, UI.SOUND_CLICK);
            callback.accept(selected);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        menu.open(player);
    }

}
