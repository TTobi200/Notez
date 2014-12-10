/*
 * $Header$
 * 
 * $Log$ Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui.controller;

import static de.util.NotezProperties.NOTEZ_ALWAYS_SAVE_ON_EXIT;
import static de.util.NotezProperties.NOTEZ_LET_RECEIVER_RUNNING;
import static de.util.NotezProperties.NOTEZ_MAIL_HOST;
import static de.util.NotezProperties.NOTEZ_MAIL_PORT;
import static de.util.NotezProperties.NOTEZ_MAIL_USER;
import static de.util.NotezProperties.NOTEZ_MAIL_USE_SSL;
import static de.util.NotezProperties.NOTEZ_RECEIVER_ON_STARTUP;
import static de.util.NotezProperties.NOTEZ_REMOTE_FOLDER;
import static de.util.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.util.NotezProperties.NOTEZ_BTN_ADD;
import static de.util.NotezProperties.NOTEZ_BTN_GROUP;
import static de.util.NotezProperties.NOTEZ_BTN_PIN;
import static de.util.NotezProperties.NOTEZ_BTN_PRINT;
import static de.util.NotezProperties.NOTEZ_BTN_REMOVE;
import static de.util.NotezProperties.NOTEZ_BTN_SAVE;
import static de.util.NotezProperties.NOTEZ_BTN_SHARE;
import static de.util.NotezProperties.get;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion.Setting;

import de.gui.NotezDialog;
import de.util.NotezFileUtil;
import de.util.NotezProperties;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;

public class NotezControllerListeners extends
                NotezControllerListenerBase<NotezController>
{
    public static final Image IMAGE_PICK_NOTE = new Image(
        NotezFileUtil.getResourceStream(NotezController.ICON_PICK_NOTE));
    public static final Image IMAGE_DISSOLVE = new Image(
        NotezFileUtil.getResourceStream(NotezController.ICON_DISSOLVE));

    public NotezControllerListeners(NotezController controller)
    {
        super(controller);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        loadIcons();

        // TODO Remove scrollbars on startup
        setNodeForDragging(c.toolBar);
        setNodeForDragging(c.txtTitle); // TODO now text selection by mouse is
                                        // impossible :-(
        setAsResizeCorner(c.resize);
        addVisibleToolbarNodeHider(c.toolBar);
        addVisibleNodeHider(c.hBoxButtom, c.fileLink);
        setAsDndSource(c.pickNote);
        setAsDndTarget(c.toolBar);

        switchTo(c.borderPaneNotez);

        initPagination();

        doOnShowing(() -> setAccelerators());
        // FORTEST

        c.pickNote.setImage(IMAGE_PICK_NOTE);
        grouped.addListener((g, o, n) -> {
            c.pickNote.imageProperty().set(
                n.booleanValue() ? IMAGE_DISSOLVE : IMAGE_PICK_NOTE);
        });

        try
        {
            c.loadNote(c.note);
        }
        catch(IOException e)
        {
        }
    }

    private void loadIcons()
    {
        c.iVPinned = new Image(
            NotezFileUtil.getResourceStream(NotezController.ICON_PINNED));
        c.iVUnpinned = new Image(
            NotezFileUtil.getResourceStream(NotezController.ICON_UNPINNED));
    }

    /**
     * Method to add all settings for Notez.
     *
     * @param all
     *            = new {@link HashMap} with {@link Setting}
     */
    @Override
    protected void initSettings()
    {
        initProperties();

        // Init remote user table
        c.tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());
        c.colUsername.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
            "username"));
        c.colShare.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
            "share"));
    }

    protected void initProperties()
    {
        c.txtPropNotezWorkFold.setText(
            get(NOTEZ_WORK_FOLDER));
        c.txtPropNotezRemoteFold.setText(
            get(NOTEZ_REMOTE_FOLDER));

        c.txtEmail.setText(
            get(NOTEZ_MAIL_USER));
        c.txtHost.setText(
            get(NOTEZ_MAIL_HOST));
        c.txtPort.setText(
            get(NOTEZ_MAIL_PORT));

        // Button invisibility
        c.cbAddNotez.selectedProperty().bindBidirectional(
            c.btnAdd.visibleProperty());
        c.cbPinNotez.selectedProperty().bindBidirectional(
            c.btnPin.visibleProperty());
        c.cbRemoveNotez.selectedProperty().bindBidirectional(
            c.btnDelete.visibleProperty());
        c.cbSaveNotez.selectedProperty().bindBidirectional(
            c.btnSave.visibleProperty());
        c.cbShareNotez.selectedProperty().bindBidirectional(
            c.btnShare.visibleProperty());
        c.cbGroupNotez.selectedProperty().bindBidirectional(
            c.pickNote.visibleProperty());
        c.cbPrintNotez.selectedProperty().bindBidirectional(
            c.btnPrint.visibleProperty());

        // Bin check boxes <-> settings
        NotezProperties.bindBoolean(NOTEZ_RECEIVER_ON_STARTUP,
            c.cbStartRecOnStartup.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_LET_RECEIVER_RUNNING,
            c.cbStartRecKeepRun.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_ALWAYS_SAVE_ON_EXIT,
            c.cbAlwaysAskToSave.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_PIN,
            c.cbPinNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_GROUP,
            c.cbGroupNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_SHARE,
            c.cbShareNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_ADD,
            c.cbAddNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_SAVE,
            c.cbSaveNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_REMOVE,
            c.cbRemoveNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_PRINT,
            c.cbPrintNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_MAIL_USE_SSL,
            c.cbUseSSL.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_PIN,
            c.cbPinNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_GROUP,
            c.cbGroupNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_SHARE,
            c.cbShareNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_ADD,
            c.cbAddNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_SAVE,
            c.cbSaveNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_REMOVE,
            c.cbRemoveNotez.selectedProperty());
        NotezProperties.bindBoolean(NOTEZ_BTN_PRINT,
            c.cbPrintNotez.selectedProperty());
    }

    @Override
    protected void initPinNote()
    {
        c.btnPin.selectedProperty()
            .addListener(
                (s, o, n) -> {
                    ((ImageView)c.btnPin.getGraphic()).setImage(n.booleanValue() ? c.iVPinned
                                    : c.iVUnpinned);
                    // btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
                    c.getStage().setAlwaysOnTop(n.booleanValue());

                    // XXX
                    // do not bind as there are needed several, which could
                    // collide
                    // if(c.noteParent.get() != null)
                    // {
                    // c.noteParent.get().btnPin.setSelected(n.booleanValue());
                    // }
                    // if(c.noteChild.get() != null)
                    // {
                    // c.noteChild.get().btnPin.setSelected(n.booleanValue());
                    // }
                });
    }

    @Override
    protected void initNoteChanged()
    {
        super.initNoteChanged();

        c.btnSave.disableProperty().bind(c.noteChanged.not());
        c.btnSave.disabledProperty().addListener((bool, old, newOne) -> {
            c.btnSave.getGraphic().setOpacity(newOne ? 0.3 : 1);
        });
    }

    protected void initPagination()
    {
        c.data.getPageData()
            .textProperty()
            .bindBidirectional(c.txtNote.textProperty());
        c.lblPage.textProperty()
            .bind(
                Bindings.concat(c.data.getPageData()
                    .curPageIndexProperty()
                    .add(1), " / ",
                    c.data.getPageData().sizeProperty()));
        c.btnPrevPage.disableProperty().bind(
            c.data.getPageData().curPageIndexProperty().isEqualTo(0));
        // XXX $DDD enable pagination
        // c.data.curTextProperty().bind(c.txtNote.textProperty());
        // c.data.saveText();
        // c.lblPage.textProperty().bind(
        // Bindings.concat(c.data.curIndexProperty().add(1), " / ",
        // NotezObservablesUtil.sizePropertyForList(c.data.getPages())));
        // c.btnPrevPage.disableProperty().bind(
        // c.data.curIndexProperty().isEqualTo(0));
        //
        // c.data.curDataProperty().addListener((c, o, n) -> {
        // o.curTextProperty().unbind();
        // this.c.txtNote.setText(n.curTextProperty().get());
        // n.curTextProperty().bind(this.c.txtNote.textProperty());
        // });
    }

    protected void switchTo(Node node)
    {
        removeAllFromStack(c.stack);
        c.stack.getChildren().add(node);
    }

    protected void removeAllFromStack(StackPane stack)
    {
        stack.getChildren().clear();
    }

    public void setAccelerators()
    {
        Scene s = c.stage.getScene();

        addAcceleratorToScene(s, KeyCode.S, () -> {
            try
            {
                c.saveNote();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });
        addAcceleratorToScene(s, KeyCode.N, () -> {
            try
            {
                c.addNewNote();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    protected void addAcceleratorToScene(Scene s, KeyCode key, Runnable run)
    {
        s.getAccelerators().put(
            new KeyCodeCombination(key, KeyCombination.SHORTCUT_DOWN), run);
    }

    protected void addFileLink(final Hyperlink link, File note)
    {
        link.setText(note.getAbsolutePath());
        link.setOnAction(e -> {
            try
            {
                NotezFileUtil.openParentFolderInBrowser(new File(link.getText()));
            }
            catch(Exception e1)
            {
                try
                {
                    NotezDialog.showExceptionDialog(c.stage,
                        "Error while opening folder",
                        "Could not open the parent folder!", e1);
                }
                catch(Exception e2)
                {
                    e1.printStackTrace();
                }
            }
        });
    }
}
