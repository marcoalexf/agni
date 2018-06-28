package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceCommentDeleteData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public Long commentID;

	
	public OccurrenceCommentDeleteData() {
		
	}
	
	public OccurrenceCommentDeleteData(AuthToken token, Long userID,  Long occurrenceID, Long commentID, String comment) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.commentID = commentID;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && commentID != null && token != null;
	}
	
}
