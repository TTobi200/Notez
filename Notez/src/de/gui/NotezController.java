/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.gui.comp.NotezSettingsPane;
import de.util.NotezFileUtil;
import de.util.NotezSettings;
import de.util.NotezSettings.Setting;

public class NotezController
{
    public static final String ICON_LOGO = "include/icons/logo.png";
    public static final String ICON_ADD = "include/icons/new_icon.png";
    public static final String ICON_SETTINGS = "include/icons/icon_local_settings.png";
    public static final String ICON_CLOSE = "include/icons/icon_close.png";
    public static final String ICON_UNPINNED = "include/icons/pin.png";
    public static final String ICON_PINNED = "include/icons/pin_2.png";
    public static final String ICON_RESIZE = "include/icons/resize.gif";
    public static final String ICON_PICK_NOTE = "include/icons/pn-add-source-copy.gif";

    @FXML
    private ToolBar toolBar;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSettings;
    @FXML
    private TextArea txtNote;
    @FXML
    private HBox hBoxButtom;
    @FXML
    private Hyperlink fileLink;
    @FXML
    private ScrollPane scrollTxt;
    @FXML
    private ImageView resize;
    @FXML
    private TabPane tabSettings;
    @FXML
    private Tab tabLocal;
    @FXML
    private StackPane stack;
    @FXML
    private BorderPane borderPaneSettings;
    @FXML
    private VBox vBoxLocalSet;
    @FXML
    private ToggleButton btnPin;
    @FXML
    private ImageView pickNote;
    @FXML
    private TextField txtTitle;

    private double initialX;
    private double initialY;

    private double initialXH;
    private double initialYW;

    private Stage stage;

    private File note;
    private ImageView iVUnpinned;
    private ImageView iVPinned;
    private int idx;

    private ArrayList<NotezController> notezGroup;

    /**
     * boolean binding indicating whether the text in {@link #txtNote} differs
     * from the saved on
     */
    private BooleanBinding noteChanged;
    /** The text currently written in this notes' file */
    private StringProperty lastSavedText;

    public NotezController(Stage stage, int idx)
    {
        this(stage, null, idx);
    }

    public NotezController(Stage stage, File note, int idx)
    {
        this.stage = stage;
        this.note = note;
        this.idx = idx;

        notezGroup = new ArrayList<>();
        lastSavedText = new SimpleStringProperty("");
    }

    @FXML
    private void initialize() throws IOException
    {
        // TODO Remove scrollbars on startup

        loadIcons();
        addDraggableNode(toolBar);
        addDraggableNode(txtTitle); // TODO now text selection by mouse is
                                    // impossible :-(
        addResizeCorner(resize);
        addVisibleToolbarNodeHider(toolBar);
        // addVisibleNodeHider(toolBar,
        // toolBar.getItems().toArray());
        addVisibleNodeHider(hBoxButtom, fileLink);
        setupGestureSource(pickNote);
        setupGestureTarget(toolBar);

        // Show settings on first start?
        // initialView(loadNote(note));
        loadNote(note);
        switchTo(txtNote);
        initSettings(NotezSettings.getAll());

        lastSavedText.set(txtNote.getText());
        noteChanged = lastSavedText.isNotEqualTo(txtNote.textProperty());

        // FORTEST
        DoubleBinding b = null;
        for(Node n : toolBar.getItems())
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
        toolBar.prefWidthProperty().bind(b);
        stage.minWidthProperty().bind(toolBar.prefWidthProperty());
    }

    /**
     * Method to add all settings for Notez.
     * @param all
     *            = new {@link HashMap} with {@link Setting}
     */
    private void initSettings(Map<String, Setting<Object>> all)
    {
        for(Setting<Object> s : all.values())
        {
            vBoxLocalSet.getChildren().add(new NotezSettingsPane(s));
        }

        addLocalNotezAvail();
    }

    private void addLocalNotezAvail()
    {
        NotezLoadSplash.availableNotez.forEach(notez ->
        {
        });
        // vBoxLocalSet.getChildren().add(
        // new NotezSettingsPane(NotezSettingsType.COMBO), new Setting<>(name,
        // value));
    }

