package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceResolveData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public boolean uploadMedia;
	public int nUploads;

	
	public OccurrenceResolveData() {
		
	}
	
	public OccurrenceResolveData(AuthToken token, Long userID,  Long occurrenceID, boolean uploadMedia, int nUploads) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.uploadMedia = uploadMedia;
		this.nUploads = nUploads;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && token != null;
	}
	
}
