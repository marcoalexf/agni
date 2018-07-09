package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceAcceptVerifyData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceUserID;
	public Long occurrenceID;

	
	public OccurrenceAcceptVerifyData() {
		
	}
	
	public OccurrenceAcceptVerifyData(AuthToken token, Long userID, Long occurrenceUserID, Long occurrenceID) {
		this.token = token;
		this.userID = userID;
		this.occurrenceUserID = occurrenceUserID;
		this.occurrenceID = occurrenceID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && token != null && occurrenceUserID != null;
	}
	
}
