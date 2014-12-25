/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.sun.javafx.application.PlatformImpl;

import de.gui.comp.NotezButtonBar;
import de.gui.comp.NotezSettingsPane;
import de.gui.comp.NotezTextPane;
import de.notez.NotezProperties;
import de.notez.NotezRemoteSync;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.notez.data.NotezData;
import de.notez.data.NotezDataProperties;
import de.notez.data.base.BaseNotezDataProperties;
import de.notez.parser.NotezParsers;
import de.notez.parser.UnsupportedVersionException;
import de.notez.share.NotezShareBase;
import de.util.NotezDataUtil;
import de.util.NotezFileUtil;
import de.util.NotezListenerUtil;
import de.util.log.NotezLog;

public class NotezNote
{
	public static final String ICON_LOGO = "include/icons/logo.png";
	public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
	public static final String NOTEZ_LOGO = "include/icons/logo.png";
	public static final String TODO_BOX = "[ ]";

	private static ObservableList<NotezNote> notes = FXCollections.observableArrayList();

	public static ObservableList<NotezNote> notezList()
	{
		return FXCollections.unmodifiableObservableList(notes);
	}

	private Stage stage;

	private NotezDataProperties data;

	@FXML
	private NotezButtonBar btns;
	@FXML
	private NotezSettingsPane settings;
	@FXML
	private NotezTextPane text;

	@FXML
	private ImageView resize;
	@FXML
	private Hyperlink fileLink;

	@FXML
	private StackPane stack;

	protected ObjectProperty<NotezBody> body;

	protected ObjectProperty<File> note;

	protected ReadOnlyBooleanWrapper noteChanged;

	public NotezNote(File file) throws IOException
	{
		FXMLLoader loader = new FXMLLoader(NotezFileUtil.getResourceURL(FXML_PATH));
		loader.setController(this);
		BorderPane root = loader.load();
		Scene scene = new Scene(root);

		PlatformImpl.runAndWait(() -> {
			stage = new Stage(StageStyle.UNDECORATED);

			// XXX Add this to gain drop shadow (1/2)
			// Group g = new Group();
			// Scene scene = new Scene(g);
			// scene.setFill(null);
			// root.setPadding(new Insets(10, 10, 10, 10));
			// root.setEffect(new DropShadow());

				stage.setScene(scene);
				// Fixed set height/width needed for dialogs (relative to)
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.getIcons().add(new Image(NotezFileUtil.getResourceStream(NOTEZ_LOGO)));
				// stage.show();

				// XXX Add this to gain drop shadow (2/2)
				// g.getChildren().add(root);
			});

		notes.add(this);

		data = new BaseNotezDataProperties(NotezFileUtil.removeEnding(file.getName()));

		Collection<NotezComponent> comps = new HashSet<>();
		comps.add(btns);
		comps.add(settings);
		comps.add(text);

		for(NotezComponent comp : comps)
		{
			comp.setNote(this);
			comp.setListener();
		}

		note = new SimpleObjectProperty<File>(file);

		body = new SimpleObjectProperty<NotezNote.NotezBody>(NotezBody.TEXT);
		stack.getChildren().setAll(text);

		noteChanged = new ReadOnlyBooleanWrapper(true);

		setListeners();
	}

	private void setListeners()
	{
		body.addListener((p, o, n) -> {
			Node node = null;
			switch(n)
			{
				case TEXT:
					node = text;
					break;

				case SETTINGS:
					node = settings;
					break;
			}
			if (Objects.nonNull(node))
			{
				stack.getChildren().setAll(node);
			}
		});

		fileLink.setText(note.get().getAbsolutePath());
		note.addListener((p, o, n) -> {
			if (Objects.nonNull(n))
			{
				fileLink.setText(n.getAbsolutePath());
			}
		});

		fileLink.setOnAction(e -> {
			try
			{
				// FIXME: the given path is invalid
				NotezFileUtil.openParentFolderInBrowser(note.get().getParentFile());
			}
			catch(Exception e1)
			{
				try
				{
					NotezDialog.showExceptionDialog(getStage(), "Error while opening folder",
						"Could not open the parent folder!", e1);
				}
				catch(Exception e2)
				{
					NotezLog.error("", e1);
				}
			}
		});

		NotezListenerUtil.setAsResizeNode(resize, getStage());
	}

