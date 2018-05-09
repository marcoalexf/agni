package pt.unl.fct.di.apdc.firstwebapp.util;

public class ProfileInformationData {

	public String username;
	public AuthToken token;
	
	public ProfileInformationData() {
		
	}
	
	public ProfileInformationData(String username, AuthToken token) {
		this.username = username;
		this.token = token;
	}
	
}
