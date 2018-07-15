package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class UserListData {
	
	public AuthToken token;
	public String entity;
	public String role;
	public Boolean waitingModApproval;
	public String cursor;

	
	public UserListData() {
		
	}
	
	public UserListData(AuthToken token, String entity, String role, Boolean waitingModApproval, String cursor) {
		this.token = token;
		this.entity = entity;
		this.role = role;
		this.waitingModApproval = waitingModApproval;
		this.cursor = cursor;
	}
	
	public boolean valid() {
		return token != null;
	}
	
}