	public String getTitle()
	{
		return getData().getTitle();
	}

	public void show()
	{
		getStage().show();
	}

	public void close(boolean askToSave)
	{
		getStage().hide();

		if (noteChanged.get() && askToSave)
		{
			if (NotezProperties.getBoolean(NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT))
			{
				save();
			}
			else
			{
				try
				{
					switch(NotezDialog.showRememberQuestionDialog(stage, "Save Changes",
						"Do you like to save the changes?",
						NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT, true))
					{
						case CANCEL:
						case CLOSE:
							// Stop this action!
							return;

						case NO:
							// Do nothing
							break;

						case OK:
						case YES:
							save();
							break;
					}
				}
				catch(IOException | InterruptedException e)
				{
					NotezLog.error("Error while asking the user for saving", e);
				}
			}
		}

		if (NotezRemoteSync.isRunning()
				&& NotezNote.notezList().stream().filter(n -> n.getStage().isShowing()).count() != 0)
		{
			try
			{
				switch(NotezDialog.showRememberQuestionDialog(stage, "Exit Notez Receiver",
					"Keep Notez-Receiver running in background?",
					NotezProperties.NOTEZ_LET_RECEIVER_RUNNING))
				{
					default:
					case CANCEL:
					case CLOSE:
					case NO:
						NotezRemoteSync.stopAll();
						break;
					case OK:
					case YES:
						// Do nothing
						break;
				}
			}
			catch(IOException | InterruptedException e)
			{
				NotezLog.error("error while asking user for closing the the server", e);
				NotezRemoteSync.stopAll();
			}
		}

		NotezProperties.save();
		stage.hide();
	}

	public void delete()
	{
		// TODO Animate that? :-)
		try
		{
			switch(NotezDialog.showQuestionDialog(stage, "Delete Notez",
				"Do you really want to delete this Notez?"))
			{
				default:
				case NO:
				case CANCEL:
				case CLOSE:
					return;

				case YES:
					if (note != null && note.get().exists())
					{
						note.get().delete();
					}
					close(false); // don't ask to save
					notes.remove(this);
					break;
			}
		}
		catch(IOException | InterruptedException e)
		{
			NotezLog.error("error while asking user for deletion", e);
		}
	}

	public void loadData(File file)
	{
		if (NotezFileUtil.fileCanBeLoad(file))
		{
			try
			{
				loadData(NotezParsers.parseFile(file));
			}
			catch(UnsupportedVersionException e)
			{
				NotezLog.warn("", e);
			}
			catch(IOException e)
			{
				NotezLog.warn("Cannot load file " + file, e);
			}
		}
	}

