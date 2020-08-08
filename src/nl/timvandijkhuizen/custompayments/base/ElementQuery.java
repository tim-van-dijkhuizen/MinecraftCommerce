package nl.timvandijkhuizen.custompayments.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ElementQuery {

	protected HashMap<String, Object> criteria = new HashMap<>();
	private Integer limit;
	
	public Map<String, Object> getCriteria() {
		return Collections.unmodifiableMap(criteria);
	}
	
	public void id(int id) {
		criteria.put("id", id);
	}
	
	public void limit(int limit) {
		this.limit = limit;
	}
	
	public Integer getLimit() {
		return limit;
	}
	
}
