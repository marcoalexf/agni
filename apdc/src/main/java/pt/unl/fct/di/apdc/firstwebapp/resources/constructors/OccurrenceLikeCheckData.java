package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceLikeCheckData {
	
	public Long userID;
	public Long userOccurrenceID;
	public Long occurrenceID;
	public AuthToken token;

	
	public OccurrenceLikeCheckData() {
		
	}
	
	public OccurrenceLikeCheckData(AuthToken token, Long userID, Long userOccurrenceID,  Long occurrenceID) {
		this.token = token;
		this.userID = userID;
		this.userOccurrenceID = userOccurrenceID;
		this.occurrenceID = occurrenceID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && userOccurrenceID != null && token != null;
	}
	
}
