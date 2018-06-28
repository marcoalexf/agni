package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceAcceptData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;

	
	public OccurrenceAcceptData() {
		
	}
	
	public OccurrenceAcceptData(AuthToken token, Long userID,  Long occurrenceID) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && token != null;
	}
	
}
