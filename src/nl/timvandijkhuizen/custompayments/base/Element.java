package nl.timvandijkhuizen.custompayments.base;

public abstract class Element extends Model {

	private Integer id;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		if(this.id != null) {
			throw new RuntimeException("Id already set");
		}
		
		this.id = id;
	}
	
}