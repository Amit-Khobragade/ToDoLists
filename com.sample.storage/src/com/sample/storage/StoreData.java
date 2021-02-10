package com.sample.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.io.*;
import java.nio.file.*;


public class StoreData {
	private StoreData() {
		format = DateTimeFormatter.ofPattern( "MMMM-d-yyyy" );
	}
	public static StoreData getInstance() {
		return instance;
	}
	public ObservableList<Item> getItems() {
		return items;
	}
	public void setItems( ObservableList<Item> other ){
		items = other;
	}
	public void load() throws IOException { 
		items = FXCollections.observableArrayList();
		Path path = Paths.get( fileName );
		BufferedReader reader = Files.newBufferedReader( path );
		try {
			for( String input = reader.readLine(); input != null; input = reader.readLine()  ){
				String[] info = input.split( "\t" );
				String title = info[0];
				String description = info[1];
				LocalDate startDate = LocalDate.parse( info[2], format );
				LocalDate dueDate = LocalDate.parse( info[3], format );
				boolean status = Boolean.parseBoolean(info[4]);
				items.add( new Item(title, description, startDate, dueDate, status ));
			}
		}
		finally{
			if( reader != null ){
				reader.close();
			}
		}
	}
	public void save() throws IOException {
		// items = FXCollections.observableArrayList();
		Path path = Paths.get( fileName );
		BufferedWriter writer = Files.newBufferedWriter( path );
		try {
			for( Iterator<Item> iter = items.iterator(); iter.hasNext(); ){
				Item it = iter.next(); 
				writer.write(String.format( "%s\t%s\t%s\t%s\t%s",
							it.getTitle(), it.getDescription(),
							it.getCreationDate().format(format), it.getDueDate().format(format),
							it.isComplete() ));
				writer.newLine();
			}
		}
		finally{
			if( writer != null ){
				writer.close();
			}
		}
	}
	public void add( Item it ){
		items.add( it );
	}

	public void remove( Item it ){
		items.remove( it );
	}
	static{
		File f = new File( "/list.txt" );
		try{
			f.createNewFile();
		}
		catch( IOException ex ){
			ex.printStackTrace();
		}
	}
	private DateTimeFormatter format;
	private static String fileName="/list.txt";
	private static StoreData instance = new StoreData();
	private ObservableList<Item> items;
}