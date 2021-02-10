package com.sample.control;

import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.FilteredList;

import com.sample.storage.*;
import java.util.Optional;
import java.time.LocalDate;
import java.util.function.Predicate;
import java.time.format.DateTimeFormatter;

public class Controls {
	public void initialize() {
		Font f = new Font( "bold", 10 );
		timePeriod.setFont( f );
		status.setFont( f );
		description.setFont( new Font( "bold", 13 ) );
		ContextMenu onList = new ContextMenu();
		MenuItem deleteItem = new MenuItem( "delete" );
		deleteItem.setOnAction( (ActionEvent e) -> {
			handleDeleteItem();
		});
		onList.getItems().addAll( deleteItem );
		title.setContextMenu( onList );
		filterAll.setSelected( true );

		allSelected = ( Item i ) -> { return true; };
		todaySelected =  ( Item i ) -> { return i.getDueDate().equals(LocalDate.now());};
		tomorrowSelected =   ( Item i ) -> { return i.getDueDate().equals(LocalDate.now().plusDays(1));};
		dueSelected =   ( Item i ) -> { return i.getDueDate().isBefore(LocalDate.now()) && !i.isComplete();};
		doneSelected =   ( Item i ) -> { return i.isComplete(); };

		filteredList = new FilteredList<Item>( StoreData.getInstance().getItems(), allSelected );
		
		
		SortedList<Item> list = new SortedList< Item >( filteredList, ( Item i1, Item i2 ) -> {
			if( i1.isComplete() && !i2.isComplete() ){
				return 1;
			} else if( !i1.isComplete() && i2.isComplete()) {
				return -1;
			}
			if( i1.getDueDate().equals( i2.getDueDate() )){
				return 0;
			} else if( i1.getDueDate().isBefore( i2.getDueDate() )) {
				return -1;
			} else {
				return 1;
			}
		});
		title.setItems( list );
		title.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );

		title.getSelectionModel().selectedItemProperty().addListener(
			 ( ObservableValue<? extends Item> observable, Item oldItem, Item newItem ) -> {
				if( newItem != null ){
					updateScene();
				}
		});
		title.setOnKeyPressed(
			(KeyEvent e) -> {
				if( e.getCode().equals( KeyCode.DELETE )){
					handleDeleteItem();
				}
			});

		status.setOnMouseClicked( ( MouseEvent e ) -> {
					Item it = title.getSelectionModel().getSelectedItem();
					it.setStatus( !it.isComplete() );
					String s = "status: " +( (it.isComplete())? "completed" : "not completed");
					status.setText( s );
					StoreData.getInstance().remove( it );
					StoreData.getInstance().add( it );	
					title.getSelectionModel().select( it );
		});

		if(!list.isEmpty()){
			title.getSelectionModel().selectFirst();
		}
		
