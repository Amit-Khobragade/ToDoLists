package com.sample;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import com.sample.storage.*;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void init() throws Exception {
		StoreData.getInstance().load();
	}
	@Override
	public void start( Stage primaryStage ) throws Exception {
		Parent root = FXMLLoader.load( getClass().getResource( "/rsrcs/rsc.fxml" ));
		primaryStage.setTitle( "To-do List" );
		primaryStage.setScene( new Scene( root, 800, 400 ));
		primaryStage.show();
	}
	@Override
	public void stop() throws Exception{
		StoreData.getInstance().save();
	}
	public static void main( String[] args ) {
		launch(args);
	}
}