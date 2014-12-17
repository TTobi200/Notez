package de.gui.comp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class NotezTextPane extends BorderPane
{
	public static final String FXML = "include/fxml/NotezTextPane.fxml";

	@FXML
	private TextArea txt;

	@FXML
	private Button btnPrevPage;

	@FXML
	private Button btnNextPage;

	@FXML
	private Label lblPage;

	public NotezTextPane() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML));
		loader.setRoot(this);
		loader.setController(this);

		loader.load();
	}

	@FXML
	void prevPage()
	{

	}

	@FXML
	void nextPage()
	{

	}
}
