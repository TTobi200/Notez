package de.gui.comp;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class NotezButtonBar extends AnchorPane
{
	public static final String FXML = "include/fxml/NotezButtonBar.fxml";

	@FXML
	private Button btnClose;

	@FXML
	private Button btnSave;

	@FXML
	private Tooltip tTipClose;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnPrint;

	@FXML
	private Button btnSettings;

	@FXML
	private ToggleButton btnPin;

	@FXML
	private ImageView pickNote;

	@FXML
	private Button btnShare;

	@FXML
	private TextField txtTitle;

	public NotezButtonBar() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML));
		loader.setRoot(this);
		loader.setController(this);

		loader.load();
	}

	@FXML
	void closeNote()
	{

	}

	@FXML
	void openSettings()
	{

	}

	@FXML
	void deleteNote()
	{

	}

	@FXML
	void saveNote()
	{

	}

	@FXML
	void addNewNote()
	{

	}

	@FXML
	void printNote()
	{

	}

	@FXML
	void shareNote()
	{

	}
}
