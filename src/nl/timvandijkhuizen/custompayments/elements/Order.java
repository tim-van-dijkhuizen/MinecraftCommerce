package nl.timvandijkhuizen.custompayments.elements;

import java.util.Currency;
import java.util.UUID;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Order extends Element {

    private String reference;
    private UUID playerUniqueId;
    private String playerName;
    private Currency currency;
    private boolean completed;

    public Order(int id, String reference, UUID playerUniqueId, String playerName, Currency currency, boolean completed) {
        this.setId(id);
        this.reference = reference;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.completed = completed;
    }
    
    public Order(String reference, UUID playerUniqueId, String playerName, Currency currency) {
        this.reference = reference;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
    }
    
    @Override
    public boolean validate() {
        return false;
    }
    
    public String getReference() {
        return reference;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isCompleted() {
        return completed;
    }
    
}
