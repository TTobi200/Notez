/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui.controller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
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
import de.gui.NotezFrame;
import de.util.NotezFileUtil;
import de.util.NotezObservablesUtil;
import de.util.NotezProperties;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.notez.NotezData;
import de.util.notez.parser.NotezParsers;

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

        doOnFirstShowing(() -> setAccelerators());
        // FORTEST

        c.pickNote.setImage(IMAGE_PICK_NOTE);
        grouped.addListener((g, o, n) -> {
            c.pickNote.imageProperty().set(
                n.booleanValue() ? IMAGE_DISSOLVE : IMAGE_PICK_NOTE);
        });
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
        c.txtPropNotezFold.setText(NotezProperties.get(
            NotezProperties.PROP_NOTEZ_FOLDER));
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
        c.data.curTextProperty().bind(c.txtNote.textProperty());
        c.data.saveText();
        // TODO $Dauerdaddlah
        // noteChanged = lastSavedText.isNotEqualTo(txtNote.textProperty());
        // noteChanged = noteChanged.or(
        // lastSavedSize.xProperty().isNotEqualTo(stage.xProperty()))
        // .or(lastSavedSize.yProperty().isNotEqualTo(stage.yProperty()))
        // .or(lastSavedSize.widthProperty().isNotEqualTo(
        // stage.widthProperty()))
        // .or(lastSavedSize.heightProperty().isNotEqualTo(
        // stage.heightProperty()));
        c.lblPage.textProperty().bind(
            Bindings.concat(c.data.curIndexProperty().add(1), " / ",
                NotezObservablesUtil.sizePropertyForList(c.data.getPages())));
        c.btnPrevPage.disableProperty().bind(
            c.data.curIndexProperty().isEqualTo(0));

        c.data.curDataProperty().addListener((c, o, n) -> {
            o.curTextProperty().unbind();
            this.c.txtNote.setText(n.curTextProperty().get());
            n.curTextProperty().bind(this.c.txtNote.textProperty());
        });
    }

    /**
     * Method to load note from file.
     *
     * @param note
     *            = the {@link File} to load note from
     * @return true if file could be loaded
     * @throws IOException
     *             See: {@link Files#readAllBytes(java.nio.file.Path)}
     */
    @Override
    protected boolean loadNote(File note) throws IOException
    {
        addFileLink(c.fileLink, note);
        c.getStage().titleProperty().bind(c.txtTitle.textProperty());
        c.txtTitle.setText(note.getName().replace(NotezFrame.NOTEZ_FILE_POSFIX,
            ""));

        if(NotezFileUtil.fileCanBeLoad(note))
        {
            NotezData data = NotezParsers.parseFile(note);
            Point2D pos = data.getPosition();

            Point2D size = data.getSize();
            c.stage.setWidth(size.getX());
            c.stage.setHeight(size.getY());

            final Dimension D = Toolkit.getDefaultToolkit().getScreenSize();
            if(pos.getX() < 0d)
            {
                pos = new Point2D(0d, pos.getY());
            }
            else if(pos.getX() + size.getX() > D.getWidth())
            {
                pos = new Point2D(D.getWidth() - pos.getX(), pos.getY());
            }

            if(pos.getY() < 0d)
            {
                pos = new Point2D(pos.getX(), 0d);
            }
            else if(pos.getY() + size.getY() > D.getHeight())
            {
                pos = new Point2D(pos.getX(), D.getHeight() - pos.getY());
            }

            final Point2D p = pos;
            c.txtNote.setText(data.getText());
            doOnFirstShowing(() -> moveStageAnimatedTo(p));

            // File exists and loaded
            return true;
        }
        // File does not exists created new
        return false;
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
        link.setOnAction(e ->
        {
            try
            {
                NotezFileUtil.openParentFolderInBrowser(new File(
                    link.getText()));
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
