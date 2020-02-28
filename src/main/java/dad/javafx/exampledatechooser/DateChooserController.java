package dad.javafx.exampledatechooser;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import dad.javafx.components.DateChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

public class DateChooserController implements Initializable{
	//view
    @FXML
    private VBox root;

    @FXML
    private DateChooser fechaDateChooser;

    @FXML
    private Button inicializarButton;

    @FXML
    private Button consultarButton;
	public DateChooserController(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DateChooserExample.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		inicializarButton.setOnAction(e->onInicializar(e));
		consultarButton.setOnAction(e->onConsultar(e));
	}
	private void onInicializar(ActionEvent e) {
    	fechaDateChooser.nuevaFecha(LocalDate.now());

	}
	private void onConsultar(ActionEvent e) {
		Alert alert = new Alert(AlertType.NONE);
    	alert.setTitle("Fecha");
    	alert.initOwner(getRoot().getScene().getWindow());
    	
    	LocalDate fechaActual = fechaDateChooser.getFecha();
    	ButtonType aceptar=new ButtonType("Aceptar",ButtonData.CANCEL_CLOSE);
    	
    	String aux = fechaActual.getDayOfMonth()+"/"+ fechaActual.getMonthValue()+"/"+ fechaActual.getYear();
    	alert.setContentText("La fecha seleccionada es: " + aux);
    	alert.getDialogPane().getButtonTypes().add(aceptar);
    	
    	alert.showAndWait();

	}
	public VBox getRoot() {
		return root;
	}

}