    /**
     * Method to load all icons for nodes.
     */
    private void loadIcons()
    {
        resize.setImage(new Image(NotezFileUtil.getResourceStream(ICON_RESIZE)));
        pickNote.setImage(new Image(
            NotezFileUtil.getResourceStream(ICON_PICK_NOTE)));
        btnAdd.setGraphic(new ImageView(new Image(
            NotezFileUtil.getResourceStream(ICON_ADD))));
        btnClose.setGraphic(new ImageView(new Image(
            NotezFileUtil.getResourceStream(ICON_CLOSE))));
        btnSettings.setGraphic(new ImageView(new Image(
            NotezFileUtil.getResourceStream(ICON_SETTINGS))));
        btnPin.setGraphic(iVUnpinned = new ImageView(new Image(
            NotezFileUtil.getResourceStream(ICON_UNPINNED))));
        iVPinned = new ImageView(new Image(
            NotezFileUtil.getResourceStream(ICON_PINNED)));

        stage.getIcons().add(
            new Image(NotezFileUtil.getResourceStream(ICON_LOGO)));
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
        addFileLink(fileLink, note);
        getStage().titleProperty().bind(txtTitle.textProperty());
        txtTitle.setText(note.getName().replace(
            NotezFrame.NOTEZ_FILE_POSFIX, ""));

        if(NotezFileUtil.fileCanBeLoad(note))
        {
            txtNote.setText(new String(Files.readAllBytes(note.toPath())));

            // File exists and loaded
            return true;
        }
        // File does not exists created new
        return false;
    }

    /**
     * Method to show initial view or not.
     * @param initView
     *            = has to show settings?
     */
    @SuppressWarnings("unused")
    private void initialView(boolean initView)
    {
        switchTo(initView ? borderPaneSettings : txtNote);
    }

    /**
     * Method to add a new notez.
     * @throws IOException
     *             See: {@link NotezFrame#createNotezFrame()}
     */
    @FXML
    public void addNewNote() throws IOException
    {
        NotezFrame.createNotezFrame();
    }

