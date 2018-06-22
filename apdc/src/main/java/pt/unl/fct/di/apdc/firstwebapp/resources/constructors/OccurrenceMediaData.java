package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceMediaData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public Long mediaID;

	
	public OccurrenceMediaData() {
		
	}
	
	public OccurrenceMediaData(AuthToken token, Long userID, Long occurrenceID, Long mediaID) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.mediaID = mediaID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && mediaID != null;
	}
	
}
