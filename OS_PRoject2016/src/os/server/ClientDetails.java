package os.server;

public class ClientDetails {
	//variables to hold client details
	private String name;
	private String Address;
	private String accNumber;
	private String username;
	private String password;
	
	public ClientDetails(){
		
	}

	//getters and setters for variables
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ClientDetails [name=" + name + ", Address=" + Address + ", accNumber=" + accNumber + ", username="
				+ username + ", password=" + password + "]";
	}

}
