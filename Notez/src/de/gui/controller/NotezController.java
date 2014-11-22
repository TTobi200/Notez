/*
 * $Header$
 *
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.gui.NotezDialog;
import de.gui.NotezPagedData;
import de.gui.comp.NotezSettingsPane;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.share.NotezShareBase;

public class NotezController extends NotezControllerBase<NotezPagedData, NotezControllerListeners>
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

	protected Image iVUnpinned;
	protected Image iVPinned;

	public NotezController(Stage stage, int idx)
	{
		this(stage, null, idx);
	}

	public NotezController(Stage stage, File note, int idx)
	{
		super(stage, note, idx);
	}

	@Override
	protected NotezControllerListeners creNotezControllerListener()
	{
		return new NotezControllerListeners(this);
	}

	@Override
	public void initialize()
	{
		c.initialize();

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
		vBoxLocalSet.getChildren().forEach(child -> {
			if (child instanceof NotezSettingsPane)
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

	@Override
	protected void saveNote() throws Exception
	{
		saveNote(genNotezFile(txtTitle.getText()));
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
				NotezShareBase.shareNotez(this, note, NotezRemoteSync.getUser("HERTEL-1")
						.getShare());
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


	public void openUrl(URI uri) throws IOException, URISyntaxException
	{
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();

			if (desktop.isSupported(java.awt.Desktop.Action.BROWSE))
			{
				desktop.browse(uri);
			}
		}
	}

	@Override
	public String getNoteText()
	{
		return txtNote.getText();
	}

	@Override
	protected NotezPagedData creNotezData()
	{
		return new NotezPagedData();
	}
}