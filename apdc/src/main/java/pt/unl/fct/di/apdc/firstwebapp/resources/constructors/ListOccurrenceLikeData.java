package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ListOccurrenceLikeData {
	
	public AuthToken token;
	public Long userID;
	public String cursor;

	
	public ListOccurrenceLikeData() {
		
	}
	
	public ListOccurrenceLikeData(AuthToken token, Long userID, String cursor) {
		this.token = token;
		this.userID = userID;
		this.cursor = cursor;
	}
	
	public boolean valid() {
		return userID != null && token != null;
	}
	
}
