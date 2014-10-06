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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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

	public NotezController(Stage stage, int idx)
	{
		this(stage, null, idx);
	}

	public NotezController(Stage stage, File note, int idx)
	{
		this.stage = stage;
		this.note = note;
		this.idx = idx;

		notezGroup = new ArrayList<NotezController>();
	}

	@FXML
	private void initialize() throws IOException
	{
		// TODO Remove scrollbars on startup

		loadIcons();
		addDraggableNode(toolBar);
		addResizeCorner(resize);
		addVisibleToolBtns(toolBar);
		setupGestureSource(pickNote);
		setupGestureTarget(toolBar);

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

	/**
	 * Method to load all icons for nodes.
	 */
	private void loadIcons()
	{

		resize.setImage(new Image(
			NotezFileUtil.getResourceStream(ICON_RESIZE)));
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
		// TODO Do you want to save?
		saveNote(note);
		stage.hide();
	}

	/**
	 * Method to open settings.
	 */
	@FXML
	public void openSettings()
	{
		removeAllFromStack(stack);
		stack.getChildren().add(borderPaneSettings);
	}

	@FXML
	public void saveSettings() throws IOException
	{
		// TODO save settings
		// NotezFrame.LOCAL_NOTEZ_FOLDER =
	}

	/**
	 * Method to cancel editing settings.
	 */
	@FXML
	public void cancelSettings()
	{
		// TODO maybe restore defaults?
		removeAllFromStack(stack);
		stack.getChildren().add(txtNote);
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

	/**
	 * Method to save given note to file.
	 * @param note
	 *            = the {@link File} to save note in
	 * @throws Exception
	 *             See: {@link FileWriter}
	 */
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
		sourceImg.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event)
			{
				Dragboard db = sourceImg.startDragAndDrop(TransferMode.MOVE);

				// Copy index of this Notez to clipboard
				ClipboardContent content = new ClipboardContent();
				content.putString(String.valueOf(idx));
				db.setContent(content);

				event.consume();
			}
		});

		sourceImg.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e)
			{
				// Looks better 8-)
				sourceImg.setCursor(Cursor.HAND);
			}
		});
	}

	private void setupGestureTarget(final ToolBar targetBar)
	{
		targetBar.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event)
			{
				// Accept the move
				event.acceptTransferModes(TransferMode.MOVE);
				event.consume();
			}
		});

		targetBar.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event)
			{
				Dragboard db = event.getDragboard();

				// Get dropped notez id from clipbord and pin it to this notez
				if(event.getGestureTarget() instanceof ToolBar)
				{
					NotezController ctrl = NotezFrame.getNotez(Integer.valueOf(db.getString()));
					pinToThisNote(ctrl);
				}

				event.consume();
			}
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

		link.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				NotezFileUtil.openParentFolderInBrowser(new File(link.getText()));
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

					for(int i = 0; i < notezGroup.size(); i++)
					{
						NotezController ctrl = notezGroup.get(i);
						ctrl.initialX = me.getSceneX();
						ctrl.initialY = me.getSceneY()
										- (toolBar.getHeight() * (i + 1));
					}
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

					notezGroup.forEach(ctrl ->
					{
						Scene s = ctrl.getStage().getScene();
						s.getWindow()
							.setX(me.getScreenX() - ctrl.initialX);
						s.getWindow()
							.setY(me.getScreenY() - ctrl.initialY);

						// TODO This is in-performant-shit
						ctrl.getStage().toFront();
					});
				}
			}
		});
	}

	public Stage getStage()
	{
		return stage;
	}
}