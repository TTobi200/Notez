/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.PrinterJob;
import javafx.scene.control.TextArea;

import com.sun.javafx.application.PlatformImpl;

import de.gui.NotezGui.NotezGuiBody;
import de.gui.comp.NotezSettingsPane.NotezSettingsPaneTabPane;
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
import de.util.log.NotezLog;

public class NotezNote
{
	public static final String TODO_BOX = "[ ]";

	private static ObservableList<NotezNote> notes = FXCollections.observableArrayList();

	public static ObservableList<NotezNote> notezList()
	{
		return FXCollections.unmodifiableObservableList(notes);
	}

	private NotezDataProperties data;

	protected ReadOnlyObjectWrapper<File> noteFile;

	protected ReadOnlyBooleanWrapper noteChanged;

	protected NotezGui gui;

	public NotezNote(File file) throws IOException
	{
		data = new BaseNotezDataProperties(NotezFileUtil.removeEnding(file.getName()));

		noteFile = new ReadOnlyObjectWrapper<File>(file);

		loadData(file);

		noteChanged = new ReadOnlyBooleanWrapper(true);

		PlatformImpl.runAndWait(() -> {
			try
			{
				gui = new NotezGui(this);
			}
			catch(Exception e)
			{
				NotezLog.error("could not create notez gui", e);
			}
		});

		notes.add(this);

		setListeners();
	}

	private void setListeners()
	{

	}

	public String getTitle()
	{
		return getData().getTitle();
	}

	public void show()
	{
		getGui().show();
	}

	public void close(boolean askToSave)
	{
		getGui().hide();

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
					switch(NotezDialog.showRememberQuestionDialog(gui, "Save Changes",
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
				&& NotezNote.notezList().stream().filter(n -> n.getGui().isShowing()).count() != 0)
		{
			try
			{
				switch(NotezDialog.showRememberQuestionDialog(gui, "Exit Notez Receiver",
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
		gui.hide();
	}

	public void delete()
	{
		// TODO Animate that? :-)
		try
		{
			switch(NotezDialog.showQuestionDialog(gui, "Delete Notez",
				"Do you really want to delete this Notez?"))
			{
				default:
				case NO:
				case CANCEL:
				case CLOSE:
					return;

				case YES:
					if (noteFile != null && noteFile.get().exists())
					{
						noteFile.get().delete();
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
						+ NotezFrame.NOTEZ_FILE_POSFIX) : noteFile.get();
	}

	public void save()
	{
		save(NotezFileUtil.genNotezFile(getData().getTitle()));
	}

	public void save(File note)
	{
		if (Objects.isNull(note))
		{
			note = this.noteFile.get();
		}
		// If opened notez differes from to saving note
		else if (!note.equals(this.noteFile.get()))
		{
			this.noteFile.get().delete();
		}

		File parent = note.getParentFile();
		if (parent != null && !parent.exists())
		{
			try
			{
				switch(NotezDialog.showQuestionDialog(gui, "Create folder",
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
		this.noteFile.set(note);
	}

	public void printNote() throws Exception
	{
		TextArea toPrint = new TextArea(getData().getPageData().getText());

		switch(NotezDialog.showQuestionDialog(gui, "Print the Notez",
			"Format Notez into TODO-List?"))
		{
			default:
			case CANCEL:
			case CLOSE:
				// Stop printing action
				return;
			case NO:
				break;

			case OK:
			case YES:
				toPrint = formatToTODO(toPrint);
				break;
		}

		PrinterJob print = PrinterJob.createPrinterJob();
		if (toPrint != null && print.showPrintDialog(gui))
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
			switch(NotezDialog.showQuestionDialog(gui, "No user to share with",
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
					getGui().switchToBody(NotezGuiBody.SETTINGS);

					getGui().getSettingsPane().switchToPane(NotezSettingsPaneTabPane.SHARE);

					// TODO $ddd
//					addNewUser();
					break;
			}
			return;
		}

		NotezRemoteUser user = NotezDialog.showShareWithDialog(gui, "Share Notez",
			"Share this Notez with ", NotezRemoteSync.getAllUsers());

		if (user != null)
		{
			String msg = "";
			switch(NotezShareBase.shareNotez(this, noteFile.get(), user.getShare()))
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

			NotezDialog.showInfoDialog(gui, "Share Notez with " + user.getUsername(), msg);
		}
	}

	public void loadData(NotezData data)
	{
		NotezDataUtil.equalize(data, getData());
	}

	public NotezDataProperties getData()
	{
		return data;
	}

	public File getNoteFile()
	{
		return noteFileProperty().get();
	}

	public ReadOnlyObjectProperty<File> noteFileProperty()
	{
		return noteFile.getReadOnlyProperty();
	}

	public NotezGui getGui()
	{
		return gui;
	}
}
