package de.gui.comp;

import java.io.*;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import de.gui.*;
import de.gui.NotezGui.NotezGuiBody;
import de.notez.*;
import de.notez.prop.NotezProperties;
import de.util.*;
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

	@FXML
	private HBox hboxButtons;
	@FXML
	private HBox hboxLeft;

	protected Image iVUnpinned;
	protected Image iVPinned;

	protected List<Node> buttons;
	protected List<Node> leftSide;

	public NotezButtonBar() throws IOException
	{
		if (!NotezSystemUtil.isRunningInSceneBuilder())
		{
			FXMLLoader loader = new FXMLLoader(
					NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER + File.separator + FXML));
			loader.setRoot(this);
			loader.setController(this);

			loader.load();

			buttons = new ArrayList<>(hboxButtons.getChildren());
			leftSide = new ArrayList<>(hboxLeft.getChildren());
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
		txtTitle.textProperty().bindBidirectional(getNote().getData().titleProperty());

		NotezListenerUtil.setAsRelocateNode(txtTitle, getGui());
		NotezListenerUtil.setAsRelocateNode(this, getGui());

		btnPin.selectedProperty().addListener((s, o, n) -> {
			((ImageView)btnPin.getGraphic()).setImage(n.booleanValue() ? iVPinned : iVUnpinned);
			// btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
			getGui().setAlwaysOnTop(n.booleanValue());
		});

		setAsDndSource(pickNote);
		setAsDndTarget(this);
		getChildrenUnmodifiable().forEach(this::setAsDndTarget);

		Node[] itms = getChildrenUnmodifiable().stream().toArray(Node[]::new);

		// Add item visibility
		NotezListenerUtil.addVisibleNodeHider(this, itms);

		for(Object o : itms)
		{
			if (o instanceof Control)
			{
				Tooltip tT = ((ButtonBase)o).getTooltip();
				if (tT != null)
				{
					// Add Tooltip visibility
					NotezListenerUtil.addVisibleNodeHider(tT, itms);
				}
			}
		}

		btnSave.disableProperty().bind(getNote().changedProperty().not());

		hboxButtons.getChildren().addListener(
			(ListChangeListener<Node>)c -> {
				while(c.next())
				{
					if (c.wasPermutated() || c.wasUpdated() || c.wasReplaced())
					{
						return;
					}
					else if (c.wasAdded() || c.wasRemoved())
					{
						Platform.runLater(() ->
						FXCollections.sort(hboxButtons.getChildren(),
							(o1, o2) -> buttons.indexOf(o1) - buttons.indexOf(o2)));

						return;
					}
				}
			});
		
		hboxLeft.getChildren().addListener(
			(ListChangeListener<Node>)c -> {
				while(c.next())
				{
					if (c.wasPermutated() || c.wasUpdated() || c.wasReplaced())
					{
						return;
					}
					else if (c.wasAdded() || c.wasRemoved())
					{
						Platform.runLater(() ->
						FXCollections.sort(hboxLeft.getChildren(),
							(o1, o2) -> leftSide.indexOf(o1) - leftSide.indexOf(o2)));
						
						return;
					}
				}
			});
		
		bindButtonVisibility(btnDelete, NotezProperties.NOTEZ_BTN_REMOVE, hboxButtons.getChildren());
		bindButtonVisibility(btnSave, NotezProperties.NOTEZ_BTN_SAVE, hboxButtons.getChildren());
		bindButtonVisibility(btnAdd, NotezProperties.NOTEZ_BTN_ADD, hboxButtons.getChildren());
		bindButtonVisibility(btnPrint, NotezProperties.NOTEZ_BTN_PRINT, hboxButtons.getChildren());
		bindButtonVisibility(btnShare, NotezProperties.NOTEZ_BTN_SHARE, hboxButtons.getChildren());
		bindButtonVisibility(pickNote, NotezProperties.NOTEZ_BTN_GROUP, hboxButtons.getChildren());
		bindButtonVisibility(btnPin, NotezProperties.NOTEZ_BTN_PIN, hboxLeft.getChildren());
	}

	private void bindButtonVisibility(Node node, String property, List<Node> listChildren)
	{
		NotezSystemUtil.getSystemProperties()
		.getBooleanProperty(property, true)
		.addListener((p, o, n) -> {
			if (n.booleanValue())
			{
				listChildren.add(node);
			}
			else
			{
				listChildren.remove(node);
			}
		});
		if(!NotezSystemUtil.getSystemProperties().getBoolean(property))
		{
			listChildren.remove(node);
		}
	}

	protected void setAsDndSource(final Node node)
	{
		node.setOnDragDetected(event -> {
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
		node.setOnDragOver(event -> {
			if (event.getDragboard().hasContent(NOTEZ_CONTROLLER_DATA_FORMAT)
					&& !event.getDragboard().getContent(NOTEZ_CONTROLLER_DATA_FORMAT)
							.equals(new Integer(getNote().getIndex())))
			{
				// Accept the move
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});

		node.setOnDragDropped(event -> {

			// Get dropped notez id from clipbord and pin it to this notez
			if (event.isAccepted())
			{
				Dragboard db = event.getDragboard();
				// NotezController ctrl =
				// NotezFrame.getNotez(Integer.valueOf(db.getString()));
				NotezNote note = NotezNote.notezList().get(
					((Integer)db.getContent(NOTEZ_CONTROLLER_DATA_FORMAT)).intValue());

				NotezNote parent = getNote();
				while(Objects.nonNull(parent.noteChildProperty().get()))
				{
					parent = parent.noteChildProperty().get();
				}

				note.setNoteParent(parent);
			}

			event.setDropCompleted(true);
			event.consume();
		});
	}
}
