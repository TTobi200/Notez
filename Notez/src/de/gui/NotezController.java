/*
 * $Header$
 *
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import de.gui.comp.NotezSettingsPane;
import de.util.NotezFileUtil;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.NotezSettings;
import de.util.notez.NotezParsers;
import de.util.share.NotezShareBase;

public class NotezController
{
	public static final String ICON_LOGO = "include/icons/logo.png";
	public static final String ICON_ADD = "include/icons/new_icon.png";
	public static final String ICON_SETTINGS = "include/icons/icon_local_settings.png";
	public static final String ICON_CLOSE = "include/icons/icon_close.png";
	public static final String ICON_UNPINNED = "include/icons/pin.png";
	public static final String ICON_PINNED = "include/icons/pin_2.png";
	public static final String ICON_SHARE = "include/icons/share.png";
	public static final String ICON_SAVE = "include/icons/save.png";
	public static final String ICON_DELETE = "include/icons/delete.png";
	public static final String ICON_RESIZE = "include/icons/resize.gif";
	public static final String ICON_PICK_NOTE = "include/icons/pn-add-source-copy.gif";
	public static final String ICON_DISSOLVE = "include/icons/link_break.png";

	@FXML
	protected Region toolBar;
	@FXML
	protected Button btnAdd;
	@FXML
	protected Button btnClose;
	@FXML
	protected Button btnSettings;
	@FXML
	protected Button btnSave;
	@FXML
	protected Button btnShare;
	@FXML
	protected Button btnDelete;
	@FXML
	protected TextArea txtNote;
	@FXML
	protected HBox hBoxButtom;
	@FXML
	protected Hyperlink fileLink;
	@FXML
	protected ScrollPane scrollTxt;
	@FXML
	protected ImageView resize;
	@FXML
	protected TabPane tabSettings;
	@FXML
	protected Tab tabLocal;
	@FXML
	protected StackPane stack;
	@FXML
	protected BorderPane borderPaneSettings;
	@FXML
	protected VBox vBoxLocalSet;
	@FXML
	protected ToggleButton btnPin;
	@FXML
	protected ImageView pickNote;
	@FXML
	protected TextField txtTitle;
	@FXML
	protected Button btnAddUser;
	@FXML
	protected Button btnDeleteUser;

	public static ObservableList<String> notezTxt;

	@FXML
	protected BorderPane borderPanePage;
	@FXML
	protected BorderPane borderPaneNotez;
	@FXML
	protected Button btnPrevPage;
	@FXML
	protected Button btnNextPage;
	@FXML
	protected Label lblPage;

	@FXML
	protected TableView<NotezRemoteUser> tableRemoteuser;
	@FXML
	protected TableColumn<NotezRemoteUser, String> colUsername;
	@FXML
	protected TableColumn<NotezRemoteUser, String> colFolder;

	protected double initialX;
	protected double initialY;

	protected Stage stage;

	protected File note;
	protected Image iVUnpinned;
	protected Image iVPinned;
	protected Integer idx;
	protected boolean isGrouped = false;

	protected ObservableList<NotezController> notezGroup;

	protected Rectangle lastSavedSize;

	protected NotezPagedData data;

	/**
	 * boolean binding indicating whether the text in {@link #txtNote} differs
	 * from the saved on
	 */
	protected ReadOnlyBooleanProperty noteChanged;

	protected NotezControllerListeners c;

	public NotezController(Stage stage, int idx)
	{
		this(stage, null, idx);
	}

	public NotezController(Stage stage, File note, int idx)
	{
		this.stage = stage;
		this.note = note;
		this.idx = new Integer(idx);

		notezGroup = FXCollections.observableArrayList();
		lastSavedSize = new Rectangle();
		notezTxt = FXCollections.observableArrayList();

		stage.showingProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue)
			{
				if(newValue.booleanValue())
				{
					lastSavedSize.setX(stage.getX());
					lastSavedSize.setY(stage.getY());
					lastSavedSize.setWidth(stage.getWidth());
					lastSavedSize.setHeight(stage.getHeight());

					stage.showingProperty().removeListener(this);
				}
			}
		});

		c = new NotezControllerListeners(this);
	}

	@FXML
	protected void initialize() throws IOException
	{
		c.initialize();

	}

	/**
	 * Method to add a new notez.
	 * @throws IOException
	 *             See: {@link NotezFrame#createNotezFrame()}
	 */
	@FXML
	public void addNewNote() throws IOException
	{
		NotezFrame.createNotezFrame();
	}

	/**
	 * Method to close this notez.
	 * User gets asked if he likes to save the notez.
	 * @throws Exception
	 *             See: {@link #saveNote(File)}
	 */
	@FXML
	public void closeNote() throws Exception
	{
		closeNote(true);
	}

	/**
	 * Method to close this notez.
	 * @throws Exception
	 *             See: {@link #saveNote(File)}
	 */
	public void closeNote(boolean askToSave) throws Exception
	{
		if(noteChanged.get())
		{
			switch(NotezDialog.showQuestionDialog(stage, "Save Changes",
				"Do you like to save the changes?"))
			{
				case CANCEL:
				case CLOSE:
					// Do nothing
					return;

				case OK:
				case YES:
					saveNote(genNotezFile(txtTitle.getText()));
				case NO:
					NotezSettings.store(new File(NotezFrame.SETTINGS_FILE));
					break;

			}
		}
		stage.hide();
	}

	protected File genNotezFile(String notezName)
	{
		return NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
			NotezFrame.LOCAL_NOTEZ_FOLDER.replace(".", "") + notezName
							+ NotezFrame.NOTEZ_FILE_POSFIX) : note;
	}

	/**
	 * Method to open settings.
	 */
	@FXML
	public void openSettings()
	{
		c.switchTo(borderPaneSettings);
	}

	@FXML
	public void saveSettings() throws IOException
	{
		vBoxLocalSet.getChildren().forEach(child ->
		{
			if(child instanceof NotezSettingsPane)
			{
				((NotezSettingsPane)child).saveSetting();
			}
		});

		// TODO only switch if valid?
		c.switchTo(borderPaneNotez);
	}

	/**
	 * Method to cancel editing settings.
	 */
	@FXML
	public void cancelSettings()
	{
		// TODO maybe restore defaults?
		c.switchTo(borderPaneNotez);
	}

	/**
	 * Method to pin note. Set stage always on top and changes icon.
	 */
	@FXML
	public void pinNote()
	{
		boolean pinned = btnPin.isSelected();

		((ImageView)btnPin.getGraphic()).setImage(pinned ? iVPinned : iVUnpinned);
//		btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
		stage.setAlwaysOnTop(pinned);

		// Pin grouped notez
		notezGroup.forEach(notez ->
		{
			notez.btnPin.selectedProperty().set(pinned);
			notez.pinNote();
		});
	}

	@FXML
	protected void shareNote() throws IOException, InterruptedException
	{
		switch(NotezDialog.showQuestionDialog(stage, "Share Notez",
			"Share this Notez with HERTEL-1"))
		{
			default:
			case CANCEL:
			case CLOSE:
			case NO:
				return;

			case OK:
			case YES:
				NotezShareBase.shareNotez(this, note,
					NotezRemoteSync.getUser("HERTEL-1").getShare());
				break;
		}
	}

	@FXML
	protected void deleteNote() throws Exception
	{
		// TODO Animate that? :-)
		switch(NotezDialog.showQuestionDialog(stage, "Delete Notez",
			"Do you really want to delete this Notez?"))
		{
			default:
			case NO:
			case CANCEL:
			case CLOSE:
				return;

			case YES:
				if(note != null && note.exists())
				{
					note.delete();
				}
				closeNote(false); // don't ask to save
				break;
		}

	}

	@FXML
	protected void prevPage()
	{
		data.previousPage();
	}

	@FXML
	protected void nextPage()
	{
		data.nextPage();
	}

	/**
	 * Method to save current note.
	 * @throws Exception
	 *             See: {@link FileWriter}
	 */
	@FXML
	protected void saveNote() throws Exception
	{
		saveNote(genNotezFile(txtTitle.getText()));
	}

	/**
	 * Method to save given note to file.
	 * @param note
	 *            = the {@link File} to save note in
	 * @throws Exception
	 *             See: {@link FileWriter}
	 */
	protected void saveNote(File note) throws Exception
	{
		// If opened notez differes from to saving note
		if(note != this.note)
		{
			this.note.delete();
		}

		File parent = note.getParentFile();
		if(parent != null && !parent.exists())
		{
			switch(NotezDialog.showQuestionDialog(stage, "Create folder",
				"Notez folder not found. Create new?"))
			{
				default:
				case NO:
				case CANCEL:
				case CLOSE:
					return;

				case YES:
					parent.mkdirs();
			}
		}

		// try (FileWriter writer = new FileWriter(note))
		// {
		// writer.append(txtNote.getText());
		// writer.flush();
		// writer.close();
		// }
		// catch(Exception e)
		// {
		// throw e;
		// }

		NotezParsers.save(this, note);
		data.saveText();
		lastSavedSize.setX(stage.getX());
		lastSavedSize.setY(stage.getY());
		lastSavedSize.setWidth(stage.getWidth());
		lastSavedSize.setHeight(stage.getHeight());
	}

	public void openUrl(URI uri) throws IOException, URISyntaxException
	{
		if(Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();

			if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
			{
				desktop.browse(uri);
			}
		}
	}

	/**
	 * Method to pin given notez to this notez.
	 * @param noteToPin
	 *            = the {@link NotezController} (notez) to pin
	 */
	protected void pinToThisNote(NotezController noteToPin)
	{
		// Notez not group with itself
		if(noteToPin != this)
		{
			notezGroup.add(noteToPin);

			noteToPin.getStage().setX(stage.getX());
			noteToPin.getStage().setY(
				stage.getY() + (toolBar.getHeight() * notezGroup.size()));

			c.changePickIcon(isGrouped = true);
		}
	}

	public Stage getStage()
	{
		return stage;
	}

	public File getNoteFile()
	{
		return note;
	}

	public String getNoteText()
	{
		return txtNote.getText();
	}
}