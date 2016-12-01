/* ChatRoom <ErrorBox.java>
 * EE422C Project 7 submission by
 * <Pratyush Behera>
 * <pb22426>
 * <Minkoo Park>
 * <mp32454>
 * <16480>
 * Slip days used: <1>
 * Fall 2016
 */
package assignment7;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class ErrorBox{
	 
	/**
	 * display pop-up error message box that describes error type
	 * and print out an instruction message for the user
	 * @param title title of the window is the erro type
	 * @param message message in the window is the instruction
	 */
	public static void displayError(String title, String message){
		Stage window = new Stage();
		//create error message window
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(200);
		window.setMinHeight(200);
		//label holds the error message
		Label label = new Label();
		label.setText(message);
		label.setFont(new Font(20));
		Button closeBtn = new Button("Close");
		closeBtn.setPrefWidth(250);
		closeBtn.setOnAction(e -> window.close());
		//VBox layout for the button and label
		VBox layout = new VBox(80);
		layout.getChildren().addAll(label, closeBtn);
		layout.setPadding(new Insets(60,20,20,20));
		layout.setAlignment(Pos.CENTER);
		//set scene, and show window
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}

}
