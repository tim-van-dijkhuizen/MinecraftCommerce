package nl.timvandijkhuizen.commerce.base;

public interface SourceElement<T> {
    
    T getEditableCopy();
    
    void updateFromCopy(T copy);

}
