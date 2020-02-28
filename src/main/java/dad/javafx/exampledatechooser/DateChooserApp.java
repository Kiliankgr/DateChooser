package dad.javafx.exampledatechooser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DateChooserApp extends Application{
	DateChooserController controller;
	@Override
	public void start(Stage primaryStage) throws Exception {
		controller=new DateChooserController();
		Scene scene=new Scene(controller.getRoot());
		primaryStage.setTitle("Probando JDate");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}
	
}
