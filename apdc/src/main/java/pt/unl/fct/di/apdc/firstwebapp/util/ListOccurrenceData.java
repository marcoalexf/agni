package pt.unl.fct.di.apdc.firstwebapp.util;

public class ListOccurrenceData {

	public String title;
	public String type;
	public int level;
	public boolean visibility;
	public AuthToken token;
	public double lat;
	public double lon;

	
	public ListOccurrenceData() {
		
	}
	
	public ListOccurrenceData(String title, String type, int level, boolean visibility, AuthToken token, double lat, double lon) {
		this.title = title;
		this.type = type;
		this.level = level;
		this.visibility = visibility;
		this.token = token;
		this.lat = lat;
		this.lon = lon;
	}
}
