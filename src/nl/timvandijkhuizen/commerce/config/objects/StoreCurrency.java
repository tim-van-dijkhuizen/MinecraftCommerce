package nl.timvandijkhuizen.commerce.config.objects;

import java.text.DecimalFormat;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.spigotutils.config.ConfigObject;
import nl.timvandijkhuizen.spigotutils.config.ConfigObjectData;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class StoreCurrency implements ConfigObject {

    private String code;
    private float conversionRate;
    private DecimalFormat format;

    public StoreCurrency(String code, float conversionRate, DecimalFormat format) {
        this.code = code;
        this.conversionRate = conversionRate;
        this.format = format;
    }

    public StoreCurrency() {
    }

    @Override
    public void serialize(ConfigObjectData output) {
        output.set("code", code);
        output.set("conversionRate", conversionRate);
        output.set("format", format.toPattern());
    }

    @Override
    public void deserialize(ConfigObjectData input) {
        code = input.getString("code");
        conversionRate = input.getFloat("conversionRate");
        format = new DecimalFormat(input.getString("format"));
    }

    @Override
    public String getItemName() {
        return code;
    }

    @Override
    public String[] getItemLore() {
        return new String[] {
            UI.color("Conversion rate: ", UI.COLOR_TEXT) + UI.color("" + conversionRate, UI.COLOR_SECONDARY), UI.color("Format: ", UI.COLOR_TEXT) + UI.color(format.toPattern(), UI.COLOR_SECONDARY)
        };
    }

    @Override
    public void getInput(Player player, Runnable callback) {
        ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

        StringPrompt formatPrompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the format?", UI.COLOR_PRIMARY);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                try {
                    format = new DecimalFormat(input);
                    callback.run();
                    return null;
                } catch (IllegalArgumentException e) {
                    context.getForWhom().sendRawMessage(UI.color("Invalid format, please try again.", UI.COLOR_ERROR));
                    return this;
                }
            }
        };

        NumericPrompt conversionRatePrompt = new NumericPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the conversion rate?", UI.COLOR_PRIMARY);
            }

            @Override
            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                conversionRate = input.floatValue();
                return formatPrompt;
            }
        };

        StringPrompt namePrompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the code?", UI.COLOR_PRIMARY);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.length() > 255) {
                    context.getForWhom().sendRawMessage(UI.color("The currency code cannot be longer than 255 characters", UI.COLOR_ERROR));
                    UI.playSound(player, UI.SOUND_ERROR);
                    return this;
                }

                code = input;
                return conversionRatePrompt;
            }
        };

        factory.withFirstPrompt(namePrompt).withLocalEcho(false).buildConversation(player).begin();
    }

    public String getCode() {
        return code;
    }

    public float getConversionRate() {
        return conversionRate;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreCurrency) {
            StoreCurrency currency = (StoreCurrency) obj;
            return currency.getCode().equals(code) && currency.getConversionRate() == conversionRate && currency.getFormat().equals(format);
        } else {
            return false;
        }
    }

}
