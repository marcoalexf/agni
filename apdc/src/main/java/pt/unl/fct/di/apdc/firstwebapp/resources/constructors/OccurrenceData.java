package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceData {
	
	public AuthToken token;
	public String title;
	public String description;
	public String type;
	public int level;
	public boolean visibility;
	public float lat;
	public float lon;
	public boolean notificationOnResolve;
	public boolean uploadMedia;
	public int nUploads;

	
	public OccurrenceData() {
		
	}
	
	public OccurrenceData(AuthToken token, String title, String description, String type, int level, 
			boolean visibility, float lat, float lon, boolean notificationOnResolve, 
			boolean uploadMedia, int nUploads) {
		this.token = token;
		this.title = title;
		this.description = description;
		this.type = type;
		this.level = level;
		this.visibility = visibility;
		this.lat = lat;
		this.lon = lon;
		this.notificationOnResolve = notificationOnResolve;
		this.uploadMedia = uploadMedia;
		this.nUploads = nUploads;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	
	public boolean valid() {
		return nonEmptyField(title) && nonEmptyField(description) && nonEmptyField(type) && level != 0;
	}
	
}
