package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class ProfileEditData {

	public String username;
	public AuthToken token;
	public String role;
	public String district;
	public String county;
	public String locality;
	public String email;
	
	public ProfileEditData() {
		
	}
	
	public ProfileEditData(AuthToken token, String username, String email, String district, String county, String locality) {
		this.username = username;
		this.token = token;
		this.email = email;
		this.district = district;
		this.county = county;
		this.locality = locality;
	}

	public boolean valid() {
		return token != null && username != null && username != "";
	}
	
}
