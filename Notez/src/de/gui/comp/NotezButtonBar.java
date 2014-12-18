package de.gui.comp;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import de.gui.NotezComponent;
import de.gui.NotezNote;
import de.gui.NotezNotes;
import de.util.NotezFileUtil;
import de.util.NotezSystemUtil;

public class NotezButtonBar extends AnchorPane implements NotezComponent
{
	public static final String FXML = "NotezButtonBar.fxml";

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
		if(!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
				NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER
											 + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();
		}
	}

	@FXML
	void closeNote()
	{
		getNote().hide();
	}

	@FXML
	void openSettings()
	{

	}

	@FXML
	void deleteNote()
	{
		getNote().delete();
	}

	@FXML
	void saveNote()
	{

	}

	@FXML
	void addNewNote()
	{
		NotezNotes.creNote().show();
	}

	@FXML
	void printNote()
	{

	}

	@FXML
	void shareNote()
	{

	}
	
	protected NotezNote note;

	@Override
	public void setNote(NotezNote note)
	{
		this.note = note;
	}

	@Override
	public NotezNote getNote()
	{
		return note;
	}
	
	@Override
	public void setListener()
	{
		// TODO Auto-generated method stub
		
	}
}
