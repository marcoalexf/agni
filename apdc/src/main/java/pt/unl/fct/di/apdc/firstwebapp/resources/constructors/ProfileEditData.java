package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileEditData {

	public String username;
	public AuthToken token;
	public String role;
	public String district;
	public String county;
	public String locality;
	public String email;
	public String newUsername;
	public String password;
	public String newPassword;
	public boolean uploadPhoto;
	
	public ProfileEditData() {
		
	}
	
	public ProfileEditData(AuthToken token, String username, String newUsername, String password, 
			String newPassword, String email, String district, String county, String locality, 
			boolean uploadPhoto) {
		this.username = username;
		this.token = token;
		this.newUsername = newUsername;
		this.password = password;
		this.newPassword = newPassword;
		this.email = email;
		this.district = district;
		this.county = county;
		this.locality = locality;
		this.uploadPhoto = uploadPhoto;
	}

	public boolean valid() {
		return token != null && username != null;
	}
	
}
