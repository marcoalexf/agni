package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ListOccurrenceData {

	public AuthToken token;
	public boolean showPrivate = false;
	public String username;
	
	public ListOccurrenceData() {
		
	}
	
	public ListOccurrenceData(AuthToken token, boolean showPrivate, String username) {
		this.token = token;
		this.showPrivate = showPrivate;
		this.username = username;
	}
	
	public ListOccurrenceData(AuthToken token) {
		this.token = token;
		this.showPrivate = true;
	}
	
	
	public ListOccurrenceData(String username) {
		this.username = username;
	}
	
}
