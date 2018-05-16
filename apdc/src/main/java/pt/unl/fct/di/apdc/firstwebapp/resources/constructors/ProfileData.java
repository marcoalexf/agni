package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileData {

	public String username;
	public AuthToken token;
	
	public ProfileData() {
		
	}
	
	public ProfileData(String username, AuthToken token) {
		this.username = username;
		this.token = token;
	}
	
}
