package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceLikeCountData {
	
	public Long userID;
	public Long occurrenceID;

	
	public OccurrenceLikeCountData() {
		
	}
	
	public OccurrenceLikeCountData(Long userID,  Long occurrenceID) {
		this.userID = userID;
		this.occurrenceID = occurrenceID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null;
	}
	
}
