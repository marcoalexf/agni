package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileData {

	public Long userID;
	public String username;
	public AuthToken token;
	
	public ProfileData() {
		
	}
	
	public ProfileData(Long userID, String username, AuthToken token) {
		this.userID = userID;
		this.username = username;
		this.token = token;
	}
	
}
