package de.notez;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.print.PrinterJob;
import javafx.scene.control.TextArea;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.event.*;

import de.gui.*;
import de.gui.NotezGui.NotezGuiBody;
import de.gui.comp.NotezSettingsPane.NotezSettingsPaneTabPane;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.notez.data.*;
import de.notez.data.base.BaseNotezDataProperties;
import de.notez.event.NotezNoteEvent;
import de.notez.parser.*;
import de.notez.prop.NotezProperties;
import de.notez.share.NotezShareBase;
import de.util.*;
import de.util.log.NotezLog;

/**
 * The model of a note.
 * 
 * @author ddd
 */
public class NotezNote
{
	/** The String added at each end of a to do for marks */
	public static final String TODO_BOX = "[ ]";

	/** all notes that currently exist */
	private static ObservableList<NotezNote> notes = FXCollections.observableArrayList();

	/**
	 * @return a list of all notes currently existing
	 */
	public static ObservableList<NotezNote> notezList()
	{
		return FXCollections.unmodifiableObservableList(notes);
	}

	/** The data of a note */
	private NotezDataProperties data;

	/** The file, this note is or should be saved in */
	protected ReadOnlyObjectWrapper<File> noteFile;

	/** whether this note or his data have changed */
	protected ReadOnlyBooleanWrapper noteChanged;

	/** The gui showing this note */
	protected NotezGui gui;

	/** the parentnote of this one */
	protected ReadOnlyObjectWrapper<NotezNote> noteParent;
	/** the childnote of this one */
	protected ReadOnlyObjectWrapper<NotezNote> noteChild;

	protected EventHandlerManager eventHandlerManager;

