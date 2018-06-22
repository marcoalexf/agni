package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceEditData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public String title;
	public String description;
	public Boolean visibility;
	public Boolean notificationOnResolve;
	public Boolean uploadMedia;
	public int nUploads;


	
	public OccurrenceEditData() {
		
	}
	
	public OccurrenceEditData(AuthToken token, Long userID, Long occurrenceID, String title, 
			String description,  Boolean visibility, Boolean notificationOnResolve, 
			Boolean uploadMedia, int nUploads) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.title = title;
		this.description = description;
		this.visibility = visibility;
		this.notificationOnResolve = notificationOnResolve;
		this.uploadMedia = uploadMedia;
		this.nUploads = nUploads;
	}
	
	public boolean valid() {
		return occurrenceID != null && userID != null;
	}
	
}
