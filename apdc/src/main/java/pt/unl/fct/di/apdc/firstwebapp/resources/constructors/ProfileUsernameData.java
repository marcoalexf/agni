package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileUsernameData {

	public Long userID;
	
	public ProfileUsernameData() {
		
	}
	
	public ProfileUsernameData(Long userID) {
		this.userID = userID;
	}
	
	public boolean valid() {
		return userID != null;
	}
	
}
