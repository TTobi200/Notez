/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import de.gui.comp.NotezSettingsPane;
import de.util.NotezFileUtil;
import de.util.NotezObservablesUtil;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.NotezSettings;
import de.util.NotezSettings.Setting;
import de.util.notez.NotezData;
import de.util.notez.NotezParsers;

public class NotezControllerListeners
{
	protected NotezController c;
	
	protected double curX;
	protected double curY;
	
	public NotezControllerListeners(NotezController controller)
	{
		this.c = controller;
	}
	
	protected void initialize()
	{
//		loadIcons();
		
		// TODO Remove scrollbars on startup
        addDraggableNode(c.toolBar);
        addDraggableNode(c.txtTitle); // TODO now text selection by mouse is
                                    // impossible :-(
        addResizeCorner(c.resize);
        addVisibleToolbarNodeHider(c.toolBar);
        addVisibleNodeHider(c.hBoxButtom, c.fileLink);
        addDissolve(c.pickNote);
        setupGestureSource(c.pickNote);
        setupGestureTarget(c.toolBar);

        try
		{
			loadNote(c.note);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        switchTo(c.borderPaneNotez);
        initSettings(NotezSettings.getAll());

        initPagination();
        initNoteChanged();

        // FORTEST
        DoubleBinding b = null;
        for(Node n : c.toolBar.getChildrenUnmodifiable())
        {
            if(n instanceof Region)
            {
                Region r = (Region)n;
                if(b == null)
                {
                    b = Bindings.add(0d, r.widthProperty());
                }
                else
                {
                    b = b.add(r.widthProperty());
                }
            }
        }
        c.toolBar.prefWidthProperty().bind(b);
        c.stage.minWidthProperty().bind(c.toolBar.prefWidthProperty());

        c.btnSave.disableProperty().bind(c.noteChanged.not());
        c.btnSave.disabledProperty().addListener((bool, old, newOne) ->
        {
            // TODO $TTobi200
            c.btnSave.setClip(new Rectangle(0d, 0d, c.btnSave.getWidth()
                                                  * (newOne ? .5 : 1d),
                c.btnSave.getHeight()));
        });

        // FORTEST set save Accelerator
        c.stage.showingProperty().addListener(l -> {
            if(c.stage.isShowing())
			{
				setAccelerators();
			}
        });

	}
	
//	/**
//     * Method to load all icons for nodes.
//     */
//    protected void loadIcons()
//    {
//        c.resize.setImage(new Image(NotezFileUtil.getResourceStream(c.ICON_RESIZE)));
//        c.pickNote.setImage(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_PICK_NOTE)));
//        c.btnAdd.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_ADD))));
//        c.btnShare.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_SHARE))));
//        c.btnClose.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_CLOSE))));
//        c.btnSettings.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_SETTINGS))));
//        c.btnSave.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_SAVE))));
//        c.btnDelete.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_DELETE))));
//        c.btnDeleteUser.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_DELETE))));
//        c.btnAddUser.setGraphic(new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_ADD))));
//        c.btnPin.setGraphic(c.iVUnpinned = new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_UNPINNED))));
//        c.iVPinned = new ImageView(new Image(
//            NotezFileUtil.getResourceStream(c.ICON_PINNED)));
//
//        c.stage.getIcons().add(
//            new Image(NotezFileUtil.getResourceStream(c.ICON_LOGO)));
//    }

    protected void addDraggableNode(final Node node)
    {
    	// TODO $Dauerdaddlah outsource to NotezGuiUtil
        node.setOnMousePressed(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
                c.initialX = me.getSceneX();
                c.initialY = me.getSceneY();

                for(int i = 0; i < c.notezGroup.size(); i++)
                {
                    NotezController ctrl = c.notezGroup.get(i);
                    ctrl.initialX = me.getSceneX();
                    ctrl.initialY = me.getSceneY()
                                    - (c.toolBar.getHeight() * (i + 1));
                }
            }
        });

