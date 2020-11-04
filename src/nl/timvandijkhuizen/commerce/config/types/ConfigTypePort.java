package nl.timvandijkhuizen.commerce.config.types;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.OptionConfig;
import nl.timvandijkhuizen.spigotutils.config.types.ConfigTypeInteger;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ConfigTypePort extends ConfigTypeInteger {

    @Override
    public void getValueInput(OptionConfig config, ConfigOption<Integer> option, MenuClick event, Consumer<Integer> callback) {
        ConversationFactory factory = new ConversationFactory(PluginBase.getInstance());
        Player player = event.getPlayer();

        Conversation conversation = factory.withFirstPrompt(new NumericPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the new value?", UI.COLOR_PRIMARY);
            }

            @Override
            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                int value = input.intValue();

                // Check range
                if (value < 1 || value > Short.MAX_VALUE) {
                    context.getForWhom().sendRawMessage(UI.color("Invalid port, choose a port between 1 and " + Short.MAX_VALUE + ".", UI.COLOR_ERROR));
                    return this;
                }

                // Check server port
                if (value == Bukkit.getServer().getPort()) {
                    context.getForWhom().sendRawMessage(UI.color("This port is already being used by your server.", UI.COLOR_ERROR));
                    return this;
                }

                callback.accept(value);
                return null;
            }
        }).withLocalEcho(false).buildConversation(player);

        player.closeInventory();
        conversation.begin();
    }

}
