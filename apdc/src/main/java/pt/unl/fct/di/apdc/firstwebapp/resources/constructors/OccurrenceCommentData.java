package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceCommentData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public String comment;

	
	public OccurrenceCommentData() {
		
	}
	
	public OccurrenceCommentData(AuthToken token, Long userID,  Long occurrenceID, String comment) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.comment = comment;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && token != null && comment != null && !comment.isEmpty();
	}
	
}
