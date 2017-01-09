package os.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
  private volatile boolean keepRunning = true;
  private ObjectOutputStream out;
  private ObjectInputStream in;

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		sendMessage("New Client: "+client.toString()+" Created");
		
	} catch (ClassNotFoundException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
  }
  
  //method to login existing client
  private void loginClient(){
	  message = "login requested";
	  sendMessage(message);
  }
  
  //method to disconnect client
  private void disconnectClient(){
	  message = "disconnect";
	  sendMessage(message);
  }
 
}

