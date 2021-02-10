module com.sample {
	exports com.sample;
	

	requires transitive com.sample.control;
	requires javafx.base;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	
}