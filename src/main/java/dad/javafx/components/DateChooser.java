package dad.javafx.components;


import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class DateChooser extends HBox implements Initializable{
	//model
	private ListProperty<Integer> diaLista = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ObjectProperty<LocalDate> fecha=new SimpleObjectProperty<>();//fecha del componente
	private IntegerProperty dia = new SimpleIntegerProperty();
	//lista de meses
	private static String[] MESES= {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
	
	//years
	private static int minYear=1900;
	private int maxYear=minYear;
	
	//fecha actual
	LocalDate fechaActual;
	
	//formato
	DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	//view
	
    @FXML
    private ComboBox<Integer> diaCombo;

    @FXML
    private ComboBox<String> mesCombo;

    @FXML
    private ComboBox<String> yearCombo;
	
	public DateChooser () {
		super();
		try {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DateChooserView.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fechaActual=LocalDate.now();
		
		//asignamos dia
		diaCombo.itemsProperty().bind(diaLista);
		
		//asignamos meses
		mesCombo.getItems().addAll(MESES);
		mesCombo.getSelectionModel().selectFirst();
		
		//año tope que sera el actual
		maxYear=fechaActual.getYear();
		
		//asignamos años
		yearCombo.getItems().addAll(listadoYears());
		
		yearCombo.getSelectionModel().select(String.valueOf(fechaActual.getYear()));//año actual
		//al introducir algo para cambiar la fecha
		yearCombo.getEditor().textProperty().addListener(new  ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> o, String ov, String nv) {
					try {
						if(!nv.isEmpty()) {
							int year= Integer.parseInt(nv);
							//si no salta la excepcion es porque es un numero, entonces
							if(year>maxYear) {
								yearCombo.getSelectionModel().selectFirst();
							}else if( year < minYear) {
								yearCombo.getSelectionModel().selectLast();
							}else {
								yearCombo.getSelectionModel().select(year);
							}
						}
					} catch (NumberFormatException e) {
						yearCombo.getEditor().textProperty().removeListener(this); //evitamos que lo llame sin motivo
						yearCombo.getEditor().setText(ov); // al no haber introducido un numero no cambiamos el año
						yearCombo.getEditor().textProperty().addListener(this); // volvemos a colocar el listener
					}
			}
			
		});
		
		
		//al cambiar el mes recalculamos los dias
		mesCombo.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->{
			boolean nDiasRecalculado=false;
			
			YearMonth ym= YearMonth.of(Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem()), Month.of(mesCombo.getSelectionModel().getSelectedIndex()+1));
			Integer dias[] = IntStream.range(1, ym.lengthOfMonth()+1).boxed().toArray( Integer[]::new );
			
			int nDias = diaLista.getSize() > 0 ? diaCombo.getSelectionModel().getSelectedIndex() : 0; // Seleccionamos el mismo dia que antes

			// Ahora necesitamos recalcular si estábamos en el último dia del mes
			if( diaCombo.getSelectionModel().getSelectedIndex() == diaLista.getSize() - 1 && diaLista.getSize() > 0) {
				nDiasRecalculado = true;
			}
			
			diaLista.clear();
			diaLista.addAll(dias);
			
			if( nDiasRecalculado ) {
				diaCombo.getSelectionModel().selectLast();
			} else {
				diaCombo.getSelectionModel().select(nDias);
			}
			
			// al cambiar de mes actualizamos la fecha
			if( getFecha() != null )
				setFecha(LocalDate.of(getFecha().getYear(), ym.getMonth(), diaCombo.getSelectionModel().getSelectedItem()));
			
		});
		
		//lo mismo con el año
		yearCombo.getSelectionModel().selectedItemProperty().addListener((o,ov,nv)->{
			// Necesitamos recalcular los dias en el caso de estar en febrero
			if( mesCombo.getSelectionModel().getSelectedIndex() + 1 == Month.FEBRUARY.getValue()) {
				
				boolean recalculateDay = false;
				
				YearMonth ym = YearMonth.of(Integer.parseInt(nv), Month.of(mesCombo.getSelectionModel().getSelectedIndex()+1));;
				
				// Comprobamos si es bisiesto o no
				Integer dias[] = IntStream.range(1, ym.lengthOfMonth()+1).boxed().toArray( Integer[]::new );
				
				int nDias = diaLista.getSize() > 0 ? diaCombo.getSelectionModel().getSelectedIndex() : 0; // Seleccionamos el mismo dia que antes
				// Ahora necesitamos recalcular si teníamos el último día seleccionado
				if( diaCombo.getSelectionModel().getSelectedIndex() == diaLista.getSize() - 1 && diaLista.getSize() > 0) {
					recalculateDay = true;
				}
				
				diaLista.clear();
				diaLista.addAll(dias);
				
				if( recalculateDay ) {
					diaCombo.getSelectionModel().selectLast();
				} else {
					diaCombo.getSelectionModel().select(nDias);
				}
			}
			// Si el año cambia cambiamos la fecha
			if( getFecha() != null )
				setFecha(LocalDate.of(Integer.parseInt(nv), getFecha().getMonth(), diaCombo.getSelectionModel().getSelectedItem()));
		});
		Integer dias[] = IntStream.range(1,31+1).boxed().toArray( Integer[]::new );
		diaLista.addAll(dias);
		diaCombo.getSelectionModel().selectFirst();
		dia.bind( diaCombo.getSelectionModel().selectedItemProperty());
		
		setFecha(LocalDate.of(Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem()), 
				  Month.of(mesCombo.getSelectionModel().getSelectedIndex()+1),
				  diaCombo.getSelectionModel().getSelectedItem()));
		yearCombo.getSelectionModel().selectFirst();
		mesCombo.getSelectionModel().selectFirst();
		dia.addListener((o,ov,nv)->{
			try {
				if( getFecha() != null && nv.intValue() != 0) {
					setFecha(LocalDate.of(getFecha().getYear(), getFecha().getMonthValue(), nv.intValue()));		
				}
			} catch( Exception e ) {
				
			}  									
		});
	}
	private ArrayList<String> listadoYears() {
		ArrayList<String> years = new ArrayList<>();
		int i=maxYear;
		while ( i>= minYear){
		    years.add(String.valueOf(i));
		    i--;
		}
		return years;
	}
		
	public final ObjectProperty<LocalDate> fechaProperty() {
		return this.fecha;
	}
	
	public final LocalDate getFecha() {
		return this.fechaProperty().get();
	}
	
	private final void setFecha(final LocalDate fecha) {
		this.fechaProperty().set(fecha);
	}
	public final void nuevaFecha(final LocalDate date) {
		yearCombo.getSelectionModel().select(String.valueOf(date.getYear()));
		mesCombo.getSelectionModel().select(date.getMonthValue()-1);
		diaCombo.getSelectionModel().select(date.getDayOfMonth()-1);
	}
	
}