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
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypeOrderEffect implements ConfigType<OrderEffect> {

    @Override
    public OrderEffect getValue(OptionConfig config, ConfigOption<OrderEffect> option) {
    	OrderService orderService = Commerce.getInstance().getService("orders");
    	Collection<OrderEffect> effects = orderService.getOrderEffects();
        String handle = config.getString(option.getPath());
        
        if(handle == null) {
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
    public String getValueLore(OptionConfig config, ConfigOption<OrderEffect> option) {
        return !isValueEmpty(config, option) ? getValue(config, option).getHandle() : "";
    }

    @Override
    public boolean isValueEmpty(OptionConfig config, ConfigOption<OrderEffect> option) {
        return getValue(config, option) == null;
    }

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<OrderEffect> option, MenuItemClick event, Consumer<OrderEffect> callback) {
    	OrderService orderService = Commerce.getInstance().getService("orders");
    	Collection<OrderEffect> effects = orderService.getOrderEffects();
        PagedMenu menu = new PagedMenu("Choose an effect", 3, 7, 1, 1);
        OrderEffect selected = getValue(config, option);
        Player player = event.getPlayer();

        for (OrderEffect effect : effects) {
            MenuItemBuilder item = new MenuItemBuilder(effect.getIcon());

            item.setName(UI.color(effect.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            if(selected != null && selected.getHandle().equals(effect.getHandle())) {
                item.addEnchantGlow();
            }
            
            item.setClickListener(itemClick -> {
                UI.playSound(player, UI.SOUND_CLICK);
                callback.accept(effect);
            });

            menu.addPagedButton(item);
        }
        
        menu.open(player);
    }

}