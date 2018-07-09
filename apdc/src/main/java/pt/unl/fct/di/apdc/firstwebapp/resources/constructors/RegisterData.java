package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class RegisterData {

	public String username;
	public String password;
	public String name;
	public String email;
	public String role;
	public String district;
	public String county;
	public String locality;
	public boolean uploadPhoto;
	public String entity;

	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String password, String name, String email, String role, 
			String district, String county, String locality, boolean uploadPhoto, String entity) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.email = email;
		this.role = role;
		this.district = district;
		this.county = county;
		this.locality = locality;
		this.uploadPhoto = uploadPhoto;
		this.entity = entity;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	
	public boolean valid() {
		return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(email) 
				&& nonEmptyField(name) && email.contains("@") && nonEmptyField(role);
	}
	
}
