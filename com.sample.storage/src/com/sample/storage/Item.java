package com.sample.storage;

import java.time.LocalDate;

public final class Item{
	public Item( String title, String description, LocalDate creationDate, LocalDate dueDate ){
		this( title, description,creationDate, dueDate, false ); 
	}
	public Item( String title, String description, LocalDate creationDate, LocalDate dueDate, boolean status ){
		this.title = title;
		this.description = description;
		this.creationDate = creationDate;
		this.dueDate = dueDate;
		this.status = status;
	}
	/*
	* setters
	*/
	public void setTitle( String newTitle ){
		this.title = newTitle;
	}
	public void setDescription( String newDescription ){
		this.description = newDescription;
	}
	public void setDueDate( LocalDate newDueDate ){
		this.dueDate = newDueDate;
	}
	public void setStatus( boolean status ){
		this.status = status;
	}
	public String getTitle(){
		return new String( title );
	}
	public String getDescription(){
		return new String( description );
	}
	public boolean isComplete(){
		return status;
	}
	public LocalDate getCreationDate(){
		return LocalDate.of( creationDate.getYear(), creationDate.getMonth(),
						 creationDate.getDayOfMonth() );
	}
	public LocalDate getDueDate(){
		return LocalDate.of( dueDate.getYear(), dueDate.getMonth(),
							dueDate.getDayOfMonth() );
	}

	@Override
	public String toString(){
		return title;
	}
	private String title;
	private String description;
	private LocalDate creationDate;
	private LocalDate dueDate;
	private boolean status;
}