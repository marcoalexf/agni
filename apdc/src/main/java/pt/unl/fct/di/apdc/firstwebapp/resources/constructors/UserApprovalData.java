package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class UserApprovalData {
	
	public AuthToken token;
	public Long userID;
	
	public UserApprovalData() {
		
	}
	
	public UserApprovalData(AuthToken token, Long userID) {
		this.userID = userID;
		this.token = token;
	}
	
	public boolean valid() {
		return userID != null && token != null;
	}
	
}
