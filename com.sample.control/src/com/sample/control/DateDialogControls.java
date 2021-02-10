package com.sample.control;

import javafx.scene.control.*;
import javafx.fxml.FXML;

import java.time.LocalDate;

public class DateDialogControls{
	public LocalDate saveDate(){
		LocalDate ld = date.getValue();
		return ld;
	}
	@FXML
	private DatePicker date;
}