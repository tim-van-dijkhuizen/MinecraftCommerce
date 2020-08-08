package nl.timvandijkhuizen.custompayments.storage.query;

import nl.timvandijkhuizen.custompayments.base.ElementQuery;

public class ProductQuery extends ElementQuery {

	public void name(String name) {
		criteria.put("name", name);
	}
	
	public void description(String description) {
		criteria.put("description", description);
	}
	
	public void price(float price) {
		criteria.put("price", price);
	}
	
}
