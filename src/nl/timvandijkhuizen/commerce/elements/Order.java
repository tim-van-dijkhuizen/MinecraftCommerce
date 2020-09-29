package nl.timvandijkhuizen.commerce.elements;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataList;

public class Order extends Element {
    
    public static final String SCENARIO_FIELDS = "fields";
    public static final String SCENARIO_GATEWAYS = "gateways";
    public static final String SCENARIO_PAY = "pay";
    
    private String number;
    private UUID playerUniqueId;
    private String playerName;
    private StoreCurrency currency;
    private boolean completed;
    private DataList<LineItem> lineItems;
    private OrderFieldData fieldData;
    private Gateway gateway;
    
    private String paymentUrl;
    private boolean updatePaymentUrl;
    
    public Order(int id, String number, UUID playerUniqueId, String playerName, StoreCurrency currency, boolean completed, DataList<LineItem> lineItems, OrderFieldData fieldData, Gateway gateway, String paymentUrl) {
        this.setId(id);
        this.number = number;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.completed = completed;
        this.lineItems = lineItems;
        this.fieldData = fieldData;
        this.gateway = gateway;
        this.paymentUrl = paymentUrl;
    }
    
    public Order(String number, UUID playerUniqueId, String playerName, StoreCurrency currency) {
        this.number = number;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = new DataList<>();
        this.fieldData = new OrderFieldData();
    }
    
    @Override
    public boolean validate(String scenario) {
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
        
        if (fieldData == null) {
            addError("fields", "Fields is required");
            return false;
        }
        
        if(scenario.equals(SCENARIO_FIELDS) || scenario.equals(SCENARIO_PAY)) {
            Collection<ConfigOption<?>> options = fieldData.getOptions();
            boolean fieldsValid = true;
            
            for(ConfigOption<?> option : options) {
                if(option.isRequired() && option.isValueEmpty(fieldData)) {
                    addError(option.getPath(), "Field \"" + option.getName() + "\" is required");
                    fieldsValid = false;
                }
            }
            
            if(!fieldsValid) {
                return false;
            }
        }
        
        if((scenario.equals(SCENARIO_GATEWAYS) || scenario.equals(SCENARIO_PAY)) && gateway == null) {
        	addError("gateway", "Gateway is required");
        	return false;
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
    
    public OrderFieldData getFieldData() {
        return fieldData;
    }
    
    public Gateway getGateway() {
    	return gateway;
    }
    
    public void setGateway(Gateway gateway) {
    	this.gateway = gateway;
    }
    
    public String getPaymentUrl() {
        return paymentUrl;
    }
    
    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
        this.updatePaymentUrl = true;
    }
    
    public boolean updatePaymentUrl() {
        return updatePaymentUrl;
    }
    
}
