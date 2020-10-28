package nl.timvandijkhuizen.commerce.config.objects;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Model;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.spigotutils.config.ConfigObject;
import nl.timvandijkhuizen.spigotutils.config.ConfigObjectData;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class StoreCurrency extends Model implements ConfigObject {

    private Currency code;
    private Float conversionRate;
    private String pattern;
    private Character groupSeparator;
    private Character decimalSeparator;

    public StoreCurrency(Currency code, float conversionRate, String pattern, char groupSeparator, char decimalSeparator) {
        this.code = code;
        this.conversionRate = conversionRate;
        this.pattern = pattern;
        this.groupSeparator = groupSeparator;
        this.decimalSeparator = decimalSeparator;
    }

    public StoreCurrency() { }

    @Override
    public void serialize(ConfigObjectData output) throws Throwable {
        output.set("code", code.getCurrencyCode());
        output.set("conversionRate", conversionRate);
        output.set("pattern", pattern);
        output.set("groupingSeparator", groupSeparator);
        output.set("decimalSeparator", decimalSeparator);
    }

    @Override
    public void deserialize(ConfigObjectData input) throws Throwable {
        code = Currency.getInstance(input.getString("code"));
        conversionRate = input.getFloat("conversionRate");
        pattern = input.getString("pattern");
        groupSeparator = input.getChar("groupingSeparator");
        decimalSeparator = input.getChar("decimalSeparator");
    }
    
    @Override
    protected boolean validate(String scenario) {
        if(code == null) {
            addError("code", "Code is required");
            return false;
        }
        
        if(conversionRate == null) {
            addError("code", "Code is required");
            return false;
        }
        
        if(pattern == null || pattern.length() == 0) {
            addError("pattern", "Pattern is required");
            return false;
        }
        
        if(groupSeparator == null) {
            addError("groupSeparator", "Group separator is required");
            return false;
        }
        
        if(decimalSeparator == null) {
            addError("decimalSeparator", "Decimal separator is required");
            return false;
        }
        
        try {
            getFormat();
        } catch(IllegalArgumentException e) {
            addError("pattern", "Pattern must be a valid decimal format");
            return false;
        }
        
        return true;
    }

    @Override
    public String getItemName() {
        return code.getDisplayName();
    }

    @Override
    public String[] getItemLore() {
        return new String[] {
            UI.color("Conversion rate: ", UI.COLOR_TEXT) + UI.color(String.valueOf(conversionRate), UI.COLOR_SECONDARY),
            UI.color("Pattern: ", UI.COLOR_TEXT) + UI.color(pattern, UI.COLOR_SECONDARY),
            UI.color("Group separator: ", UI.COLOR_TEXT) + UI.color(String.valueOf(groupSeparator), UI.COLOR_SECONDARY),
            UI.color("Decimal separator: ", UI.COLOR_TEXT) + UI.color(String.valueOf(decimalSeparator), UI.COLOR_SECONDARY)
        };
    }

    @Override
    public void getInput(MenuItemClick event, Consumer<Boolean> callback) {
        boolean isNew = code == null;
        Menu menu = new Menu((isNew ? "Edit" : "Create") + " Currency", MenuSize.XL);
        Player player = event.getPlayer();

        // Currency button
        // ===========================
        MenuItemBuilder currencyButton = new MenuItemBuilder(XMaterial.SUNFLOWER);

        currencyButton.setName(UI.color("Code", UI.COLOR_PRIMARY, ChatColor.BOLD));

        currencyButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (code != null) {
                lore.add(UI.color(code.getDisplayName(), UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
            
            // Validation lore
            ValidationHelper.addErrorLore(lore, this, "code");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        currencyButton.setClickListener(clickEvent -> {
            PagedMenu currencyMenu = new PagedMenu("Select Currency", 3, 7, 1, 1, 1, 5, 7);

            for (Currency code : Currency.getAvailableCurrencies()) {
                MenuItemBuilder item = new MenuItemBuilder(XMaterial.SUNFLOWER);

                item.setName(UI.color(code.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
                item.setLore(UI.color("Code: ", UI.COLOR_TEXT) + UI.color(code.getCurrencyCode(), UI.COLOR_SECONDARY));

                if(this.code != null && this.code.getCurrencyCode().equals(code.getCurrencyCode())) {
                    item.addEnchantGlow();
                }
                
                item.setClickListener(itemClick -> {
                    this.code = code;
                    UI.playSound(player, UI.SOUND_CLICK);
                    menu.open(player);
                });

                currencyMenu.addPagedButton(item);
            }

            // Go back button
            MenuItemBuilder backButton = MenuItems.BACK.clone();

            backButton.setClickListener(backEvent -> {
                UI.playSound(player, UI.SOUND_CLICK);
                menu.open(player);
            });

            currencyMenu.setButton(backButton, currencyMenu.getSize().getSlots() - 9 + 3);
            
            currencyMenu.open(player);
        });

        menu.setButton(currencyButton, 11);

        // Conversion rate button
        // ===========================
        MenuItemBuilder conversionRateButton = new MenuItemBuilder(XMaterial.BREWING_STAND);

        conversionRateButton.setName(UI.color("Conversion Rate", UI.COLOR_PRIMARY, ChatColor.BOLD));

        conversionRateButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (conversionRate != null) {
                lore.add(UI.color(String.valueOf(conversionRate), UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Validation lore
            ValidationHelper.addErrorLore(lore, this, "conversionRate");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        conversionRateButton.setClickListener(clickEvent -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new NumericPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the conversion rate?", UI.COLOR_PRIMARY);
                }

                @Override
                protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
                    conversionRate = input.floatValue();
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(conversionRateButton, 13);

        // Pattern button
        // ===========================
        MenuItemBuilder patternButton = new MenuItemBuilder(XMaterial.CREEPER_BANNER_PATTERN);

        patternButton.setName(UI.color("Pattern", UI.COLOR_PRIMARY, ChatColor.BOLD));
        patternButton.hideAttributes();

        patternButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (pattern != null && pattern.length() > 0) {
                lore.add(UI.color(pattern, UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Validation lore
            ValidationHelper.addErrorLore(lore, this, "pattern");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        patternButton.setClickListener(clickEvent -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the pattern?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    pattern = input;
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(patternButton, 15);

        // Group separator button
        // ===========================
        MenuItemBuilder groupSeparatorButton = new MenuItemBuilder(XMaterial.PAPER);

        groupSeparatorButton.setName(UI.color("Group Separator", UI.COLOR_PRIMARY, ChatColor.BOLD));

        groupSeparatorButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (groupSeparator != null) {
                lore.add(UI.color(String.valueOf(groupSeparator), UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Validation lore
            ValidationHelper.addErrorLore(lore, this, "groupSeparator");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        groupSeparatorButton.setClickListener(clickEvent -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the group separator?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    groupSeparator = input.charAt(0);
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(groupSeparatorButton, 21);
        
        // Conversion rate button
        // ===========================
        MenuItemBuilder decimalSeparatorButton = new MenuItemBuilder(XMaterial.MAP);

        decimalSeparatorButton.setName(UI.color("Decimal Separator", UI.COLOR_PRIMARY, ChatColor.BOLD));

        decimalSeparatorButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (decimalSeparator != null) {
                lore.add(UI.color(String.valueOf(decimalSeparator), UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Validation lore
            ValidationHelper.addErrorLore(lore, this, "decimalSeparator");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        decimalSeparatorButton.setClickListener(clickEvent -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the decimal separator?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    decimalSeparator = input.charAt(0);
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(decimalSeparatorButton, 23);
        
        // Set bottom line
        // ===========================
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(clickEvent -> {
            UI.playSound(player, UI.SOUND_CANCEL);
            callback.accept(false);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (hasErrors()) {
                lore.add(UI.color("Error: Field contains an invalid value.", UI.COLOR_ERROR));
            }
            
            return lore;
        });

        saveButton.setClickListener(clickEvent -> {
            if(isValid()) {
                UI.playSound(player, UI.SOUND_CONFIRM);
                callback.accept(true);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
                menu.refresh();
            }
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);
        
        menu.open(player);
    }

    public Currency getCode() {
        return code;
    }

    public float getConversionRate() {
        return conversionRate;
    }

    public String getPattern() {
        return pattern;
    }
    
    public char getGroupSeparator() {
        return groupSeparator;
    }
    
    public char getDecimalSeparator() {
        return decimalSeparator;
    }

    public DecimalFormat getFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        
        symbols.setGroupingSeparator(groupSeparator);
        symbols.setDecimalSeparator(decimalSeparator);

        return new DecimalFormat(pattern, symbols);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreCurrency) {
            StoreCurrency currency = (StoreCurrency) obj;
            
            return currency.getCode().equals(code) && 
                currency.getConversionRate() == conversionRate && 
                currency.getPattern().equals(pattern) && 
                currency.getGroupSeparator() == groupSeparator && 
                currency.getDecimalSeparator() == decimalSeparator;
        }
        
        return false;
    }

}
