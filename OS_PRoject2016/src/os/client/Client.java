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
		message = "login";
		sendMessage(message);//send user's chosen option to server
	}
	
	//method to exit application
	private void exitApplication(){
		message = "bye";
		sendMessage(message);//send user's chosen option to server
	}
}
