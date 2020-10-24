package nl.timvandijkhuizen.commerce.base;

public abstract class Element extends Model implements ElementInterface {

    private Integer id;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        if (this.id != null) {
            throw new RuntimeException("Id already set");
        }

        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Element && obj.getClass() == getClass() && ((Element) obj).getId().equals(getId());
    }

}