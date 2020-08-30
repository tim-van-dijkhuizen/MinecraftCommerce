package nl.timvandijkhuizen.custompayments.base;

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
    
    public StoreCurrency(String code, float conversionRate) {
        this.code = code;
        this.conversionRate = conversionRate;
    }
    
    public StoreCurrency() { }
    
    @Override
    public void serialize(ByteArrayDataOutput output) {
        output.writeUTF(code);
        output.writeFloat(conversionRate);
    }

    @Override
    public void deserialize(ByteArrayDataInput input) {
        code = input.readUTF();
        conversionRate = input.readFloat();
    }

    @Override
    public String getInputName() {
        return code;
    }
    
    @Override
    public void createNew(Player player, Runnable callback) {
        ConversationFactory factory = new ConversationFactory(CustomPayments.getInstance());

        NumericPrompt conversionRatePrompt = new NumericPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the conversion rate?", UI.PRIMARY_COLOR);
            }

            @Override
            protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                conversionRate = input.floatValue();
                callback.run();
                return null;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof StoreCurrency && ((StoreCurrency) obj).getCode().equals(code);
    }
    
}
