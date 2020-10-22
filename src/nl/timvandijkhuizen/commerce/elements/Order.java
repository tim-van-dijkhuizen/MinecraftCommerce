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

    private UUID uniqueId;
    private UUID playerUniqueId;
    private String playerName;
    private StoreCurrency currency;
    private DataList<LineItem> lineItems;
    private OrderFieldData fieldData;
    private Gateway gateway;
    private PaymentUrl paymentUrl;
    private Transaction transaction;

    public Order(int id, UUID uniqueId, UUID playerUniqueId, String playerName, StoreCurrency currency, DataList<LineItem> lineItems, OrderFieldData fieldData, Gateway gateway, PaymentUrl paymentUrl, Transaction transaction) {
        this.setId(id);
        this.uniqueId = uniqueId;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = lineItems;
        this.fieldData = fieldData;
        this.gateway = gateway;
        this.paymentUrl = paymentUrl;
        this.transaction = transaction;
    }

    public Order(UUID uniqueId, UUID playerUniqueId, String playerName, StoreCurrency currency) {
        this.uniqueId = uniqueId;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = new DataList<>();
        this.fieldData = new OrderFieldData();
    }

    @Override
    protected boolean validate(String scenario) {
        if (uniqueId == null) {
            addError("uniqueId", "Unique ID is required");
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

        if (scenario.equals(SCENARIO_FIELDS) || scenario.equals(SCENARIO_PAY)) {
            Collection<ConfigOption<?>> options = fieldData.getOptions();
            boolean fieldsValid = true;

            for (ConfigOption<?> option : options) {
                if (option.isRequired() && option.isValueEmpty(fieldData)) {
                    addError(option.getPath(), "Field \"" + option.getName() + "\" is required");
                    fieldsValid = false;
                }
            }

            if (!fieldsValid) {
                return false;
            }
        }

        if ((scenario.equals(SCENARIO_GATEWAYS) || scenario.equals(SCENARIO_PAY)) && gateway == null) {
            addError("gateway", "Gateway is required");
            return false;
        }

        return true;
    }

    public UUID getUniqueId() {
        return uniqueId;
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

    public float getTotal() {
        float total = 0;

        for (LineItem item : getLineItems()) {
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

        if (existing != null) {
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

    public PaymentUrl getPaymentUrl() {
        return paymentUrl;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }

}
