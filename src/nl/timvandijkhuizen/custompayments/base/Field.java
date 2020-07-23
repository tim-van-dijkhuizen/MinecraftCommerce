package nl.timvandijkhuizen.custompayments.base;

public interface Field<T> {

	public String getName();
	
	public String serialize(T value);
	public T deserialize(String value);
	
}
