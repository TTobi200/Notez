/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.util.NotezFileUtil;
import de.util.NotezSettings;
import de.util.NotezSettings.Setting;

public class NotezController
{
    public static final String ICON_ADD = "/include/icons/new_icon.png";
    public static final String ICON_SETTINGS = "/include/icons/icon_local_settings.png";
    public static final String ICON_CLOSE = "/include/icons/icon_close.png";

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

    private double initialX;
    private double initialY;

    private double initialXH;
    private double initialYW;

    private Stage stage;

    private File note;

    public NotezController(Stage stage)
    {
        this(stage, null);
    }

    public NotezController(Stage stage, File note)
    {
        this.stage = stage;
        this.note = note;
    }

    @FXML
    private void initialize() throws IOException
    {
        loadIcons();
        addDraggableNode(toolBar);
        addResizeCorner(resize);
        addVisibleToolBtns(toolBar);

        initialView(loadNote(note));

        initSettings(NotezSettings.getAll());
    }

    private void initSettings(HashMap<String, Setting<Object>> all)
    {
        for(Setting<Object> s : all.values())
        {
            vBoxLocalSet.getChildren().add(new NotezSettingsPane(s));
        }
    }

    private void loadIcons()
    {
        btnAdd.setGraphic(new ImageView(new Image(
            getClass().getResourceAsStream(ICON_ADD))));
        btnClose.setGraphic(new ImageView(new Image(
            getClass().getResourceAsStream(ICON_CLOSE))));
        btnSettings.setGraphic(new ImageView(new Image(
            getClass().getResourceAsStream(ICON_SETTINGS))));
    }

    private boolean loadNote(File note) throws IOException
    {
        if(NotezFileUtil.fileCanBeLoad(note))
        {
            txtNote.setText(new String(Files.readAllBytes(note.toPath())));

            // File exists and loaded
            return true;
        }
        addFileLink(fileLink, note);

        // File does not exists created new
        return false;
    }

    private void initialView(boolean initView)
    {
        removeAllFromStack(stack);

        if(!initView)
        {
            openSettings();
        }
        else
        {
            stack.getChildren().add(txtNote);
        }
    }

    @FXML
    public void addNewNote() throws IOException
    {
        NotezFrame.createNotezFrame();
    }

    @FXML
    public void closeNote() throws Exception
    {
        // TODO Do you want to save?
        saveNote(note);
        stage.hide();
    }

    private void saveNote(File note) throws Exception
    {
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
    }

    @FXML
    public void openSettings()
    {
        removeAllFromStack(stack);
        stack.getChildren().add(borderPaneSettings);
    }

    @FXML
    public void saveSettings() throws IOException
    {
        // NotezFrame.LOCAL_NOTEZ_FOLDER =
    }

    @FXML
    public void cancelSettings()
    {
        removeAllFromStack(stack);
        stack.getChildren().add(txtNote);
    }

    private void removeAllFromStack(StackPane stack)
    {
        stack.getChildren().removeAll(stack.getChildren());
    }

    private void addFileLink(final Hyperlink link, File note)
    {
        link.setText(note.getAbsolutePath());

        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e)
            {
                System.out.println(link.getText());
            }
        });
    }

    private void addVisibleToolBtns(final Node node)
    {
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                displayChilds(toolBar, true);
            }
        });

        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                displayChilds(toolBar, false);
            }
        });
    }

    private void displayChilds(ToolBar toolBar, boolean visible)
    {
        toolBar.getItems().forEach(itm -> itm.setVisible(visible));
    }

    private void addResizeCorner(final Node node)
    {
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                if(me.getButton() != MouseButton.MIDDLE)
                {
                    initialXH = me.getSceneX();
                    initialYW = me.getSceneY();
                }
            }
        });

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                if(me.getButton() != MouseButton.MIDDLE)
                {
                    stage.setWidth(me.getSceneX() + me.getSceneX() - initialYW);
                    stage.setHeight(me.getSceneY() + me.getSceneY() - initialXH);
                }
            }
        });
    }

    private void addDraggableNode(final Node node)
    {
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                if(me.getButton() != MouseButton.MIDDLE)
                {
                    initialX = me.getSceneX();
                    initialY = me.getSceneY();
                }
            }
        });

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me)
            {
                if(me.getButton() != MouseButton.MIDDLE)
                {
                    node.getScene()
                        .getWindow()
                        .setX(me.getScreenX() - initialX);
                    node.getScene()
                        .getWindow()
                        .setY(me.getScreenY() - initialY);
                }
            }
        });
    }
}