package nl.timvandijkhuizen.commerce.config.objects;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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

    public StoreCurrency(String code, float conversionRate, String format, char groupingSeparator, char decimalSeparator) {
        this.code = code;
        this.conversionRate = conversionRate;
        this.format = parseFormat(format, groupingSeparator, decimalSeparator);
    }

    public StoreCurrency() { }

    @Override
    public void serialize(ConfigObjectData output) throws Throwable {
        output.set("code", code);
        output.set("conversionRate", conversionRate);
        
        // Serialize format
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        
        output.set("format", format.toPattern());
        output.set("groupingSeparator", symbols.getGroupingSeparator());
        output.set("decimalSeparator", symbols.getDecimalSeparator());
    }

    @Override
    public void deserialize(ConfigObjectData input) throws Throwable {
        code = input.getString("code");
        conversionRate = input.getFloat("conversionRate");
        
        // Deserialize format
        char groupingSeparator = input.getChar("groupingSeparator");
        char decimalSeparator = input.getChar("decimalSeparator");
        
        format = parseFormat(input.getString("format"), groupingSeparator, decimalSeparator);
    }

    @Override
    public String getItemName() {
        return code;
    }

    @Override
    public String[] getItemLore() {
        return new String[] {
            UI.color("Conversion rate: ", UI.COLOR_TEXT) + UI.color("" + conversionRate, UI.COLOR_SECONDARY),
            UI.color("Format: ", UI.COLOR_TEXT) + UI.color(format.toPattern(), UI.COLOR_SECONDARY)
        };
    }

    @Override
    public void getInput(Player player, Runnable callback) {
        ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

        // Get the format including grouping and decimal separator
        // ====================================================
        StringPrompt formatPrompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the format?", UI.COLOR_PRIMARY);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String pattern) {
                Prompt firstStep = this;
                
                // Next get the grouping separator
                return new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return UI.color("What should be the grouping separator?", UI.COLOR_PRIMARY);
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String grouping) {
                        if(grouping.length() > 1) {
                            context.getForWhom().sendRawMessage(UI.color("The grouping separator must be a character, please try again.", UI.COLOR_ERROR));
                            UI.playSound(player, UI.SOUND_ERROR);
                            return this;
                        }
                        
                        // Next get the decimal separator
                        return new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext context) {
                                return UI.color("What should be the decimal separator?", UI.COLOR_PRIMARY);
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String decimal) {
                                if(decimal.length() > 1) {
                                    context.getForWhom().sendRawMessage(UI.color("The decimal separator must be a character, please try again.", UI.COLOR_ERROR));
                                    UI.playSound(player, UI.SOUND_ERROR);
                                    return this;
                                }
                                
                                try {
                                    format = parseFormat(pattern, grouping.charAt(0), decimal.charAt(0));
                                    callback.run();
                                    return null;
                                } catch (IllegalArgumentException e) {
                                    context.getForWhom().sendRawMessage(UI.color("Invalid format, please try again.", UI.COLOR_ERROR));
                                    UI.playSound(player, UI.SOUND_ERROR);
                                    return firstStep;
                                }
                            }
                        };
                    }
                };
            }
        };

        // Get the conversion rate
        // ====================================================
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

        // Get the name
        // ====================================================
        StringPrompt namePrompt = new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
                return UI.color("What should be the code?", UI.COLOR_PRIMARY);
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.length() > 255) {
                    context.getForWhom().sendRawMessage(UI.color("The currency code cannot be longer than 255 characters, please try again.", UI.COLOR_ERROR));
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
    
    private DecimalFormat parseFormat(String pattern, char groupingSeparator, char decimalSeparator) throws IllegalArgumentException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        
        symbols.setGroupingSeparator(groupingSeparator);
        symbols.setDecimalSeparator(decimalSeparator);

        return new DecimalFormat(pattern, symbols);
    }

}