	protected File genNotezFile(String notezName)
	{
		return NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
				NotezProperties.get(NotezProperties.NOTEZ_WORK_FOLDER) + notezName
						+ NotezFrame.NOTEZ_FILE_POSFIX) : note.get();
	}

	public void save()
	{
		save(NotezFileUtil.genNotezFile(getData().getTitle()));
	}

	public void save(File note)
	{
		if (Objects.isNull(note))
		{
			note = this.note.get();
		}
		// If opened notez differes from to saving note
		else if (!note.equals(this.note.get()))
		{
			this.note.get().delete();
		}

		File parent = note.getParentFile();
		if (parent != null && !parent.exists())
		{
			try
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
			catch(IOException | InterruptedException e)
			{
				NotezLog.error("Error while asking the user for creating the notez folder", e);
			}
		}

		try
		{
			NotezParsers.save(this, note);
		}
		catch(IOException e)
		{
			NotezLog.error("Error while saving", e);
		}
		this.note.set(note);
	}

	public void saveSettings() throws IOException
	{
		NotezProperties.set(NotezProperties.NOTEZ_WORK_FOLDER, txtPropNotezWorkFold.getText());
		NotezProperties.set(NotezProperties.NOTEZ_REMOTE_FOLDER, txtPropNotezRemoteFold.getText());

		NotezProperties.set(NotezProperties.NOTEZ_MAIL_USER, txtEmail.getText());
		NotezProperties.set(NotezProperties.NOTEZ_MAIL_HOST, txtHost.getText());
		NotezProperties.set(NotezProperties.NOTEZ_MAIL_PORT, txtPort.getText());

		// TODO only switch if valid?
		c.switchTo(borderPaneNotez);
	}

	public void printNote() throws Exception
	{
		TextArea toPrint = null;

		switch(NotezDialog.showQuestionDialog(stage, "Print the Notez",
			"Format Notez into TODO-List?"))
		{
			default:
			case CANCEL:
			case CLOSE:
				// Stop printing action
				return;
			case NO:
				toPrint = text.getTxt();
				break;

			case OK:
			case YES:
				toPrint = formatToTODO(text.getTxt());
				break;
		}

		PrinterJob print = PrinterJob.createPrinterJob();
		if (toPrint != null && print.showPrintDialog(stage))
		{
			// TODO add the logo in background
			// toPrint.setStyle("-fx-background-image: url('../icons/tray.png'); "
			// + "-fx-background-position: bottom right; "
			// + "-fx-background-repeat: stretch");

			print.printPage(toPrint);
			print.endJob();
		}
		else
		{
			print.cancelJob();
		}
	}

	public TextArea formatToTODO(TextArea toPrint)
	{
		String[] tmp = toPrint.getText().split("\n");
		StringBuilder result = new StringBuilder("TODO: ");

		int maxLen = 0;
		for(String s : tmp)
		{
			if (s.length() > maxLen)
			{
				maxLen = s.length();
			}
		}

		result.append(getData().getPageData().getText())
				.append("\r\n")
				.append("Date: ")
				.append(
					new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance()
							.getTime())).append("\r\n\r\n");

		for(String s : tmp)
		{
			result.append(s);

			for(int i = s.length(); i <= maxLen; i++)
			{
				result.append(" ");
			}

			result.append(TODO_BOX).append("\r\n");
		}

		return new TextArea(result.toString());
	}

	public void shareNote() throws IOException, InterruptedException
	{
		if (NotezRemoteSync.getAllUsers().isEmpty())
		{
			switch(NotezDialog.showQuestionDialog(stage, "No user to share with",
				"You have no user registered to share with.\r\n" + "Like to add one?"))
			{
				case CANCEL:
				case CLOSE:
				case NO:
					// Stop action
					return;

				case OK:
				case YES:
					// Switch to share user settings and open add dialog
					switchTo(NotezBody.SETTINGS);
					tabSettings.getSelectionModel().select(tabRemote);
					tPaneShareUser.setExpanded(true);
					addNewUser();
					break;
			}
			return;
		}

		NotezRemoteUser user = NotezDialog.showShareWithDialog(stage, "Share Notez",
			"Share this Notez with ", NotezRemoteSync.getAllUsers());

		if (user != null)
		{
			String msg = "";
			switch(NotezShareBase.shareNotez(this, note.get(), user.getShare()))
			{
				default:
				case NOT_SUPPORTED:
					msg = "Sharing not supported!";
					break;
				case BLOCKED:
					msg = "Sharing of Notez blocked!";
					break;
				case OFFLINE:
					msg = "Cant reach user!";
					break;
				case SHARED:
					msg = "Notez shared successfull!";
					break;
				case CANCELD_BY_USER:
					// Cancel it
					return;
			}

			NotezDialog.showInfoDialog(stage, "Share Notez with " + user.getUsername(), msg);
		}
	}

	public void switchTo(NotezBody body)
	{
		setNotezBody(body);
	}

	public void setNotezBody(NotezBody body)
	{
		notezBodyProperty().set(body);
	}

	public NotezBody getNotezBody()
	{
		return notezBodyProperty().get();
	}

	public ObjectProperty<NotezBody> notezBodyProperty()
	{
		return body;
	}

	public void loadData(NotezData data)
	{
		NotezDataUtil.equalize(data, getData());
	}

	public NotezDataProperties getData()
	{
		return data;
	}

	public Stage getStage()
	{
		return stage;
	}

	public File getNoteFile()
	{
		return note.get();
	}

	public static enum NotezBody
	{
		TEXT, SETTINGS,

	}
}
