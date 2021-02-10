module com.sample.control {
	opens com.sample.control;
	
	requires transitive com.sample.storage;
	requires transitive javafx.base;
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
}