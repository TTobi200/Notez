package de.gui.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import de.gui.NotezDialog;
import de.gui.NotezFrame;
import de.notez.NotezProperties;
import de.notez.NotezRemoteSync;
import de.notez.data.NotezData;
import de.notez.data.NotezDataProperties;
import de.notez.data.base.BaseNotezDataProperties;
import de.notez.parser.NotezParsers;
import de.util.NotezFXMLInitializable;
import de.util.NotezFileUtil;

public abstract class NotezControllerBase<L extends NotezControllerListenerBase<?>>
                implements
                NotezFXMLInitializable
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

    protected SimpleObjectProperty<File> note;
    // protected File note;
    protected Stage stage;
    protected Integer idx;

    /**
     * boolean binding indicating whether the text in {@link #txtNote} differs
     * from the saved on
     */
    protected BooleanBinding noteChanged;

    protected L c;

    /** the parent of this note */
    protected ObjectProperty<NotezControllerBase<?>> noteParent;
    /** the child of this node */
    protected ObjectProperty<NotezControllerBase<?>> noteChild;

    protected NotezDataProperties data;

    protected double initialX;
    protected double initialY;

    public NotezControllerBase(Stage stage, int idx)
    {
        this(stage, null, idx);
    }

    public NotezControllerBase(Stage stage, File note, int idx)
    {
        this.stage = stage;
        this.note = new SimpleObjectProperty<>(note);
        this.idx = new Integer(idx);

        noteParent = new SimpleObjectProperty<>(null);
        noteChild = new SimpleObjectProperty<>(null);

        c = creNotezControllerListener();
        data = new BaseNotezDataProperties(
            NotezFileUtil.removeEnding(note.getName()));
        data.getStageData().bind(stage);
    }

    protected abstract L creNotezControllerListener();

    /**
     * Method to add a new notez.
     *
     * @throws IOException
     *             See: {@link NotezFrame#createNotezFrame()}
     */
    @FXML
    public void addNewNote() throws IOException
    {
        NotezFrame.createNotezFrame().getStage().show();
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
            if(NotezProperties.getBoolean(
                NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT))
            {
                saveNote();
            }
            else
            {
                switch(NotezDialog.showRememberQuestionDialog(stage,
                    "Save Changes",
                    "Do you like to save the changes?",
                    NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT,
                    true))
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
        }

        if(NotezRemoteSync.isRunning() && NotezFrame.notezOpened.size() == 1)
        {
            switch(NotezDialog.showRememberQuestionDialog(stage,
                "Exit Notez Receiver",
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

        NotezProperties.save();
        NotezFrame.notezOpened.remove(this);
        stage.hide();
    }

    protected File genNotezFile(String notezName)
    {
        return NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
            NotezProperties.get(
                NotezProperties.NOTEZ_WORK_FOLDER,
                NotezFrame.DEF_LOCAL_NOTEZ_FOLDER)
                            + notezName
                            + NotezFrame.NOTEZ_FILE_POSFIX) : note.get();
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
                if(note != null && note.get().exists())
                {
                    note.get().delete();
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
        if(note != this.note.get())
        {
            this.note.get().delete();
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
        this.note.set(note);
        // TODO transform into the new format
        // data.saveText();
        c.stageSize.setX(stage.getX());
        c.stageSize.setY(stage.getY());
        c.stageSize.setWidth(stage.getWidth());
        c.stageSize.setHeight(stage.getHeight());
    }

    protected void pinToNode(NotezControllerBase<?> noteToPin)
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
        return note.get();
    }

    public NotezData getData()
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

    protected abstract void setNoteText(String text);

    /**
     * Method to load note from file.
     *
     * @param note
     *            = the {@link File} to load note from
     * @return true if file could be loaded
     * @throws IOException
     *             See: {@link Files#readAllBytes(java.nio.file.Path)}
     */
    public boolean loadNote(File note) throws IOException
    {
        if(NotezFileUtil.fileCanBeLoad(note))
        {
            return loadNote(NotezParsers.parseFile(note));
        }

        return false;
    }

    public abstract boolean loadNote(NotezData data) throws IOException;

    // TODO just for tests
    @FXML
    protected void prevPage()
    {
        data.getPageData().prevPage();
    }

    @FXML
    protected void nextPage()
    {
        data.getPageData().nextPage();
    }
}
