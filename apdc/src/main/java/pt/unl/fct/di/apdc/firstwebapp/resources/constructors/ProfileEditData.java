package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileEditData {

	public String name;
	public AuthToken token;
	public Long userID;
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
	
	public ProfileEditData(AuthToken token, Long userID, String newUsername, String password, 
			String newPassword, String email, String name, String district, String county, String locality, 
			boolean uploadPhoto) {
		this.name = name;
		this.token = token;
		this.userID = userID;
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
		return token != null && userID != null;
	}
	
}
