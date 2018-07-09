package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class UserModData {
	
	public AuthToken token;
	public Long userID;
	public String entity;
	
	public UserModData() {
		
	}
	
	public UserModData(AuthToken token, Long userID, String entity) {
		this.userID = userID;
		this.token = token;
		this.entity = entity;
	}
	
	public boolean valid() {
		return userID != null && token != null && entity != null;
	}
	
}
