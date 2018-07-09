package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceAcceptListData {
	
	public AuthToken token;
	public Long userID;
	public String cursor;

	
	public OccurrenceAcceptListData() {
		
	}
	
	public OccurrenceAcceptListData(AuthToken token, Long userID, String cursor) {
		this.token = token;
		this.userID = userID;
		this.cursor = cursor;
	}
	
	public boolean valid() {
		return userID != null && token != null;
	}
	
}
