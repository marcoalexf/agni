package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceCommentEditData {
	
	public AuthToken token;
	public Long userID;
	public Long occurrenceID;
	public Long commentID;
	public String comment;

	
	public OccurrenceCommentEditData() {
		
	}
	
	public OccurrenceCommentEditData(AuthToken token, Long userID,  Long occurrenceID, Long commentID, String comment) {
		this.token = token;
		this.userID = userID;
		this.occurrenceID = occurrenceID;
		this.commentID = commentID;
		this.comment = comment;
	}
	
	public boolean valid() {
		return userID != null && occurrenceID != null && commentID != null && token != null && comment != null && !comment.isEmpty();
	}
	
}
