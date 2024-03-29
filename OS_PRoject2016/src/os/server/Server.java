package os.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
  public static void main(String[] args) throws Exception {
    ServerSocket m_ServerSocket = new ServerSocket(2004,10);
    int id = 0;
    while (true) {
      Socket clientSocket = m_ServerSocket.accept();
      System.out.println("Server is now running");
      id++;
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id);
      cliThread.start();
    }    
  }
}

//inner class looking after client requests
class ClientServiceThread extends Thread {
  private Socket clientSocket;
  private String message;
  private String request;
  private int clientID;  
  private boolean authenticated = false;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  
  //client file location for registration and logins.
  final String clientFile = "./Client_Details/Clients.txt";
  
  //file writer and bufferedwriter for saving new client details;
  private FileWriter fw;
  private BufferedWriter bfw;
  
  private Scanner fScanner;
  

  ClientServiceThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }

 
  public void run() {	  
    try 
    {
    	System.out.println("Connection successful!");
    	out = new ObjectOutputStream(clientSocket.getOutputStream());
		out.flush();
		in = new ObjectInputStream(clientSocket.getInputStream());
		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
		        + clientSocket.getInetAddress().getHostName());
		
		//sendMessage("Connection successful");
		do{
			try
			{
				authenticated = false;//refresh authentication variable
				message = "Make a choice please";				
				sendMessage(message);
				
				request = (String)in.readObject();
				
				//depending on user request, different action is taken
				switch(request){
				case "register":					
					registerClient();
					break;
				case "login":					
					loginClient();
					if(authenticated){
						accessAccount();
					}else{
						message = "Wrong credentials entered";
						sendMessage(message);
					}
					break;
				case "bye":
					disconnectClient();
					break;
				}
				
				
			}
			catch(ClassNotFoundException classnot){
				System.err.println("Data received in unknown format");
			}
			
    	}while(!message.equals("disconnect"));
      
		System.out.println("Ending Client : ID - " + clientID + " : Address - "
		        + clientSocket.getInetAddress().getHostName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    //close socket and data streams at the end.
    finally{
    	try {
			in.close();
			out.close();
			clientSocket.close();
			fScanner.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
  }
  
  private void accessAccount() {
	message = "login_Successful";
	sendMessage(message);
	File clientDetails = new File(clientFile);
	
	try {
		request = (String)in.readObject();
		System.out.println(request);
		
		fScanner = new Scanner(clientDetails);
		  fScanner.useDelimiter(",");//use delimiter of "," when reading file
		  
		  String[] namePassword = request.split("\\W+");
		  String curUserName = namePassword[0];
		  String curPassword = namePassword[1];
		 
		  while(fScanner.hasNextLine()){
			 String currentUser = fScanner.nextLine();
		  	 String[] details = currentUser.split("\\W+");
		 
			 String uName = details[0];//read in username
			 String uPass = details[1];//read in password
			 
			 if(uName.equalsIgnoreCase(curUserName) && uPass.equalsIgnoreCase(curPassword)){
				//send current user details to client
				 sendMessage(currentUser);
				 break;
			 }
			 
		  }
		  //received updated client details from client
		  request = (String)in.readObject();
		  System.out.println(request);
		  
		  //if client made updates to client data it invokes updateDetails method. Otherwise just a message is displayed.
		  if(!request.equalsIgnoreCase("NoUpdates")){
			  updateDetails(request);
		  }else{
			  System.out.println("No updates in this session");
		  }
		  
	} catch (ClassNotFoundException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

//method to update user details. It is not finished! currently only reads data from file and finds line in file that corresponds to line that should be replacing it.
private void updateDetails(String detailChange) {
	
	try {
		BufferedReader bfr = new BufferedReader(new FileReader(clientFile));
		
		
		  String[] uName_Account = detailChange.split(",");
		  String curUserName = uName_Account[0];
		  String curAccountNo = uName_Account[4];
		  System.out.println(curUserName+"-:-"+curAccountNo);
		  
		  String currentUser;
		 
		  while((currentUser = bfr.readLine()) != null){
			  
		  	 String[] details = currentUser.split(",");
		 
			 String uName = details[0];//read in username
			 String uAccount = details[4];//read in password
			 System.out.println("Current line: "+uName+"-:-"+uAccount);
			 if(uName.equalsIgnoreCase(curUserName) && uAccount.equalsIgnoreCase(curAccountNo)){
				//show that application finds existing line in file.
				System.out.println("Current line on file: "+currentUser);
				System.out.println("Replacement line: "+detailChange);	
					 
			 }		 
		  	
			
		  }
		  
		  //close reader and writer once they are done
		  bfr.close();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


//method to send message string
  private void sendMessage(String msg)
 	{
 		try{
 			out.writeObject(msg);
 			out.flush(); 	
 			//System.out.println("client "+clientID+": "+ msg);
 		}
 		catch(IOException ioException){
 			ioException.printStackTrace();
 		}
 	}
  
  
  //method to register new client
  private void registerClient(){
	  String cDetails;	  
	  ClientDetails client = new ClientDetails();
	  boolean firstClient = false;
	 
	  //File writer and buffered writer declaration.
	 try {
		File cFile = new File(clientFile);
		
		//check if file exists
		if(!cFile.exists()){
			cFile.createNewFile();//create new file if doesnt exist
			firstClient = true;//set boolean to true. this means next client entry will be first one in the file.
		}
		
		
		fw = new FileWriter(clientFile, true);
		bfw = new BufferedWriter(fw);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
	  try {
		  //get client name
		sendMessage("Please enter your full name:");
		cDetails = (String)in.readObject();
		client.setName(cDetails);//set name in client details
		
		//get client address
		sendMessage("Please enter your address:");
		cDetails = (String)in.readObject();
		client.setAddress(cDetails);//set address 
		
		//get client account number
		sendMessage("Please enter your account number:");
		cDetails = (String)in.readObject();
		client.setAccNumber(cDetails);
		
		//get client username
		sendMessage("Please enter your username:");
		cDetails = (String)in.readObject();
		client.setUsername(cDetails);
		
		//get client password
		sendMessage("Please enter your password");
		cDetails = (String)in.readObject();
		client.setPassword(cDetails);
				
		
		client.setBalance(1000);//set initial balance for client to 1000.
		
		//save details into file. Username and password goes first for future login checks.
		//following snippet of code checks if client entry is first in file or not. if not, it prepends new line character to front of it.
		if(firstClient){
			firstClient = false;
		}else{
			bfw.write("\n");
		}
		
		bfw.write(client.getUsername());
		bfw.write(","+client.getPassword());
		bfw.write(","+client.getName());
		bfw.write(","+client.getAddress());
		bfw.write(","+client.getAccNumber());
		bfw.write(","+client.getBalance()+",");		
		bfw.close();//close buffered writer
		//send message to client showing newly created client's details
		sendMessage("New Client: "+client.toString()+" Created");
		
	} catch (ClassNotFoundException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
  }
  
  //method to login existing client
  private boolean loginClient(){
	  String uNameCheck;
	  String passCheck;
	  File clientLogin = new File(clientFile);	
	  
	  try {
		  message = "Please enter your username";
		  sendMessage(message);
		  uNameCheck = (String)in.readObject();
		  
		  message = "Please enter your password";
		  sendMessage(message);
		  passCheck = (String)in.readObject();
		  
		  //start up file reader and Scanner to read in user details
		 	  
		  fScanner = new Scanner(clientLogin);
		  fScanner.useDelimiter(",");//use delimiter of "," when reading file
		  
		  while(fScanner.hasNextLine()){
			  
		  	 String[] details = fScanner.nextLine().split("\\W+");
		 
			 String uName = details[0];//read in username
			 String uPass = details[1];//read in password			
			  
			  if(uNameCheck.equalsIgnoreCase(uName) && passCheck.equalsIgnoreCase(uPass)){
				  authenticated = true;
				  break;//break out of file reader
			  }			  
			  
		  }
		  
		  
		  
	} catch (ClassNotFoundException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
	  //now need file reader to read from client file. Find the details that match. if no details found then no authentication is provided.
	  
	  return authenticated;
	  
  }
  
  //method to disconnect client
  private void disconnectClient(){
	  message = "disconnect";
	  sendMessage(message);
	  authenticated = false;
  }
 
}

