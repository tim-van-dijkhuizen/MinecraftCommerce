package nl.timvandijkhuizen.commerce.webserver;

import java.util.Map;
import java.util.function.Function;

public class QueryParameters {

	private Map<String, String> params;
	
	public QueryParameters(Map<String, String> params) {
		this.params = params;
	}
	
	public String getString(String key) {
		return getValue(key, raw -> raw);
	}
	
	public Integer getInteger(String key) {
		return getValue(key, raw -> Integer.valueOf(raw));
	}
	
	public Float getFloat(String key) {
		return getValue(key, raw -> Float.valueOf(raw));
	}
	
	public Double getDouble(String key) {
		return getValue(key, raw -> Double.valueOf(raw));
	}
	
	public Long getLong(String key) {
		return getValue(key, raw -> Long.valueOf(raw));
	}
	
	public Boolean getBoolean(String key) {
		return getValue(key, raw -> Boolean.valueOf(raw));
	}
	
	private <T> T getValue(String key, Function<String, T> converter) {
		String value = params.get(key);
		
		if(value == null) {
			return null;
		}
		
		try {
			return converter.apply(value);
		} catch(Exception e) {
			return null;
		}
	}
	
}
