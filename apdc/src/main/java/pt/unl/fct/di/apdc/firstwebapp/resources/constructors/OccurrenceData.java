package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceData {
	
	public AuthToken token;
	public String title;
	public String description;
	public String type;
	public int level;
	public boolean visibility;
	public double lat;
	public double lon;
	public boolean notificationOnResolve;

	
	public OccurrenceData() {
		
	}
	
	public OccurrenceData(AuthToken token, String title, String description, String type, int level, 
			boolean visibility, double lat, double lon, boolean notificationOnResolve) {
		this.token = token;
		this.title = title;
		this.description = description;
		this.type = type;
		this.level = level;
		this.visibility = visibility;
		this.lat = lat;
		this.lon = lon;
		this.notificationOnResolve = notificationOnResolve;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	
	public boolean valid() {
		return nonEmptyField(title) && nonEmptyField(description) && nonEmptyField(type) && level != 0;
	}
	
}
