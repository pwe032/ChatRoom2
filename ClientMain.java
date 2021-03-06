package assignment7;
 
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ClientMain extends Application{
	private TextField tf = new TextField();
	private TextArea ta = new TextArea();
	private ComboBox <String> chatters = new ComboBox<String>();
	private ComboBox <String> invitees = new ComboBox<String>();
	private ComboBox <String> requests = new ComboBox<String>();
	private BufferedReader reader;
	private PrintWriter writer; 
	private String ID; 
	private String Password;
	private String History = "";
	private boolean loginAllow;
	 
	/*static and private variables*/
	static GridPane userInterfaceGrid = new GridPane();
	static Stage userInterfaceStage = new Stage();
	static Scene userInterfaceScene = new Scene(userInterfaceGrid, 300,150);
	static Button createBtn = new Button();
	static TextField userID = new TextField();
	static PasswordField userPassword = new PasswordField();
	static ObservableList<String> availableClientsCur = FXCollections.observableArrayList();
	static ObservableList<String> friends = FXCollections.observableArrayList();
	static ObservableList<String> friendReq = FXCollections.observableArrayList();	


	public String getID(){
		return this.ID;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void run() throws Exception {
		initView();	//login feature
		setUpNetworking();
	}
 
	@Override
	public void start(Stage primaryStage) throws Exception {
		run();
	}
	
	public void initView(){
		//make user interface for chat application
				userInterfaceStage.setTitle("WELCOME!!!!");
				userInterfaceGrid.setPadding(new Insets(10,10,10,10));
				userInterfaceGrid.setVgap(20);
				userInterfaceGrid.setHgap(10);
				//ID, Password input, and create button
				userID.setPromptText("ID");
				userPassword.setPromptText("Password");
				createBtn.setText("Create");
				Button login = new Button();
				login.setText("Log in");
				
				GridPane.setConstraints(login, 1, 2);
				GridPane.setConstraints(userID, 0, 1);
				GridPane.setConstraints(userPassword, 0, 2);
				GridPane.setConstraints(createBtn, 1, 1); 			
				/*all action handlers start here*/
				createBtn.setOnAction(e -> {
					String id = userID.getText();
					String Password = userPassword.getText();
					this.ID = id;
					this.Password = Password;
					String saveAccount = "/crt " + id + " " + Password; // "/crt id password"  //
					writer.println(saveAccount);											   //
					writer.flush();                                                            //
					writer.println("New User#"  + userID.getText());
					writer.flush();
					
					userInterfaceStage.close();
					availableClientsCur.add("Everyone");
					chatters.setItems(availableClientsCur);
					friends.add("HISTORY");
					invitees.setItems(friends);
					initView2();//actual chat room
				});
				
				login.setOnAction(e -> {
					//send a message to a server that the user wants to login
					String id = userID.getText();
					String Password = userPassword.getText();
					this.ID = id;
					this.Password = Password;
					writer.println("/log " + id + " " + Password); // a request to log in
					writer.flush();
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					if(loginAllow == true){
						writer.println("New User#" + userID.getText());
						writer.flush();
						userInterfaceStage.close();
						availableClientsCur.add("Everyone");
						chatters.setItems(availableClientsCur);
						initView2();
					}
					else
						ErrorBox.displayError("Login Failed", "Account not found");
				});
				userInterfaceGrid.getChildren().addAll(userID, userPassword,createBtn, login);
				userInterfaceStage.setScene(userInterfaceScene);
				userInterfaceStage.show();	
	}
	
	public void initView2(){
		Stage chatInterface = new Stage();
		//make chat room interface
		Button sendBtn = new Button("Send");
		sendBtn.setPrefHeight(50);
		Button logoffBtn = new Button("LOGOFF");
		Button changePw = new Button("Change Password");
		Button leaveBtn = new Button("LEAVE");
		// Panel p to hold the label and text field 
		GridPane paneForTextField = new GridPane(); 
		paneForTextField.setHgap(5.0);
		paneForTextField.setVgap(10.0);
		paneForTextField.setPadding(new Insets(10, 10, 10, 10)); 
		paneForTextField.setStyle("-fx-border-color: pink");  
		tf.setPromptText("Enter your message...."); 
		tf.setPrefHeight(50);
		ScrollPane area = new ScrollPane(ta);
		ta.setPrefSize(470, 200); 
		//message input layout
		HBox input = new HBox(); 
		input.setSpacing(15);
		tf.setPrefWidth(400); 
		input.getChildren().add(tf);
		input.getChildren().add(sendBtn);
		//dropdown menu layout
//		ComboBox <String> chatters = new ComboBox<String>();
		chatters.setPrefWidth(330);
		Label lb = new Label("Friends:");
		HBox invite = new HBox();
		invite.setSpacing(15);
		invite.getChildren().addAll(lb, invitees);
		//group adding interface
		TextField groupName = new TextField();
		groupName.setPrefWidth(110);
		groupName.setPromptText("Group name");
		TextField groupMembers = new TextField();
		groupMembers.setPrefWidth(275);
		groupMembers.setPromptText("Enter all members to invite");
		Button makeGroup = new Button("make");
		HBox addGroup = new HBox();
		addGroup.setSpacing(15);
		addGroup.getChildren().addAll(groupName, groupMembers,makeGroup);
		//person invite interface
		Label availableList = new Label("Available:");
		Button inviteBtn = new Button("Invite");
		invitees.setPrefWidth(310);
		HBox invitePerson = new HBox();
		invitePerson.setSpacing(19);
		invitePerson.getChildren().addAll(availableList, chatters, inviteBtn);
		//friendRequest
		Label request = new Label ("Request: "); 
		requests.setPrefWidth(200);
		Button accept = new Button("Accept");
		Button decline = new Button("Decline");
		HBox requestInf = new HBox();
		requestInf.setSpacing(15);
		requestInf.getChildren().addAll(request, requests, accept, decline);
		//Buttons that are useful
		HBox buttonInf = new HBox();
		buttonInf.setSpacing(15);
		buttonInf.getChildren().addAll(logoffBtn, changePw);
		
		GridPane.setConstraints(requestInf, 0, 5);
		GridPane.setConstraints(invitePerson, 0, 3);
		GridPane.setConstraints(addGroup,0, 4);
		GridPane.setConstraints(input,0,1);
		GridPane.setConstraints(invite,0,2);
		GridPane.setConstraints(buttonInf, 0, 6);
		paneForTextField.getChildren().addAll(buttonInf, requestInf, invitePerson, addGroup, area, input, invite);


		// Create a scene and place it in the stage 
		Scene scene = new Scene(paneForTextField, 500, 550); 
		chatInterface.setTitle("Chat Room for " + ID); // Set the stage title 
		chatInterface.setScene(scene); // Place the scene in the stage 
		chatInterface.show(); // Display the stage 	
		
		sendBtn.setOnAction( e -> {
			//figure our receiver's ID
			String receiver = invitees.getValue();
			String sender = ID;
			// if sending to Group, all clients must accept
//			if (receiver.equals("Everyone")){
//				String x = "$" + ID + ": " + tf.getText();
//			writer.println("$" + ID + ": " + tf.getText() + "#" + friends);
//			}
			//elsif it's a distinct group, then selected clients must accept
			if(receiver.charAt(0) == '*'){ 
				// syntax: *GroupName#senderID#actualMessage
				writer.println("@" + receiver + "#" + ID + ": " + tf.getText());
				writer.flush();
			}
			else if(receiver.equals("HISTORY")){
				ta.appendText("******THIS IS YOUR HISTORY BELOW******\n" + History + "******THIS IS YOUR HISTORY ABOVE******\n");
			}
			//else it's one-to-one and only two clients must accept

			else{ 
				writer.println(sender + "#" + receiver + "#" + ID + ": " + tf.getText());
				writer.flush();
			}
			
			tf.setText("");
			tf.requestFocus();
		});
		
		tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
					//figure our receiver's ID
					String receiver = invitees.getValue();
					String sender = ID;
					// if sending to Group, all clients must accept
//					if (receiver.equals("Everyone")){
//						String x = "$" + ID + ": " + tf.getText();
//					writer.println("$" + ID + ": " + tf.getText());
//					}
					//elsif it's a distinct group, then selected clients must accept
					if(receiver.charAt(0) == '*'){ 
						// syntax: *GroupName#senderID#actualMessage
						writer.println("@" + receiver + "#" + ID + ": " + tf.getText());
						writer.flush();
					}
					else if(receiver.equals("HISTORY")){
						ta.appendText("******THIS IS YOUR HISTORY BELOW******\n" + History + "******THIS IS YOUR HISTORY ABOVE******\n");
					}
					//else it's one-to-one and only two clients must accept
					else{ 
					writer.println(sender + "#" + receiver + "#" + ID + ": " + tf.getText());
					writer.flush();
					}
					
					tf.setText("");
					tf.requestFocus();
		        	
		        	
		        }
		    }
		});
		
		makeGroup.setOnAction( e -> {
			String name = "*" + groupName.getText(); //get the group name from the textField
			if (friends.contains(name) == false){
			friends.add(name);//put it into the Observable List for drop-down menu
			invitees.setItems(friends);
			String Line = groupMembers.getText();//read a Line of String from textField and parse
			Scanner LineProcess = new Scanner (Line);
			ArrayList<String> groupIDs = parseIDs(LineProcess);// groupIDs holds all IDs of clients in a group
//			ServerMain.getAllIds.put(name, groupIDs); //holds groupName as string key and List of ID's as values
			// now, given "groupName" maps to appropriate "List of Cliend IDs" in that specific group"
			
			writer.println(name + "#" + groupIDs);
			writer.flush();
			}		
		});
		logoffBtn.setOnAction(e -> {
			availableClientsCur.remove(ID);
			chatInterface.close();
			
		});

		inviteBtn.setOnAction( e -> {
		
			String receiver = chatters.getValue();
			
			if (receiver.equals("Everyone")){
//				String x = "$" + ID + ": " + tf.getText();
			writer.println("$" + receiver + "#" + ID);
			}
			else{
			writer.println("%" + receiver + "#" + ID);
			}
			writer.flush();
			
			
		});
		
		accept.setOnAction( e -> {
			
			String receiver = requests.getValue();
			writer.println("+" + receiver + "#" + ID);
			writer.flush();
			
			
		});
		
		decline.setOnAction( e -> {
			
			String receiver = requests.getValue();
			writer.println("-" + receiver + "#" + ID);
			writer.flush();
			
			
		});
		
		changePw.setOnAction(e -> {
			String newPw = NewPassword.resetPassword();
			writer.println("/pw " + ID + " " + newPw);
			writer.flush();    
		});

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242); // 127.0.0.1 //128.62.23.210
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			String firstID;
			String secondID;
			
			try {
				while ((message = reader.readLine()) != null) { 
					System.out.println("server sent: " + message);
					int i = 0;
					int hashIndex1;
					int hashIndex2 = 0;
					String addOnlineList = "";
					String senderID = "";
					String receiverID = "";
					String actualMsg;
					
					if(message.charAt(i) == '$'){ // $ -> this is all group message
//						actualMsg = message.substring(1,message.length());
						
//						ta.appendText("---Following Message for Everyone---\n" +actualMsg + "\n");	
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							i++;
						}
						senderID = message.substring(i,message.length());
						if (senderID.equals(ID) == false){
						History = History + "--- Received Friend Request from: " +  senderID + "\n";
						ta.appendText("--- Received Friend Request from: " +  senderID + "\n");
						friendReq.add(senderID);
						requests.setItems(friendReq);
						}
						
						
					}
					

					else if(message.contains("true")){
						loginAllow = true;
					}
					else if(message.contains("false")){
						loginAllow = false;
					}
					else if(message.charAt(i) == '+'){
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							i++;
						}
						addOnlineList = message.substring(1,i-1);
						receiverID = message.substring(i,message.length());
						if (ID.equals(addOnlineList) && friends.contains(receiverID) == false){
							friends.add(receiverID);
							invitees.setItems(friends);
							History = History + "--- " + receiverID + " accepted your friend request!!!\n";
							ta.appendText("--- " + receiverID + " accepted your friend request!!!\n");
							
						}
						else if(ID.equals(receiverID) && friends.contains(addOnlineList) == false ) {
							friends.add(addOnlineList);
							invitees.setItems(friends);
							int idx = friendReq.indexOf(addOnlineList);
							friendReq.remove(idx);
							requests.setItems(friendReq);
						}
						
					}
					
					else if(message.charAt(i) == '-'){
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							i++;
						}
						addOnlineList = message.substring(1,i-1);
						receiverID = message.substring(i,message.length());
						if (ID.equals(addOnlineList) && friends.contains(receiverID) == false){
//							friends.add(receiverID);
//							invitees.setItems(friends);
							History = History + "--- " + receiverID + " denied your friend request.\n";
							ta.appendText("--- " + receiverID + " denied your friend request.\n");
						}
						else if(ID.equals(receiverID) && friends.contains(addOnlineList) == false ) {

							int idx = friendReq.indexOf(addOnlineList);
							friendReq.remove(idx);
							requests.setItems(friendReq);
						}
						
					}
					else if(message.charAt(i) == '%'){
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							i++;
						}
						
						addOnlineList = message.substring(1,i-1);
						if (addOnlineList.equals(ID)){
							senderID = message.substring(i,message.length());
							friendReq.add(senderID);
							requests.setItems(friendReq);
							History = History + "--- Received Friend Request from: " +  senderID + "\n";
							ta.appendText("--- Received Friend Request from: " +  senderID + "\n");
						}
						
						
					}
					else if(message.charAt(i) == '*'){ // * 
						String groupName = "";
						
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							senderID = senderID + message.charAt(i);
							i++;
						}
						groupName = message.substring(0,i-1);
						if (message.contains(ID) == true && friends.contains(groupName) == false){ //MUST CHANGE YA KNOW
							friends.add(groupName);
							invitees.setItems(friends);
						}

						
					}
					else if(message.charAt(i) == '@'){
						String groupName = "";
						String groupMsg = "";
						while((i < message.length())){
							if(message.charAt(i) == '#'){
								hashIndex1 = i;
								i++;
								break;
							}
							senderID = senderID + message.charAt(i);
							i++;
						}
						groupName = message.substring(1,i-1);
						groupMsg = message.substring(i,message.length()); 
						if (friends.contains(groupName) == true ){
							History = History + "--- Following Message for GroupChat: " + groupName + "\n" + groupMsg + "\n";
							ta.appendText("--- Following Message for GroupChat: " + groupName + "\n" + groupMsg + "\n");
						}		
					}
					else{
					while((i < message.length())){
						if(message.charAt(i) == '#'){
							hashIndex1 = i;
							i++;
							break;
						}
						senderID = senderID + message.charAt(i);
						i++;
					}
					if (senderID.equals("online") == true){
						addOnlineList = message.substring(i, message.length());
						if (addOnlineList==null){
						}
						else if(availableClientsCur.indexOf(addOnlineList) == -1 && addOnlineList.equals(ID) == false)
						{
							availableClientsCur.add(addOnlineList);
							chatters.setItems(availableClientsCur);
						}
					}
					else if (senderID.equals("New User") == true){
						addOnlineList = message.substring(i, message.length());
						if (addOnlineList.equals(ID) == false && availableClientsCur.contains(addOnlineList) == false){
							
						availableClientsCur.add(addOnlineList);
						chatters.setItems(availableClientsCur);
						}
						if (ID == null){}
						else{
						writer.println("online#"  + ID);
						writer.flush();
						}
					}
					else{
					while((i < message.length())){
						if(message.charAt(i) == '#'){
							i++;
							hashIndex2 = i;
							break;
						}
						receiverID = receiverID + message.charAt(i);
						i++;
					}
					actualMsg = message.substring(hashIndex2, message.length());			
					//print the wanted message IF the current client ID matches either receiverID or senderID
					if(idMatch(senderID, receiverID)){
						History = History + actualMsg + "\n";
						ta.appendText(actualMsg + "\n");						
					}
					
					}
				}			
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}	
	
	/**
	 * 
	 * @param id1 sender ID
	 * @param id2 receiver ID
	 * @return true if current client's ID matches id1 or id2
	 */
	private boolean idMatch(String id1, String id2){
		if(ID.equals(id1) || ID.equals(id2)){
			return true;
		}
		return false;
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