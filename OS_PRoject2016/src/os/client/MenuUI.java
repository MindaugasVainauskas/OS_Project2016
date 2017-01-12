package os.client;

public class MenuUI {
	
	//Initial menu users see when starting up the application
	public void startMenu(){
		System.out.println("Welcome. Please select one of the following options:");
		System.out.println("1 - Register account;");
		System.out.println("2 - Login with an existing account;");
		System.out.println("3 - Exit application;");
	}
	
	//menu users see when successfully logged in
	public void selectionMenu(){
		System.out.println("Select one of following options:");
		System.out.println("1 - Change details;");
		System.out.println("2 - Make Lodgement;");
		System.out.println("3 - Make withdrawal;");
		System.out.println("4 - View last 10 transactions made on this account;");
		System.out.println("5 - Log out of this account;");
	}
	
	public void detailChange(){
		System.out.println("Select one of following options to change details:");
		System.out.println("1 - Change password;");
		System.out.println("2 - Change name;");
		System.out.println("3 - Change address;");
		System.out.println("4 - Change password, name, and address;");
	}

}
