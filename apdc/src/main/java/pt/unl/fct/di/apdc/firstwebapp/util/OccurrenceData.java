package pt.unl.fct.di.apdc.firstwebapp.util;

public class OccurrenceData {

	public String title;
	public String type;
	public int level;
	public boolean visibility;
	public AuthToken token;

	
	public OccurrenceData() {
		
	}
	
	public OccurrenceData(String title, String type, int level, boolean visibility, AuthToken token) {
		this.title = title;
		this.type = type;
		this.level = level;
		this.visibility = visibility;
		this.token = token;
	}
}