        node.setOnMouseDragged(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
            	c.stage.setX(me.getScreenX() - c.initialX);
            	c.stage.setY(me.getScreenY() - c.initialY);

                c.notezGroup.forEach(ctrl ->
                {
                    Scene s = ctrl.getStage().getScene();
                    s.getWindow().setX(me.getScreenX() - ctrl.initialX);
                    s.getWindow().setY(me.getScreenY() - ctrl.initialY);

                    // TODO This is in-performant-shit
                    ctrl.getStage().toFront();
                });
            }
        });
    }
    
    protected void addResizeCorner(final Node node)
    {
    	node.setOnMousePressed(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
			{
				curX = event.getSceneX();
				curY = event.getSceneY();
			}
		});

		node.setOnMouseDragged(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
			{
				double tempH = event.getSceneX() - curY;
				double tempW = event.getSceneY() - curX;
				double height = event.getSceneY() + event.getSceneY() - curX;
				double width = event.getSceneX() + event.getSceneX() - curY;

				if (height < c.stage.getMinHeight())
				{
					height = c.stage.getMinHeight();
				}
				if (width < c.stage.getMinWidth())
				{
					width = c.stage.getMinWidth();
				}

				c.stage.setWidth(width);
				c.stage.setHeight(height);
				curY += tempH;
				curX += tempW;
			}
		});

		node.setCursor(Cursor.SE_RESIZE);
    }

    protected void addVisibleToolbarNodeHider(Parent toolBar)
    {
        Node[] itms = toolBar.getChildrenUnmodifiable()
            .stream()
            .toArray(Node[]::new);

        // Add item visibility
        addVisibleNodeHider(toolBar, itms);

        for(Object o : itms)
        {
            if(o instanceof ButtonBase)
            {
                Tooltip tT = ((ButtonBase)o).getTooltip();
                if(tT != null)
                {
                    // Add Tooltip visibility
                    addVisibleNodeHider(tT, itms);
                }
            }
        }

        addVisibleNodeHider(toolBar,
            toolBar.getChildrenUnmodifiable().stream().toArray(Node[]::new));
    }

    protected void addVisibleNodeHider(Tooltip tT, Node... itms)
    {
        tT.setOnShowing(arg0 -> displayChilds(itms, true));

        tT.setOnHiding(arg0 -> displayChilds(itms, true));
    }

    protected void addVisibleNodeHider(final Node node, Node... itms)
    {
        node.setOnMouseEntered(me -> displayChilds(itms, true));

        node.setOnMouseExited(me -> displayChilds(itms, false));
    }
    
    protected void displayChilds(Node[] nodes, boolean visible)
    {
        Timeline line = new Timeline();
        final Duration DUR = Duration.seconds(1d);
        for(Node n : nodes)
        {
            line.getKeyFrames().add(
                new KeyFrame(DUR, new KeyValue(n.opacityProperty(),
                    visible ? 1d : 0d)));
        }
        line.play();
        // objects.forEach(itm -> itm.setVisible(visible));
    }

    protected void addDissolve(ImageView pickNote2)
    {
        // isGrouped
        pickNote2.setOnMouseClicked(event ->
        {
            if(c.isGrouped)
            {
                c.notezGroup.clear();
                changePickIcon(c.isGrouped = false);
            }
        });
    }
    
    /**
     * Method to add all settings for Notez.
     * @param all
     *            = new {@link HashMap} with {@link Setting}
     */
    protected void initSettings(Map<String, Setting<Object>> all)
    {
        for(Setting<Object> s : all.values())
        {
            c.vBoxLocalSet.getChildren().add(new NotezSettingsPane(s));
        }

        NotezLoadSplash.availableNotez.forEach(notez ->
        {
        });

        // Init remote user table
        c.tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());
        c.colUsername.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
            "username"));
        c.colFolder.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
            "share"));
    }

    protected void initNoteChanged()
    {
    	// TODO set notechanged correctly

    	c.noteChanged = c.data.textChangedProperty();
    }

    protected void initPagination()
    {
    	c.data = new NotezPagedData();

    	c.data.curTextProperty().bind(c.txtNote.textProperty());
        c.data.saveText();
        // TODO $Dauerdaddlah
//        noteChanged = lastSavedText.isNotEqualTo(txtNote.textProperty());
//        noteChanged = noteChanged.or(
//            lastSavedSize.xProperty().isNotEqualTo(stage.xProperty()))
//            .or(lastSavedSize.yProperty().isNotEqualTo(stage.yProperty()))
//            .or(lastSavedSize.widthProperty().isNotEqualTo(
//                stage.widthProperty()))
//            .or(lastSavedSize.heightProperty().isNotEqualTo(
//                stage.heightProperty()));
        c.lblPage.textProperty().bind(Bindings.concat(c.data.curIndexProperty().add(1), " / ", NotezObservablesUtil.sizePropertyForList(c.data.getPages())));
        c.btnPrevPage.disableProperty().bind(c.data.curIndexProperty().isEqualTo(0));

        c.data.curDataProperty().addListener(
        	(c, o, n) ->
        	{
        		o.curTextProperty().unbind();
        		this.c.txtNote.setText(n.curTextProperty().get());
        		n.curTextProperty().bind(this.c.txtNote.textProperty());
        	});
    }
    
    protected void changePickIcon(boolean isGrouped)
    {
        c.pickNote.setImage(new Image(
            NotezFileUtil.getResourceStream(isGrouped ? NotezController.ICON_DISSOLVE
                            : NotezController.ICON_PICK_NOTE)));
    }

    protected void setupGestureSource(final ImageView sourceImg)
    {
        sourceImg.setOnDragDetected(event ->
        {
            Dragboard db = sourceImg.startDragAndDrop(TransferMode.MOVE);

            // Copy index of this Notez to clipboard
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(c.idx));
            db.setContent(content);

            event.consume();
        });

        sourceImg.setOnMouseEntered(e -> sourceImg.setCursor(Cursor.HAND));
    }

    protected void setupGestureTarget(final Node targetBar)
    {
        targetBar.setOnDragOver(event ->
        {
            // Accept the move
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        targetBar.setOnDragDropped(event ->
        {
            Dragboard db = event.getDragboard();

            // Get dropped notez id from clipbord and pin it to this notez
            if(event.getGestureTarget() instanceof ToolBar)
            {
                NotezController ctrl = NotezFrame.getNotez(Integer.valueOf(db.getString()));
                c.pinToThisNote(ctrl);
            }

            event.consume();
        });
    }

    /**
     * Method to load note from file.
     * @param note
     *            = the {@link File} to load note from
     * @return true if file could be loaded
     * @throws IOException
     *             See: {@link Files#readAllBytes(java.nio.file.Path)}
     */
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

            // TODO Set size loaded out of notez-File
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

            c.txtNote.setText(data.getText());
            moveStageAnimatedTo(pos);

            // txtNote.setText(new String(Files.readAllBytes(note.toPath())));

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

        addAcceleratorToScene(s, KeyCode.S, ()
            -> {
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
            new KeyCodeCombination(key, KeyCombination.SHORTCUT_DOWN),
            run);
    }

    
    
    
    protected void moveStageAnimatedTo(Point2D pos)
    {
        final Duration DUR = Duration.seconds(1d);

        c.stage.showingProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                            Boolean oldValue, Boolean newValue)
            {
                if(newValue.booleanValue())
                {
                    DoubleProperty x = new SimpleDoubleProperty(c.stage.getX());
                    DoubleProperty y = new SimpleDoubleProperty(c.stage.getY());

                    ChangeListener<Number> xLis = (xx, old, newOne) -> c.stage.setX(newOne.doubleValue());
                    x.addListener(xLis);
                    ChangeListener<Number> yLis = (yy, old, newOne) -> c.stage.setY(newOne.doubleValue());
                    y.addListener(yLis);

                    new Timeline(new KeyFrame(DUR, new KeyValue(x, pos.getX()),
                        new KeyValue(y, pos.getY()))).play();

                    c.stage.showingProperty().removeListener(this);
                }
            }
        });

        // stage.setX(pos.getX());
        // stage.setY(pos.getY());

        // TODO cannot animate stage directly find other solution
        // Path path = new Path();
        // path.getElements().add(new MoveTo(pos.getX(), pos.getY()));
        // PathTransition pathTransition = new PathTransition();
        // pathTransition.setDuration(Duration.millis(4000));
        // pathTransition.setPath(path);
        // pathTransition.setNode(root);
        // pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        // pathTransition.setCycleCount(Timeline.INDEFINITE);
        // pathTransition.setAutoReverse(true);
        // pathTransition.play();
    }

    protected void addFileLink(final Hyperlink link, File note)
    {
        link.setText(note.getAbsolutePath());

        link.setOnAction(e -> NotezFileUtil.openParentFolderInBrowser(new File(
            link.getText())));
    }
}
