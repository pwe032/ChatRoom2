package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;

public class ClientMain extends Application{
	private TextField tf = new TextField();
	private TextArea ta = new TextArea();
	private ComboBox <String> chatters = new ComboBox<String>();
	private BufferedReader reader;
	private PrintWriter writer; 
	private String ID;
	private String Password;
	 
	/*static and private variables*/
	static GridPane userInterfaceGrid = new GridPane();
	static Stage userInterfaceStage = new Stage();
	static Scene userInterfaceScene = new Scene(userInterfaceGrid, 300, 250);
	static Button createBtn = new Button();
	static TextField userID = new TextField();
	static PasswordField userPassword = new PasswordField();
	static ObservableList<String> availableClientsCur = FXCollections.observableArrayList();
	
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
				GridPane.setConstraints(userID, 0, 1);
				GridPane.setConstraints(userPassword, 0, 2);
				GridPane.setConstraints(createBtn, 1, 1); 			
				/*all action handlers start here*/
				createBtn.setOnAction(e -> {
					String id = userID.getText();
					String Password = userPassword.getText();
					this.ID = id;
					this.Password = Password;
//					ServerMain.allAccounts.add(ID);
//					ServerMain.availableClientsCur.add(ID);
					
					writer.println("New User#"  + userID.getText());
					writer.flush();
					
					userInterfaceStage.close();
					availableClientsCur.add("Group");
					chatters.setItems(availableClientsCur);
					initView2();//actual chat room
				});
				userInterfaceGrid.getChildren().addAll(userID, userPassword,createBtn);
				userInterfaceStage.setScene(userInterfaceScene);
				userInterfaceStage.show();	
	}
	
	public void initView2(){
		Stage chatInterface = new Stage();
		//make chat room interface
		Button sendBtn = new Button("Send");
		Button leaveBtn = new Button("LEAVE");
		// Panel p to hold the label and text field 
		GridPane paneForTextField = new GridPane(); 
		paneForTextField.setHgap(5.0);
		paneForTextField.setVgap(10.0);
		paneForTextField.setPadding(new Insets(10, 10, 10, 10)); 
		paneForTextField.setStyle("-fx-border-color: pink");  
		tf.setPromptText("Enter your message...."); 
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
//		chatters.setItems(ServerMain.availableClientsCur);
		Label lb = new Label("Friends: ");
		HBox invite = new HBox();
		invite.setSpacing(15);
		invite.getChildren().addAll(lb, chatters);
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
		
		GridPane.setConstraints(addGroup,0, 4);
		GridPane.setConstraints(leaveBtn, 0, 3);
		GridPane.setConstraints(input,0,1);
		GridPane.setConstraints(invite,0,2);
		GridPane.setConstraints(leaveBtn, 0, 7);
		paneForTextField.getChildren().addAll(addGroup, area, input, invite, leaveBtn);


		// Create a scene and place it in the stage 
		Scene scene = new Scene(paneForTextField, 500, 400); 
		chatInterface.setTitle("Chat Room for " + ID); // Set the stage title 
		chatInterface.setScene(scene); // Place the scene in the stage 
		chatInterface.show(); // Display the stage 	
		
		sendBtn.setOnAction( e -> {
			//figure our receiver's ID
			String receiver = chatters.getValue();
			String sender = ID;
			// if sending to Group, all clients must accept
			if (receiver.equals("Group")){
				String x = "$" + ID + ": " + tf.getText();
			writer.println("$" + ID + ": " + tf.getText());
			}
			//elsif it's a distinct group, then selected clients must accept
			else if(receiver.charAt(0) == '*'){ 
				// syntax: *GroupName#senderID#actualMessage
				writer.println(receiver + "#" + sender + "#" + ID + ": " + tf.getText());
			}
			//else it's one-to-one and only two clients must accept
			else{ 
			writer.println(sender + "#" + receiver + "#" + ID + ": " + tf.getText());
			}
			writer.flush();
			tf.setText("");
			tf.requestFocus();
		});
		
		makeGroup.setOnAction( e -> {
			String name = "*" + groupName.getText(); //get the group name from the textField
			availableClientsCur.add(name);//put it into the Observable List for drop-down menu
			String Line = groupMembers.getText();//read a Line of String from textField and parse
			Scanner LineProcess = new Scanner (Line);
			ArrayList<String> groupIDs = parseIDs(LineProcess);// groupIDs holds all IDs of clients in a group
			ServerMain.getAllIds.put(name, groupIDs); //holds groupName as string key and List of ID's as values
			// now, given "groupName" maps to appropriate "List of Cliend IDs" in that specific group"
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
					int i = 0;
					int hashIndex1;
					int hashIndex2 = 0;
					String addOnlineList = "";
					String senderID = "";
					String receiverID = "";
					String actualMsg;
					
					if(message.charAt(i) == '$'){
						actualMsg = message.substring(1,message.length());
						ta.appendText(actualMsg + "\n");						
					}	
					else if(message.charAt(i) == '*'){
						//message = *GroupName#senderID#actualMessage
						
						//get groupName
						//get the ArrayList that holds all IDs that belong to this group
						//   for all Clients
						//      if (Client.ID == one of the ID in the ArrayList)
						//		    Client accetps a message
						//		else
						//			Client does not accept a message
						//		endif
						//   end forLoop
						//done 
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
						if (addOnlineList.equals(null)){
						}
						else if(availableClientsCur.indexOf(addOnlineList) == -1 && addOnlineList.equals(ID) == false)
						{
							availableClientsCur.add(addOnlineList);
							chatters.setItems(availableClientsCur);
						}
					}
					else if (senderID.equals("New User") == true){
						addOnlineList = message.substring(i, message.length());
						if (addOnlineList.equals(ID) == false){
							
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