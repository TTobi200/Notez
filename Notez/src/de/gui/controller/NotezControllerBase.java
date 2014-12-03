package de.gui.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import de.gui.NotezData;
import de.gui.NotezDialog;
import de.gui.NotezFrame;
import de.util.NotezFXMLInitializable;
import de.util.NotezFileUtil;
import de.util.NotezProperties;
import de.util.NotezRemoteSync;
import de.util.notez.parser.NotezParsers;

public abstract class NotezControllerBase<D extends NotezData, L extends NotezControllerListenerBase<?>>
				implements NotezFXMLInitializable
{
	// TODO Property onLoading
	// TODO Property onLoaded
	// TODO Property onSaving
	// TODO Property onSaved
	// TODO Property Load
	// TODO Property Saved
	// TODO Property onInitializing
	// TODO Property onInitialized
	// TODO Property Initialized
	// TODO Property onPinning
	// TODO Property onPinned
	// TODO Property pinned

	@FXML
	protected Region toolBar;

	protected File note;
	protected Stage stage;
	protected Integer idx;

	/**
	 * boolean binding indicating whether the text in {@link #txtNote} differs
	 * from the saved on
	 */
	protected BooleanBinding noteChanged;

	protected L c;

	/** the parent of this note */
	protected ObjectProperty<NotezControllerBase<?, ?>> noteParent;
	/** the child of this node */
	protected ObjectProperty<NotezControllerBase<?, ?>> noteChild;

	protected D data;

	protected double initialX;
	protected double initialY;

	public NotezControllerBase(Stage stage, int idx)
	{
		this(stage, null, idx);
	}

	public NotezControllerBase(Stage stage, File note, int idx)
	{
		this.stage = stage;
		this.note = note;
		this.idx = new Integer(idx);

		noteParent = new SimpleObjectProperty<>(null);
		noteChild = new SimpleObjectProperty<>(null);

		c = creNotezControllerListener();
		data = creNotezData();
	}

	protected abstract L creNotezControllerListener();

	protected abstract D creNotezData();

	/**
	 * Method to add a new notez.
	 *
	 * @throws IOException
	 *             See: {@link NotezFrame#createNotezFrame()}
	 */
	@FXML
	public void addNewNote() throws IOException
	{
		NotezFrame.createNotezFrame();
	}

	/**
	 * Method to close this notez. User gets asked if he likes to save the
	 * notez.
	 *
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
	 *
	 * @throws Exception
	 *             See: {@link #saveNote(File)}
	 */
	public void closeNote(boolean askToSave) throws Exception
	{
		if(noteChanged.get() && askToSave)
		{
			switch(NotezDialog.showQuestionDialog(stage, "Save Changes",
				"Do you like to save the changes?"))
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
					saveNote();
					break;
			}
		}

		if(NotezRemoteSync.isRunning() && NotezFrame.notezOpened.size() == 1)
		{
			switch(NotezDialog.showQuestionDialog(stage, "Exit Notez Receiver",
				"Keep Notez-Receiver running in background?"))
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

		NotezProperties.save();
		NotezFrame.notezOpened.remove(this);
		stage.hide();
	}

	protected File genNotezFile(String notezName)
	{
		return NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
			NotezProperties.get(
				NotezProperties.PROP_NOTEZ_FOLDER,
				NotezFrame.DEF_LOCAL_NOTEZ_FOLDER)
							+ notezName
							+ NotezFrame.NOTEZ_FILE_POSFIX)
						: note;
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

	/**
	 * Method to save current note.
	 *
	 * @throws Exception
	 *             See: {@link FileWriter}
	 */
	@FXML
	protected abstract void saveNote() throws Exception;

	/**
	 * Method to save given note to file.
	 *
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

		NotezParsers.save(this, note);
		data.saveText();
		c.stageSize.setX(stage.getX());
		c.stageSize.setY(stage.getY());
		c.stageSize.setWidth(stage.getWidth());
		c.stageSize.setHeight(stage.getHeight());
	}

	protected void pinToNode(NotezControllerBase<?, ?> noteToPin)
	{
		if(noteToPin == this)
		{
			return;
		}

		if(noteParent.get() != null)
		{
			// TODO may change to the new note?
			return;
		}

		noteParent.set(noteToPin);
	}

	public Stage getStage()
	{
		return stage;
	}

	public File getNoteFile()
	{
		return note;
	}

	public D getData()
	{
		return data;
	}

	/**
	 * Get the text of the current note. if the note consists of several pages,
	 * behaviour is
	 * undefined.<br>
	 * This method is only used for backward compatibility.
	 *
	 * @return The text of the current note
	 */
	public abstract String getNoteText();
}
