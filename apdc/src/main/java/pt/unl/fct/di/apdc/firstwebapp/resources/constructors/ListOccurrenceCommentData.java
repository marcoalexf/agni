package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ListOccurrenceCommentData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public String cursor;

	
	public ListOccurrenceCommentData() {
		
	}
	
	public ListOccurrenceCommentData(AuthToken token, Long userID,  Long occurrenceID, String cursor) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.cursor = cursor;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && token != null;
	}
	
}
