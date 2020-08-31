package nl.timvandijkhuizen.custompayments.config.objects;

import java.text.DecimalFormat;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.spigotutils.config.ConfigObject;
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
    
    public StoreCurrency() { }
    
    @Override
    public void serialize(ByteArrayDataOutput output) {
        output.writeUTF(code);
        output.writeFloat(conversionRate);
        output.writeUTF(format.toPattern());
    }

    @Override
    public void deserialize(ByteArrayDataInput input) {
        code = input.readUTF();
        conversionRate = input.readFloat();
        format = new DecimalFormat(input.readUTF());
    }

    @Override
    public String getItemName() {
        return code;
    }
    
    @Override
    public String[] getItemLore() {
        return new String[] {
            UI.color("Conversion rate: ", UI.TEXT_COLOR) + UI.color("" + conversionRate, UI.SECONDARY_COLOR),
            UI.color("Format: ", UI.TEXT_COLOR) + UI.color(format.toPattern(), UI.SECONDARY_COLOR)
        };
    }
    
    @Override
    public void getInput(Player player, Runnable callback) {
        ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

        StringPrompt formatPrompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the format?", UI.PRIMARY_COLOR);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                try {
                    format = new DecimalFormat(input);
                    callback.run();
                    return null;
                } catch(IllegalArgumentException e) {
                    context.getForWhom().sendRawMessage(UI.color("Invalid format, please try again.", UI.ERROR_COLOR));
                    return this;
                }
            }
        };
        
        NumericPrompt conversionRatePrompt = new NumericPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the conversion rate?", UI.PRIMARY_COLOR);
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
                return UI.color("What should be the code?", UI.PRIMARY_COLOR);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                code = input;
                return conversionRatePrompt;
            }
        };

        factory.withFirstPrompt(namePrompt)
            .withLocalEcho(false)
            .buildConversation(player)
            .begin();
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
        if(obj instanceof StoreCurrency) {
            StoreCurrency currency = (StoreCurrency) obj;
            
            return currency.getCode().equals(code)
                && currency.getConversionRate() == conversionRate
                && currency.getFormat().equals(format);
        } else {
            return false;
        }
    }
    
}
