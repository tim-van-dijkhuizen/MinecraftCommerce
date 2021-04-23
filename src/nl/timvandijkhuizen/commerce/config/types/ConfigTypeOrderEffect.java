package nl.timvandijkhuizen.commerce.config.types;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigType;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeOrderEffect implements ConfigType<OrderEffect> {

    @Override
    public OrderEffect getValue(OptionConfig config, ConfigOption<OrderEffect> option) {
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        Collection<OrderEffect> effects = orderService.getOrderEffects();
        String handle = config.getString(option.getPath());

        if (handle == null) {
            return null;
        }

        Optional<OrderEffect> effect = effects.stream()
            .filter(i -> i.getHandle().equals(handle))
            .findFirst();

        return effect.orElse(null);
    }

    @Override
    public void setValue(OptionConfig config, ConfigOption<OrderEffect> option, OrderEffect value) {
        config.set(option.getPath(), value != null ? value.getHandle() : null);
    }

    @Override
    public String getRawValue(OptionConfig config, ConfigOption<OrderEffect> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getHandle() : "";
    }
    
    @Override
    public String getDisplayValue(OptionConfig config, ConfigOption<OrderEffect> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getDisplayName() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<OrderEffect> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<OrderEffect> option, MenuClick event, Consumer<OrderEffect> callback) {
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        PagedMenu menu = new PagedMenu("Choose an effect", 3, 7, 1, 1, 1, 5, 7);
        Player player = event.getPlayer();
        OrderEffect selected = getValue(config, option);

        // Add available effects
        Collection<OrderEffect> effects = orderService.getOrderEffects();
        
        for (OrderEffect effect : effects) {
            MenuItemBuilder item = new MenuItemBuilder(effect.getIcon());

            item.setName(UI.color(effect.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            if (selected != null && effect.getHandle().equals(selected.getHandle())) {
                item.addEnchantGlow();
            }

            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(effect);
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