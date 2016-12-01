package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerMain extends Observable {
	
	public static ObservableList<String> availableClients = FXCollections.observableArrayList();
	private static Map<String, String> accounts = new HashMap <String, String>(); //map each clients ID to Password; 
	
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			
//			for(String ID : availableClients){
//				setChanged();
//				notifyObservers("online#" + ID);
//			}
			
			System.out.println("got a connection");
		}
	}
	
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			String addUserList = "";
			String newUserCheck = "";
			
			try {
				while ((message = reader.readLine()) != null) {
					int i = 0;
					System.out.println("server read "+ message);
					
					//check if message is soley for creating an account
					String line = message;
					Scanner lineProcess = new Scanner(line);
					String code = lineProcess.next();
					if(code.equals("/crt")){
						String ID = lineProcess.next();
						String Password = lineProcess.next();
						accounts.put(ID, Password);
						System.out.println("Server added user: " + ID + ", with Password: " + Password);
					}
					else if(code.equals("/log")){
						String ID = lineProcess.next();
						String Password = lineProcess.next();
						if(accounts.containsKey(ID) && (accounts.get(ID).equals(Password))){
							message = "true";
						}
						else{
							message = "false";
						}
					}
					else{
					
					while((i < message.length())){
						if(message.charAt(i) == '#'){
							i++;
							break;
						}
						newUserCheck = newUserCheck + message.charAt(i);
						i++;
					}
					if (newUserCheck.equals("New User") == true){
						addUserList = message.substring(i, message.length());
						availableClients.add(addUserList);
					}
					
					}
					
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	/**
	 * 
	 * @param line is a scanner connected to a line of String
	 * @return a List of Strings that holds all IDs of clients in a group
	 */
	private ArrayList<String> parseIDs(Scanner line){
		ArrayList<String> allIDs = new ArrayList<String>();
		
		while(line.hasNext()){
			String ID = line.next();
			allIDs.add(ID);
		}
		return allIDs;
	}
}