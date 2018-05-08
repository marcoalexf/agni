package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

	public String username;
	public String password;
	public String confirmation;
	public String name;
	public String email;
	public String role;
	public int cellphone;
	public int telephone;
	public String address;
	public String addressExtra;
	public String addressCity;
	public String addressPostalCode;
	public int nif;
	public String cc;

	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String password, String confirmation, 
			String name, String email, String role, int cellphone, 
			int telephone, String address, String addressExtra, 
			String addressCity, String addressPostalCode, int nif, String cc) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.name = name;
		this.email = email;
		this.role = role;
		this.cellphone = cellphone;
		this.telephone = telephone;
		this.address = address;
		this.addressExtra = addressExtra;
		this.addressCity = addressCity;
		this.addressPostalCode = addressPostalCode;
		this.nif = nif;
		this.cc = cc;
	}
	
	private boolean nonEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	public boolean validRegistration() {
		return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(confirmation) && 
			   nonEmptyField(email) && nonEmptyField(name) && email.contains("@") && password.equals(confirmation) && 
			   nonEmptyField(role) && nonEmptyField(address) && nonEmptyField(addressCity) && nonEmptyField(addressPostalCode);
	}
}
