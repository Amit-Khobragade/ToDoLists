package com.sample.control;

import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.time.LocalDate;
import com.sample.storage.*;

public class NewItem {
	public void editMode( Item it ){
		title.setText( it.getTitle() );
		description.setText( it.getDescription() );
		dueDate.setValue( it.getDueDate() );
	}
	public Item saveItem(){
		if( title.getText().isEmpty() || dueDate.getValue() == null ){
			return null;
		}
		Item it = new Item( title.getText(), description.getText(),
					LocalDate.now(), dueDate.getValue() );
		StoreData.getInstance().add(it);
		return it;
	}
	@FXML
	private TextArea description;
	@FXML
	private TextField title;
	@FXML
	private DatePicker dueDate;
}