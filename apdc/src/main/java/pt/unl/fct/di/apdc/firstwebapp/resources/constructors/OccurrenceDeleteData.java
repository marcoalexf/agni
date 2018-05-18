package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceDeleteData {
	
	public AuthToken token;
	public String username;
	public long id;

	
	public OccurrenceDeleteData() {
		
	}
	
	public OccurrenceDeleteData(AuthToken token, String username,  long id) {
		this.token = token;
		this.username = username;
		this.id = id;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	
	public boolean valid() {
		return id != 0 && nonEmptyField(username);
	}
	
}
