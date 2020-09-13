package nl.timvandijkhuizen.commerce.elements;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.OrderFields;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataList;

public class Order extends Element {
    
    public static final String SCENARIO_FIELDS = "fields";
    public static final String SCENARIO_PAY = "pay";
    
    private String number;
    private UUID playerUniqueId;
    private String playerName;
    private StoreCurrency currency;
    private boolean completed;
    private DataList<LineItem> lineItems;
    private OrderFields fields;
    
    public Order(int id, String number, UUID playerUniqueId, String playerName, StoreCurrency currency, boolean completed, DataList<LineItem> lineItems, OrderFields fields) {
        this.setId(id);
        this.number = number;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.completed = completed;
        this.lineItems = lineItems;
        this.fields = fields;
    }
    
    public Order(String number, UUID playerUniqueId, String playerName, StoreCurrency currency) {
        this.number = number;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = new DataList<>();
        this.fields = new OrderFields();
    }
    
    @Override
    public boolean validate(String scenario) {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        
        if (number == null || number.length() == 0) {
            addError("number", "Number is required");
            return false;
        }

        if (number.length() > 40) {
            addError("number", "Number cannot be longer than 20 characters");
            return false;
        }
        
        if (playerUniqueId == null) {
            addError("playerUniqueId", "Player unique id is required");
            return false;
        }
        
        if (playerName == null || playerName.length() == 0) {
            addError("playerName", "Player name is required");
            return false;
        }
        
        if (currency == null) {
            addError("currency", "Currency is required");
            return false;
        }
        
        if (fields == null) {
            addError("fields", "Fields is required");
            return false;
        }
        
        if(scenario.equals(SCENARIO_FIELDS) || scenario.equals(SCENARIO_PAY)) {
            boolean fieldsValid = true;
            
            for(Field field : fieldService.getFields()) {
                ConfigOption<?> option = field.getOption();
                
                if(option.isRequired() && option.isValueEmpty(fields)) {
                    addError("fields." + field.getId(), "Field \"" + field.getName() + "\" is required");
                    fieldsValid = false;
                }
            }
            
            if(!fieldsValid) {
                return false;
            }
        }
        
        return true;
    }
    
    public String getNumber() {
        return number;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public StoreCurrency getCurrency() {
        return currency;
    }
    
    public void setCurrency(StoreCurrency currency) {
        this.currency = currency;
    }

    public boolean isCompleted() {
        return completed;
    }
    
    public float getTotal() {
        float total = 0;
        
        for(LineItem item : getLineItems()) {
            total += item.getPrice();
        }
        
        return total;
    }
    
    public void addLineItem(LineItem lineItem) {
        Stream<LineItem> stream = StreamSupport.stream(lineItems.spliterator(), false);
        
        // Merge with existing or create new
        LineItem existing = stream
            .filter(i -> i.getProduct().getId() == lineItem.getProduct().getId())
            .findFirst()
            .orElse(null);
        
        if(existing != null) {
            existing.setQuantity(existing.getQuantity() + lineItem.getQuantity());
        } else {
            lineItems.add(lineItem);
        }
    }
    
    public void removeLineItem(LineItem lineItem) {
        lineItems.remove(lineItem);
    }
    
    public DataList<LineItem> getLineItems() {
        return lineItems;
    }
    
    public OrderFields getFields() {
        return fields;
    }
    
}