    /**
     * Method to close this notez.
     * @throws Exception
     *             See: {@link #saveNote(File)}
     */
    @FXML
    public void closeNote() throws Exception
    {
        if(noteChanged.get())
        {
            switch(NotezDialog.showQuestionDialog(stage, "Save Note",
                "Do you like to save the Note?"))
            {
                case CANCEL:
                case CLOSE:
                    // Do nothing
                    return;

                case OK:
                case YES:
                    String notezName = txtTitle.getText();
                    saveNote(NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
                        note.getParent().replace(".", "") + notezName
                                        + NotezFrame.NOTEZ_FILE_POSFIX)
                                    : note);
                case NO:
                    NotezSettings.store(new File(NotezFrame.SETTINGS_FILE));
                    break;

            }
        }
        stage.hide();
    }

    /**
     * Method to open settings.
     */
    @FXML
    public void openSettings()
    {
        switchTo(borderPaneSettings);
    }

    @FXML
    public void saveSettings() throws IOException
    {
        vBoxLocalSet.getChildren().forEach(child ->
        {
            if(child instanceof NotezSettingsPane)
            {
                ((NotezSettingsPane)child).saveSetting();
            }
        });

        // TODO only switch if valid?
        switchTo(txtNote);
    }

    /**
     * Method to cancel editing settings.
     */
    @FXML
    public void cancelSettings()
    {
        // TODO maybe restore defaults?
        switchTo(txtNote);
    }

    /**
     * Method to pin note. Set stage always on top and changes icon.
     */
    @FXML
    public void pinNote()
    {
        boolean pinned = btnPin.isSelected();

        btnPin.setGraphic(pinned ? iVPinned : iVUnpinned);
        stage.setAlwaysOnTop(pinned);
    }

    private void switchTo(Node node)
    {
        removeAllFromStack(stack);
        stack.getChildren().add(node);
    }

    /**
     * Method to save given note to file.
     * @param note
     *            = the {@link File} to save note in
     * @throws Exception
     *             See: {@link FileWriter}
     */
    private void saveNote(File note) throws Exception
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

        try (FileWriter writer = new FileWriter(note))
        {
            writer.append(txtNote.getText());
            writer.flush();
            writer.close();
        }
        catch(Exception e)
        {
            throw e;
        }

        lastSavedText.set(txtNote.getText());
    }

    public void openUrl(URI uri) throws IOException, URISyntaxException
    {
        if(Desktop.isDesktopSupported())
        {
            Desktop desktop = Desktop.getDesktop();

            if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
            {
                desktop.browse(uri);
            }
        }
    }

    private void removeAllFromStack(StackPane stack)
    {
        stack.getChildren().removeAll(stack.getChildren());
    }

    private void setupGestureSource(final ImageView sourceImg)
    {
        sourceImg.setOnDragDetected(event ->
        {
            Dragboard db = sourceImg.startDragAndDrop(TransferMode.MOVE);

            // Copy index of this Notez to clipboard
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(idx));
            db.setContent(content);

            event.consume();
        });

        sourceImg.setOnMouseEntered(e -> sourceImg.setCursor(Cursor.HAND));
    }

    private void setupGestureTarget(final ToolBar targetBar)
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
                pinToThisNote(ctrl);
            }

            event.consume();
        });
    }

    /**
     * Method to pin given notez to this notez.
     * @param noteToPin
     *            = the {@link NotezController} (notez) to pin
     */
    protected void pinToThisNote(NotezController noteToPin)
    {
        notezGroup.add(noteToPin);

        noteToPin.getStage().setX(stage.getX());
        noteToPin.getStage().setY(
            stage.getY() + (toolBar.getHeight() * notezGroup.size()));
    }

    private void addFileLink(final Hyperlink link, File note)
    {
        link.setText(note.getAbsolutePath());

        link.setOnAction(e -> NotezFileUtil.openParentFolderInBrowser(new File(
            link.getText())));
    }

    private void addVisibleToolbarNodeHider(ToolBar toolBar)
    {
        Node[] itms = toolBar.getItems().stream().toArray(Node[]::new);

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
            toolBar.getItems().stream().toArray(Node[]::new));
    }

    private void addVisibleNodeHider(Tooltip tT, Node... itms)
    {
        tT.setOnShowing(arg0 -> displayChilds(itms, true));

        tT.setOnHiding(arg0 -> displayChilds(itms, true));
    }

    private void addVisibleNodeHider(final Node node, Node... itms)
    {
        node.setOnMouseEntered(me -> displayChilds(itms, true));

        node.setOnMouseExited(me -> displayChilds(itms, false));
    }

    private void displayChilds(Node[] nodes, boolean visible)
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

    private void addResizeCorner(final Node node)
    {
        node.setOnMousePressed(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
                initialXH = me.getSceneX();
                initialYW = me.getSceneY();
            }
        });

        node.setOnMouseDragged(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
                double tempH = me.getSceneX() - initialYW;
                double tempW = me.getSceneY() - initialXH;
                double height = me.getSceneY() + me.getSceneY() - initialXH;
                double width = me.getSceneX() + me.getSceneX() - initialYW;

                if(height < stage.getMinHeight())
                {
                    height = stage.getMinHeight();
                }
                if(width < stage.getMinWidth())
                {
                    width = stage.getMinWidth();
                }

                stage.setWidth(width);
                stage.setHeight(height);
                initialYW += tempH;
                initialXH += tempW;
            }
        });
    }

    private void addDraggableNode(final Node node)
    {
        node.setOnMousePressed(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
                initialX = me.getSceneX();
                initialY = me.getSceneY();

                for(int i = 0; i < notezGroup.size(); i++)
                {
                    NotezController ctrl = notezGroup.get(i);
                    ctrl.initialX = me.getSceneX();
                    ctrl.initialY = me.getSceneY()
                                    - (toolBar.getHeight() * (i + 1));
                }
            }
        });

        node.setOnMouseDragged(me ->
        {
            if(me.getButton() != MouseButton.MIDDLE)
            {
                node.getScene().getWindow().setX(me.getScreenX() - initialX);
                node.getScene().getWindow().setY(me.getScreenY() - initialY);

                notezGroup.forEach(ctrl ->
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

    public Stage getStage()
    {
        return stage;
    }
    
    public File getNoteFile()
	{
		return note;
	}
    
    public String getNoteText()
    {
    	return txtNote.getText();
    }
}