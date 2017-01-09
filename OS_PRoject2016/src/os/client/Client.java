package os.client;


import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client{
	private Socket requestSocket;
	private ObjectOutputStream out;
 	private ObjectInputStream in;
 	private String message="";
 	private int userChoice = 0;
 	private String response = "";
 	private String ipaddress;
 	private Scanner stdin;
 	private MenuUI menu;
 	
	Client(){
		menu = new MenuUI();
	}
	
	public void run()
	{
		stdin = new Scanner(System.in);
		try{
			//1. creating a socket to connect to the server
			System.out.println("Please Enter IP Address of server to connect");
			ipaddress = stdin.next();
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
						menu.startMenu(); // show menu options for a start
						response = (String)in.readObject();//read in server response
						
						//read in user choice. I'm using do/while loop to make sure only allowed options can go through
						do{
							userChoice = stdin.nextInt();//read in user choice
						}while(userChoice < 0 || userChoice > 3);
						
						switch(userChoice){
						case 1:
							message = "register";
							break;
						case 2:
							message = "login";
							break;
						case 3:
							message = "bye";
							break;
							
						}
						
						sendMessage(message);//send user's chosen option to server
						
						response = (String)in.readObject();
						
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
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
}
