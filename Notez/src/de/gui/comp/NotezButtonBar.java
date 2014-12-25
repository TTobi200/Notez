package de.gui.comp;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import de.gui.NotezComponent;
import de.gui.NotezNote;
import de.gui.NotezNote.NotezBody;
import de.gui.NotezNotes;
import de.util.NotezFileUtil;
import de.util.NotezListenerUtil;
import de.util.NotezSystemUtil;
import de.util.log.NotezLog;

public class NotezButtonBar extends AnchorPane implements NotezComponent
{
	public static final String FXML = "NotezButtonBar.fxml";
    public static final String ICON_UNPINNED = "include/icons/pin.png";
    public static final String ICON_PINNED = "include/icons/pin_2.png";

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

	protected Image iVUnpinned;
	protected Image iVPinned;

	public NotezButtonBar() throws IOException
	{
		if (!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
					NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();
		}

		iVPinned = new Image(
            NotezFileUtil.getResourceStream(ICON_PINNED));
        iVUnpinned = new Image(
            NotezFileUtil.getResourceStream(ICON_UNPINNED));
	}

	@FXML
	private void closeNote()
	{
		getNote().close(true);
	}

	@FXML
	private void openSettings()
	{
		getNote().switchTo(
			getNote().getNotezBody() == NotezBody.SETTINGS ? NotezBody.TEXT : NotezBody.SETTINGS);
	}

	@FXML
	private void deleteNote()
	{
		getNote().delete();
	}

	@FXML
	private void saveNote()
	{
		getNote().save();
	}

	@FXML
	private void addNewNote()
	{
		NotezNotes.creNote().show();
	}

	@FXML
	private void printNote()
	{
		try
		{
			getNote().printNote();
		}
		catch(Exception e)
		{
			NotezLog.error("error while printing note.", e);
		}
	}

	@FXML
	private void shareNote()
	{
		try
		{
			getNote().shareNote();
		}
		catch(IOException | InterruptedException e)
		{
			NotezLog.error("error while sharing note.", e);
		}
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
		txtTitle.setText(getNote().getData().getTitle());
		txtTitle.textProperty().bindBidirectional(getNote().getData().titleProperty());

		NotezListenerUtil.setAsRelocateNode(txtTitle, getNote().getStage());
		NotezListenerUtil.setAsRelocateNode(this, getNote().getStage());

		btnPin.selectedProperty().addListener((s, o, n) -> {
			((ImageView)btnPin.getGraphic()).setImage(n.booleanValue() ? iVPinned : iVUnpinned);
			// btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
			getNote().getStage().setAlwaysOnTop(n.booleanValue());

			// XXX
			// do not bind as there are needed several, which could
			// collide
			// if(c.noteParent.get() != null)
			// {
			// c.noteParent.get().btnPin.setSelected(n.booleanValue());
			// }
			// if(c.noteChild.get() != null)
			// {
			// c.noteChild.get().btnPin.setSelected(n.booleanValue());
			// }
		});
	}
}
