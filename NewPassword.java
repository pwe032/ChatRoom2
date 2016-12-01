package assignment7;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewPassword {
	
	static String newPw;
	
	public static String resetPassword(){
		Stage window = new Stage();
		//create error message window
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle("New Password");
		window.setMinWidth(200);
		window.setMinHeight(200);
		//label holds the error message
		PasswordField pw = new PasswordField();
		Label label = new Label();
		label.setText("Enter New Password");
		label.setFont(new Font(15));
		Button closeBtn = new Button("Change");
		closeBtn.setPrefWidth(250);
		closeBtn.setOnAction(e -> {
			newPw = pw.getText();
			window.close();
		});
		//VBox layout for the button and label
		VBox layout = new VBox(25);
		layout.getChildren().addAll(label, pw, closeBtn);
		layout.setPadding(new Insets(60,20,20,20));
		layout.setAlignment(Pos.CENTER);
		//set scene, and show window
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
		return newPw;
	}
}