	/**
	 * Create a new note, that is or should be saved in the given file.
	 * 
	 * @param file
	 *            The file for saving/loading of this note
	 */
	public NotezNote(File file)
	{
		data = new BaseNotezDataProperties(
			NotezFileUtil.removeEnding(file.getName()));

		noteFile = new ReadOnlyObjectWrapper<File>(file);

		noteParent = new ReadOnlyObjectWrapper<NotezNote>(null);
		noteChild = new ReadOnlyObjectWrapper<NotezNote>(null);

		eventHandlerManager = new EventHandlerManager(this);

		loadData(file);

		setListeners();

		PlatformImpl.runAndWait(() ->
		{
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
	}

	private BooleanProperty titleChanged;
	private BooleanProperty stageDataChanged;
	private BooleanProperty pagedDataChanged;

	private void setListeners()
	{
		noteChanged = new ReadOnlyBooleanWrapper();

		// only parent can be set from outside, so the old parent has to get
		// a null child while the new parents child has to be set
		noteParent.addListener((p, o, n) ->
		{
			if(Objects.nonNull(o) && o.noteChildProperty().get() == this)
			{
				o.setNoteChild(null);
			}
			if(Objects.nonNull(n))
			{
				n.setNoteChild(this);
			}
		});

		noteChild.addListener((p, o, n) ->
		{
			if(Objects.nonNull(o) && o.noteParentProperty().get() == this)
			{
				o.setNoteParent(null);
			}
			// TODO
		});
		titleChanged = new SimpleBooleanProperty();
		stageDataChanged = new SimpleBooleanProperty();
		pagedDataChanged = new SimpleBooleanProperty();

		titleChanged.bind(getData().titleProperty().isNotEqualTo(
			getData().getTitle()));
		stageDataChanged.bind(getData().getStageData()
			.stageXProperty()
			.isNotEqualTo(getData().getStageData().getStageX(), .1)
			.or(getData().getStageData()
				.stageYProperty()
				.isNotEqualTo(getData().getStageData().getStageY(), .1))
			.or(getData().getStageData()
				.stageWidthProperty()
				.isNotEqualTo(getData().getStageData().getStageWidth(), .1))
			.or(getData().getStageData()
				.stageHeightProperty()
				.isNotEqualTo(getData().getStageData().getStageHeight(), .1)));

		BooleanBinding b = Bindings.when(
			getData().getPageData()
				.sizeProperty()
				.isEqualTo(getData().getPageData().getPages().size()))
			.then(false)
			.otherwise(true);

		BooleanBinding notEqual = null;

		for(NotezTextDataProperties textData : getData().getPageData()
			.getPagesObservable())
		{
			BooleanBinding bb = textData.textProperty().isNotEqualTo(
				textData.getText());
			if(Objects.isNull(notEqual))
			{
				notEqual = bb;
			}
			else
			{
				notEqual = notEqual.or(bb);
			}
		}

		pagedDataChanged.bind(b.or(notEqual));

		EventHandler<NotezNoteEvent> h = e ->
		{
			BooleanBinding nnotEqual = null;

			for(NotezTextDataProperties textData : getData().getPageData()
				.getPagesObservable())
			{
				BooleanBinding bb = textData.textProperty().isNotEqualTo(
					textData.getText());
				if(Objects.isNull(nnotEqual))
				{
					nnotEqual = bb;
				}
						else
						{
							nnotEqual = nnotEqual.or(bb);
						}
					}

					pagedDataChanged.bind(b.or(nnotEqual));
				};

		setOnSaved(h);
		setOnLoaded(h);

		noteChanged.bind(titleChanged.or(pagedDataChanged).or(stageDataChanged));
	}

	/**
	 * @return The title of this note
	 */
	public String getTitle()
	{
		return getData().getTitle();
	}

	/**
	 * Show this' note gui
	 */
	public void show()
	{
		getGui().show();
	}

	/**
	 * Close/hide this note
	 * 
	 * @param askToSave
	 *            Whether to show a dialog asking the suer to save this note,
	 *            when it has changed.
	 */
	public void close(boolean askToSave)
	{
		if(noteChanged.get() && askToSave)
		{
			if(NotezSystem.getSystemProperties().getBoolean(
				NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT))
			{
				save();
			}
			else
			{
				try
				{
					switch(NotezDialog.showRememberQuestionDialog(gui,
						"Save Changes",
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
		
		getGui().hide();

		if(NotezRemoteSync.isRunning()
			&& NotezNote.notezList()
				.stream()
				.filter(n -> n.getGui().isShowing())
				.count() == 0)
		{
			try
			{
				switch(NotezDialog.showRememberQuestionDialog(gui,
					"Exit Notez Receiver",
					"Keep Notez-Receiver running in background?",
					NotezProperties.NOTEZ_LET_RECEIVER_RUNNING))
				{
					default:
					case CANCEL:
					case CLOSE:
					case NO:
						NotezSystem.exit();
						break;
					case OK:
					case YES:
						// Do nothing
						break;
				}
			}
			catch(IOException | InterruptedException e)
			{
				NotezLog.error(
					"error while asking user for closing the the server", e);
				NotezSystem.exit();
			}
		}
	}

	/**
	 * Delete this note if the user agrees
	 */
	public void delete()
	{
		delete(true);
	}

	/**
	 * If given, ask the user whether to really delete this note.
	 * If agreed or should not ask, delete this note.
	 * 
	 * @param askUser
	 *            Whether to ask the user for confirming the deletion
	 */
	public void delete(boolean askUser)
	{
		if(Objects.nonNull(System.getSecurityManager()))
		{
			System.getSecurityManager().checkPermission(NotezPermission.PERMISSION_NOTEZ_DELETE);
		}
		
		if(askUser)
		{
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

						break;
				}
			}
			catch(IOException | InterruptedException e)
			{
				NotezLog.error("error while asking user for deletion", e);
			}
		}

		if(noteFile != null && noteFile.get().exists())
		{
			noteFile.get().delete();
		}
		// TODO Animate that? :-)
		close(false); // don't ask to save
		notes.remove(this);
	}

	/**
	 * @param file
	 *            The file to load the data from
	 */
	public void loadData(File file)
	{
		if(NotezFileUtil.fileCanBeLoad(file))
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

	/**
	 * Sve this note in its file
	 */
	public void save()
	{
		save(NotezFileUtil.genNotezFile(getData().getTitle()));
	}

	/**
	 * @param note
	 *            The note to save this file into
	 */
	public void save(File note)
	{
		if(Objects.isNull(note))
		{
			note = this.noteFile.get();
		}
		// If opened notez differes from to saving note
		else if(!note.equals(this.noteFile.get()))
		{
			this.noteFile.get().delete();
		}

		File parent = note.getParentFile();
		if(parent != null && !parent.exists())
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
				NotezLog.error(
					"Error while asking the user for creating the notez folder",
					e);
			}
		}

		try
		{
			NotezParsers.save(this, note);
			fireEvent(NotezNoteEvent.NOTEZ_NOTE_SAVED_EVENT_TYPE);
		}
		catch(IOException e)
		{
			NotezLog.error("Error while saving", e);
		}
		this.noteFile.set(note);
	}

	/**
	 * Print this note.<br>
	 * <br>
	 * The user will be asked whether to print as is or as to do-list
	 * @throws Exception
	 */
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
				toPrint.setText(formatToTODO(toPrint.getText()));
				break;
		}

		PrinterJob print = PrinterJob.createPrinterJob();
		if(toPrint != null && print.showPrintDialog(gui))
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

	/**
	 * Format the given String to be represented as a to do-list. For this
	 * purpose every line non-empty line is resized to the same size and the
	 * {@link #TODO_BOX} is added at itsend.
	 * 
	 * @param toPrint
	 *            The String to be formatted
	 * @return The given String formatted as to do-list
	 */
	protected String formatToTODO(String toPrint)
	{
		String[] tmp = toPrint.split("\n");
		StringBuilder result = new StringBuilder("TODO: ");

		int maxLen = 0;
		for(String s : tmp)
		{
			if(s.length() > maxLen)
			{
				maxLen = s.length();
			}
		}

		result.append(getData().getPageData().getText())
			.append("\r\n")
			.append("Date: ")
			.append(
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance()
					.getTime()))
			.append("\r\n\r\n");

		for(String s : tmp)
		{
			result.append(s);

			if(s.isEmpty())
			{
				continue;
			}

			for(int i = s.length(); i <= maxLen; i++)
			{
				result.append(" ");
			}

			result.append(TODO_BOX).append("\r\n");
		}

		return result.toString();
	}

	/**
	 * Start the operation for sharing this note.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void shareNote() throws IOException, InterruptedException
	{
		if(NotezRemoteSync.getAllUsers().isEmpty())
		{
			switch(NotezDialog.showQuestionDialog(gui, "No user to share with",
				"You have no user registered to share with.\r\n"
								+ "Like to add one?"))
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

					getGui().getSettingsPane().switchToPane(
						NotezSettingsPaneTabPane.SHARE);

					// TODO $ddd
					NotezRemoteSync.addNewUser(getGui());
					break;
			}
			return;
		}

		NotezRemoteUser user = NotezDialog.showShareWithDialog(gui,
			"Share Notez",
			"Share this Notez with ", NotezRemoteSync.getAllUsers());

		if(user != null)
		{
			String msg = "";
			switch(NotezShareBase.shareNotez(this, noteFile.get(),
				user.getShare()))
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

			NotezDialog.showInfoDialog(gui,
				"Share Notez with " + user.getUsername(), msg);
		}
	}

	/**
	 * @param data
	 *            The new data for this note
	 */
	public void loadData(NotezData data)
	{
		if(Objects.nonNull(data))
		{
			NotezDataUtil.equalize(data, getData());
		}
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

	public ReadOnlyBooleanProperty changedProperty()
	{
		return noteChanged.getReadOnlyProperty();
	}

	public ReadOnlyObjectProperty<NotezNote> noteParentProperty()
	{
		return noteParent.getReadOnlyProperty();
	}

	public ReadOnlyObjectProperty<NotezNote> noteChildProperty()
	{
		return noteChild.getReadOnlyProperty();
	}

	public void setNoteParent(NotezNote note)
	{
		if(this == note)
		{
			return;
		}

		noteParent.set(note);
	}

	protected void setNoteChild(NotezNote note)
	{
		if(this == note)
		{
			return;
		}

		noteChild.set(note);
	}

	@Override
	public String toString()
	{
		return new StringBuilder("NotezNote: ").append(data.toString())
			.toString();
	}

	/**
	 * @return The index of this note in the list of all notes currently
	 *         existing
	 */
	public int getIndex()
	{
		return notezList().indexOf(this);
	}

	public <T extends NotezNoteEvent> void addEventHandler(
					EventType<T> eventType,
					EventHandler<? super T> eventHandler)
	{
		eventHandlerManager.addEventHandler(eventType, eventHandler);
	}

	public void setOnSaved(EventHandler<? super NotezNoteEvent> handler)
	{
		eventHandlerManager.addEventHandler(
			NotezNoteEvent.NOTEZ_NOTE_SAVED_EVENT_TYPE, handler);
	}

	public void setOnLoaded(EventHandler<? super NotezNoteEvent> handler)
	{
		eventHandlerManager.addEventHandler(
			NotezNoteEvent.NOTEZ_NOTE_LOADED_EVENT_TYPE, handler);
	}

	protected void fireEvent(EventType<NotezNoteEvent> type)
	{
		eventHandlerManager.dispatchEvent(new NotezNoteEvent(this, type),
			new EventDispatchChainImpl());
	}
}
