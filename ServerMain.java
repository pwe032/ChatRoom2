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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerMain extends Observable {
	
	public static ObservableList<String> availableClients = FXCollections.observableArrayList();
	public static Map<String, ArrayList<String>> getAllIds = new HashMap <String, ArrayList<String>>(); //create a map that holds groupName as string key and List of ID's as values
	
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
					
					if(message.charAt(i) == '$'){}
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
}