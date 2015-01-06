package de.gui.comp;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import de.gui.NotezGui;
import de.gui.NotezGui.NotezGuiBody;
import de.notez.NotezNote;
import de.notez.NotezNotes;
import de.util.NotezFileUtil;
import de.util.NotezListenerUtil;
import de.util.NotezSystemUtil;
import de.util.log.NotezLog;

public class NotezButtonBar extends AnchorPane implements NotezComponent
{
	public static DataFormat NOTEZ_CONTROLLER_DATA_FORMAT = new DataFormat(
		NotezNote.class.getName());

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
		if(!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
				NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER
											 + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();
		}

		iVPinned = new Image(NotezFileUtil.getResourceStream(ICON_PINNED));
		iVUnpinned = new Image(NotezFileUtil.getResourceStream(ICON_UNPINNED));
	}

	@FXML
	private void closeNote()
	{
		getNote().close(true);
	}

	@FXML
	private void openSettings()
	{
		getGui().switchToBody(
			getGui().getCurrentBody() == NotezGuiBody.SETTINGS ? NotezGuiBody.TEXT
							: NotezGuiBody.SETTINGS);
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

	protected NotezGui gui;

	@Override
	public void setGui(NotezGui gui)
	{
		this.gui = gui;
	}

	@Override
	public NotezGui getGui()
	{
		return gui;
	}

	@Override
	public void setListener()
	{
		txtTitle.setText(getNote().getData().getTitle());
		txtTitle.textProperty().bindBidirectional(
			getNote().getData().titleProperty());

		NotezListenerUtil.setAsRelocateNode(txtTitle, getGui());
		NotezListenerUtil.setAsRelocateNode(this, getGui());

		btnPin.selectedProperty()
			.addListener(
				(s, o, n) ->
				{
					((ImageView)btnPin.getGraphic()).setImage(n.booleanValue() ? iVPinned
									: iVUnpinned);
					// btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
					getGui().setAlwaysOnTop(n.booleanValue());
				});

		setAsDndSource(pickNote);
		setAsDndTarget(this);
		getChildrenUnmodifiable().forEach(this::setAsDndTarget);

		Node[] itms = getChildrenUnmodifiable()
			.stream()
			.toArray(Node[]::new);

		// Add item visibility
		NotezListenerUtil.addVisibleNodeHider(this, itms);

		for(Object o : itms)
		{
			if(o instanceof Control)
			{
				Tooltip tT = ((ButtonBase)o).getTooltip();
				if(tT != null)
				{
					// Add Tooltip visibility
					NotezListenerUtil.addVisibleNodeHider(tT, itms);
				}
			}
		}
		
		btnSave.disableProperty().bind(getNote().changedProperty().not());
	}

	protected void setAsDndSource(final Node node)
	{
		node.setOnDragDetected(event ->
		{
			Dragboard db = node.startDragAndDrop(TransferMode.MOVE);

			// Copy index of this Notez to clipboard
			ClipboardContent content = new ClipboardContent();
			// content.putString(String.valueOf(c.idx));
			content.put(NOTEZ_CONTROLLER_DATA_FORMAT, getNote().getIndex());
			db.setContent(content);

			event.consume();
		});

		node.setCursor(Cursor.HAND);
	}

	protected void setAsDndTarget(final Node node)
	{
		node.setOnDragOver(event ->
		{
			if(event.getDragboard().hasContent(NOTEZ_CONTROLLER_DATA_FORMAT)
			   && !event.getDragboard()
				   .getContent(NOTEZ_CONTROLLER_DATA_FORMAT)
				   .equals(new Integer(getNote().getIndex())))
			{
				// Accept the move
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});

		node.setOnDragDropped(event ->
		{

			// Get dropped notez id from clipbord and pin it to this notez
			if(event.isAccepted())
			{
				Dragboard db = event.getDragboard();
				// NotezController ctrl =
				// NotezFrame.getNotez(Integer.valueOf(db.getString()));
				NotezNote note = NotezNote.notezList()
					.get(
						((Integer)db.getContent(NOTEZ_CONTROLLER_DATA_FORMAT)).intValue());
				note.setNoteParent(getNote());
			}

			event.setDropCompleted(true);
			event.consume();
		});
	}
}