		title.setCellFactory( ( ListView<Item> it ) -> {
				ListCell<Item> toRet = new ListCell<Item>() {
					@Override
					protected void updateItem( Item item, boolean empty ){
						super.updateItem( item, empty );
						if( empty ){
							setText( null );
							setStyle( null );
						} else {
							setText( item.getTitle() );
							if( item.isComplete() ){
								setStyle( "-fx-background-color: springgreen");
							} else {
								setStyle( null);
								if(  item.getDueDate().equals(LocalDate.now())){
									setTextFill( Color.RED );
								} else if( item.getDueDate().isBefore(LocalDate.now()) ){
									setTextFill( Color.BLACK );
									setStyle( "-fx-background-color: red");
								} else if( item.getDueDate().equals( LocalDate.now().plusDays(1)) && !item.isComplete() ){
									setTextFill( Color.BROWN );
									setStyle( "-fx-Underline: true");
								} else {
									setTextFill( Color.BLACK );
								}
							}

						}
						
					}
				};
				return toRet;
		});
	}

	@FXML
	public void handleMouseClick( Event e ){
		boolean isButton = newButton.equals( e.getSource() ) || edit.equals( e.getSource() );
		boolean isTime = timePeriod.equals( e.getSource() );
		if( !isButton && !isTime ){
			System.out.println( "Error in the click handler" );
			return;
		}
		Dialog<ButtonType> dialog = new Dialog<>();
		FXMLLoader fxmlLoader = new FXMLLoader();
		dialog.initOwner( borderPane.getScene().getWindow() );
		if( isButton ){
			if( ((Button)e.getSource()).equals(newButton)){
				dialog.setTitle( "New Item" );	
				dialog.setHeaderText("add new item");
				fxmlLoader.setLocation(  getClass().getResource( "/rscr/newItem.fxml") );
			} else {
				if( title.getItems().isEmpty() ){
					return;
				}
				dialog.setTitle( "edit Item" );	
				dialog.setHeaderText("edit item");
				fxmlLoader.setLocation(  getClass().getResource( "/rscr/newItem.fxml") );	
			}
		} else {
			dialog.setTitle( "Due Date" );	
			fxmlLoader.setLocation(  getClass().getResource( "/rscr/DueDateDialog.fxml") );
		}

		try{
			dialog.getDialogPane().setContent( fxmlLoader.load() );
			if( isButton && ((Button)e.getSource()).equals(edit)){
				((NewItem)fxmlLoader.getController()).editMode(title.getSelectionModel().getSelectedItem());
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
			return;
		}
		dialog.getDialogPane().getButtonTypes().add( ButtonType.OK );
		dialog.getDialogPane().getButtonTypes().add( ButtonType.CANCEL );

		Optional<ButtonType> val = dialog.showAndWait();
		
		if( val.isPresent() && val.get() == ButtonType.OK ){
			Item it = title.getSelectionModel().getSelectedItem();
			if( isButton ){
				it = ((NewItem)fxmlLoader.getController()).saveItem();
				if(!((Button)e.getSource()).equals(newButton) && it != null ){
					StoreData.getInstance().remove( title.getSelectionModel().getSelectedItem());
				}
			} else {
				LocalDate newDate = ((DateDialogControls) fxmlLoader.getController()).saveDate();
				
				it.setDueDate( newDate );
				StoreData.getInstance().remove( it );
				StoreData.getInstance().add( it );	
			}
			if( it != null ){
				title.getSelectionModel().select( it );
			} else {
				title.getSelectionModel().selectFirst();
			}
		}
	}

	@FXML
	public void handleDeleteItem() {
		if( title.getItems().isEmpty() ){
			return;
		}
		Item it = title.getSelectionModel().getSelectedItem();
		Alert askDelete = new Alert( Alert.AlertType.CONFIRMATION );
		askDelete.setTitle( "deletion??" );
		askDelete.setHeaderText( "do you want to delete:: " + it.getTitle() );
		Optional<ButtonType> val = askDelete.showAndWait();
		if( val.isPresent() && val.get() == ButtonType.OK ){
			StoreData.getInstance().remove(	title.getSelectionModel().getSelectedItem() );
		}
		updateScene();
	}

	private void updateScene(){
		if( title.getItems().isEmpty() ){
			description.setText( null );
			timePeriod.setText( null );
			status.setText( null );
			return;
		}
		Item it = title.getSelectionModel().getSelectedItem();
		if( it.getDescription().isEmpty() ){
			description.setText( null );	
		} else {
			description.setText( it.getDescription() );
		}
		String s = df.format(it.getCreationDate()) + "   --   " + 
					df.format(it.getDueDate());
		timePeriod.setText( s );
		s = "status: " +( (it.isComplete())? "completed" : "not completed");
		status.setText( s );
	}

	@FXML
	public void handleFilters( ActionEvent e ){

		if( filterToday.isSelected() ) {
			filteredList.setPredicate( todaySelected );
		} else if( filterTomorrow.isSelected() ) {
			filteredList.setPredicate( tomorrowSelected );
		} else if( filterAlreadyDue.isSelected() ) {
			filteredList.setPredicate( dueSelected );
		} else if( filterCompleted.isSelected() ) {
			filteredList.setPredicate( doneSelected );
		} else {
			filteredList.setPredicate( allSelected );
		}
		if( !title.getItems().isEmpty() ){
			title.getSelectionModel().selectFirst();
		}
		updateScene();
	}
	
	@FXML
	private RadioMenuItem filterToday;
	@FXML
	private RadioMenuItem filterTomorrow;
	@FXML
	private RadioMenuItem filterAlreadyDue;
	@FXML
	private RadioMenuItem filterCompleted;
	@FXML
	private RadioMenuItem filterAll;
	
	@FXML
	private ListView<Item> title;
	@FXML
	private Label timePeriod;
	@FXML
	private Label status;
	@FXML 
	private TextArea description;
	@FXML
	private BorderPane borderPane;
	@FXML
	private Button newButton;
	@FXML
	private Button edit;
	private final DateTimeFormatter df = DateTimeFormatter.ofPattern( "MMMM d, YYYY");
	private FilteredList<Item> filteredList;
	private Predicate<Item> allSelected;
	private Predicate<Item> todaySelected;
	private Predicate<Item> tomorrowSelected;
	private Predicate<Item> dueSelected;
	private Predicate<Item> doneSelected;
	
}
