package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceEditData {
	
	public AuthToken token;
	public String username;
	public long id;
	public String title;
	public String description;
	public String type;
	public int level;
	public boolean visibility;
	public double lat;
	public double lon;
	public boolean notificationOnResolve;

	
	public OccurrenceEditData() {
		
	}
	
	public OccurrenceEditData(AuthToken token, String username,  long id, String title, 
			String description, int level, boolean visibility, boolean notificationOnResolve) {
		this.token = token;
		this.username = username;
		this.id = id;
		this.title = title;
		this.description = description;
		this.level = level;
		this.visibility = visibility;
		this.notificationOnResolve = notificationOnResolve;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	
	public boolean valid() {
		return id != 0 && nonEmptyField(username);
	}
	
}
