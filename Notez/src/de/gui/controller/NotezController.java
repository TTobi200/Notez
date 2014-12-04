/*
 * $Header$
 * 
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui.controller;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import de.util.NotezProperties;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.share.NotezShareBase;

public class NotezController extends
                NotezControllerBase<NotezPagedData, NotezControllerListeners>
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
    public static final String ICON_PICK_NOTE = "include/icons/pinToNote.png";
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
    protected TextField txtPropNotezWorkFold;
    @FXML
    protected TextField txtPropNotezRemoteFold;
    @FXML
    protected CheckBox cbStartRecOnStartup;
    @FXML
    protected CheckBox cbStartRecKeepRun;
    @FXML
    protected CheckBox cbAlwaysAskToSave;

    @FXML
    protected CheckBox cbPinNotez;
    @FXML
    protected CheckBox cbRemoveNotez;
    @FXML
    protected CheckBox cbAddNotez;
    @FXML
    protected CheckBox cbSaveNotez;
    @FXML
    protected CheckBox cbShareNotez;
    @FXML
    protected CheckBox cbGroupNotez;

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
    protected TableColumn<NotezRemoteUser, String> colShare;

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
        NotezProperties.set(NotezProperties.PROP_NOTEZ_WORK_FOLDER,
            txtPropNotezWorkFold.getText());

        NotezProperties.set(NotezProperties.PROP_NOTEZ_REMOTE_FOLDER,
            txtPropNotezRemoteFold.getText());

        NotezProperties.set(NotezProperties.PROP_START_RECEIVER,
            String.valueOf(cbStartRecOnStartup.isSelected()));
        NotezProperties.set(NotezProperties.PROP_LET_RECEIVER_RUNNING,
            String.valueOf(cbStartRecKeepRun.isSelected()));

        NotezProperties.set(NotezProperties.PROP_ALWAYS_SAVE_ON_EXIT,
            String.valueOf(cbAlwaysAskToSave.isSelected()));

        NotezProperties.set(NotezProperties.PROP_BTN_PIN_VISIBLE,
            String.valueOf(cbPinNotez.isSelected()));
        NotezProperties.set(NotezProperties.PROP_BTN_GROUP_VISIBLE,
            String.valueOf(cbGroupNotez.isSelected()));
        NotezProperties.set(NotezProperties.PROP_BTN_SHARE_VISIBLE,
            String.valueOf(cbShareNotez.isSelected()));
        NotezProperties.set(NotezProperties.PROP_BTN_ADD_VISIBLE,
            String.valueOf(cbAddNotez.isSelected()));
        NotezProperties.set(NotezProperties.PROP_BTN_SAVE_VISIBLE,
            String.valueOf(cbSaveNotez.isSelected()));
        NotezProperties.set(NotezProperties.PROP_BTN_REMOVE_VISIBLE,
            String.valueOf(cbRemoveNotez.isSelected()));

        // TODO only switch if valid?
        c.switchTo(borderPaneNotez);
    }

    /**
     * Method to cancel editing settings.
     */
    @FXML
    public void cancelSettings()
    {
        // Restore default values
        c.initProperties();

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
        NotezRemoteUser user = NotezDialog.showShareWithDialog(stage,
            "Share Notez",
            "Share this Notez with ",
            NotezRemoteSync.getAllUsers());

        if(user != null)
        {
            String msg = "";
            switch(NotezShareBase.shareNotez(this, note,
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
            }

            NotezDialog.showInfoDialog(stage, "Share Notez with "
                                              + user.getUsername(), msg);
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

    @FXML
    protected void deleteUser() throws IOException, InterruptedException
    {
        NotezRemoteUser user = tableRemoteuser.getSelectionModel()
            .getSelectedItem();
        if(user != null)
        {
            switch(NotezDialog.showQuestionDialog(stage, "Delete User",
                "Do you really want to delete user " + user.getUsername()
                                + " ?"))
            {
                case NO:
                case CLOSE:
                case CANCEL:
                default:
                    break;

                case OK:
                case YES:
                    NotezRemoteSync.removeUser(user);
                    break;
            }
        }
    }

    @FXML
    protected void addNewUser() throws IOException, InterruptedException
    {
        NotezRemoteUser user = NotezDialog.showAddUserDialog(stage);

        if(user != null && user.getUsername() != null
           && user.getShare() != null)
        {
            NotezRemoteSync.addUser(user);
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