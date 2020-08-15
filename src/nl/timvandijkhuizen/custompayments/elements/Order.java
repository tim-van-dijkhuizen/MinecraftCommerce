package nl.timvandijkhuizen.custompayments.elements;

import java.util.UUID;

import nl.timvandijkhuizen.custompayments.base.Element;

public class Order extends Element {

	private UUID playerUniqueId;
	private String playerName;
	
	@Override
	protected boolean validate() {
		return false;
	}
	
	public UUID getPlayerUniqueId() {
		return playerUniqueId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

}
