package nl.timvandijkhuizen.custompayments.base;

public interface Field<T> {

    /**
     * Returns the name of the field.
     * 
     * @return
     */
    public String getName();

    /**
     * Serializes the actual value into a string.
     * 
     * @param value
     * @return
     */
    public String serialize(T value);

    /**
     * Turns the serialized string back 
     * into the actual value/type.
     * 
     * @param value
     * @return
     */
    public T deserialize(String value);

}
