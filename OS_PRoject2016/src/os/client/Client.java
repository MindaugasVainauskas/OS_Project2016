package os.client;


import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client{
	private Socket requestSocket;
	private ObjectOutputStream out;
 	private ObjectInputStream in;
 	private String message="";
 	private int userChoice;
 	private String response;
 	private String ipaddress;
 	private Scanner scan;
 	private MenuUI menu;
 	
 	//user details that are checked at login.
 	private String userName;
	private String password;
 	private volatile boolean loggedIn = false;//login boolean. If true then user is connected.
 	
	Client(){
		menu = new MenuUI();
	}
	
	public void run()
	{
		scan = new Scanner(System.in);
		try{
			//1. creating a socket to connect to the server
			System.out.println("Please Enter IP Address of server to connect");
			ipaddress = scan.next();
			
			requestSocket = new Socket(ipaddress, 2004);
			System.out.println("Connected to "+ipaddress+" in port 2004");
			//2. get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			//3: Communicating with the server
			do{
				
				try
				{
					userChoice = 0;
					
					menu.startMenu(); // show menu options for a start
					response = (String)in.readObject();//receive response from server
					System.out.println(response);// print out response so client knows what is being asked 
					
					//read in user choice. I'm using do/while loop to make sure only allowed options can go through
					do{
						userChoice = scan.nextInt();//read in user choice
						scan.nextLine();//flush the scanner
						
					}while(userChoice < 0 || userChoice > 3);
					
					
					System.out.println("Current user choice: "+userChoice);
					switch(userChoice){
					case 1:
						registerClient(); // register new client
						break;
					case 2:							
						loginClient(); // login existing client 							
						break;
					case 3:
						exitApplication();	// exit application						
						break;
						
					}					
										
				}
				catch(ClassNotFoundException classNot)
				{
					System.err.println("data received in unknown format");
				}
			}while(!message.equals("bye"));
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client side entry>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//registration handling method
	private void registerClient(){
		message = "register";
		sendMessage(message);//send user's chosen option to server
		
		
		try {
			//response = (String)in.readObject();//receive response from server
			for(int i = 0; i < 5; i++){
				response = (String)in.readObject();//receive response from server asking for client details
				System.out.println(response);// print out response so client knows what is being asked 
				message = scan.nextLine();//read in client choice			
				sendMessage(message);
			}
			
			response = (String)in.readObject();//receive confirmation from server that client has been created.
			
			System.out.println(response);
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//login handling method
	private void loginClient(){
		
		int userSelection;
		
		message = "login";
		sendMessage(message);//send user's chosen option to server
		
		try {
			response = (String)in.readObject();
			System.out.println(response);
			userName = scan.nextLine();
			sendMessage(userName);
			
			response = (String)in.readObject();
			System.out.println(response);
			password = scan.nextLine();
			sendMessage(password);
			
			
			response = (String)in.readObject();
			if(response.equalsIgnoreCase("login_Successful")){
				System.out.println("Login was successful");
				loggedIn = true;				
			}else{
				System.out.println("Login was not successful!");
			}
			
			while(loggedIn){
				menu.selectionMenu();				
				userSelection = scan.nextInt();
				
				switch(userSelection){
				case 1:					
					changeDetails();//call method to change user details
					break;
				case 2:					
					makeDeposit();// call method to make lodgement
					break;
				case 3:					
					makeWithdrawal(); //call method to make withdrawal
					break;
				case 4:					
					viewHistory();//call method to view last 10 transactions user made.
					break;
				case 5:
					logOut();// call method to log out of current account					
					break;
				}
			}
			
			
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//log out of account
	private void logOut() {
		System.out.println("Logging out of current account");
		loggedIn = false;
		userName = "";
		password = "";
		
	}

	//check previous transaction history
	private void viewHistory() {
		System.out.println("View last 10 transactions not implemented yet");
		
	}

	//make a deposit/Lodgement
	private void makeDeposit() {
		System.out.println("Make Lodgement not implemented yet");
		
	}

	//make a withdrawal
	private void makeWithdrawal() {
		System.out.println("Make Withdrawal not implemented yet");
		
	}

	//change user details
	private void changeDetails() {
		System.out.println("Change details not implemented yet");
		System.out.println("Current username: "+userName);
		System.out.println("Current password: "+password);
		
		message = userName+","+password;
		sendMessage(message);
		
	}

	//method to exit application
	private void exitApplication(){
		message = "bye";
		sendMessage(message);//send user's chosen option to server
	}
}
