/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.gui.comp.NotezSettingsPane;
import de.util.NotezFileUtil;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.NotezSettings;
import de.util.NotezSettings.Setting;
import de.util.notez.NotezData;
import de.util.notez.NotezParsers;
import de.util.share.NotezShareBase;

public class NotezController
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
	public static final String ICON_PICK_NOTE = "include/icons/pn-add-source-copy.gif";
	public static final String ICON_DISSOLVE = "include/icons/link_break.png";

	@FXML
	private Region toolBar;
	@FXML
	private Button btnAdd;
	@FXML
	private Button btnClose;
	@FXML
	private Button btnSettings;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnShare;
	@FXML
	private Button btnDelete;
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
	@FXML
	private Button btnAddUser;
	@FXML
	private Button btnDeleteUser;

	@FXML
	private TableView<NotezRemoteUser> tableRemoteuser;
	@FXML
	private TableColumn<NotezRemoteUser, String> colUsername;
	@FXML
	private TableColumn<NotezRemoteUser, String> colFolder;

	private double initialX;
	private double initialY;

	private double initialXH;
	private double initialYW;

	private Stage stage;

	private File note;
	private ImageView iVUnpinned;
	private ImageView iVPinned;
	private int idx;
	private boolean isGrouped = false;

	private ArrayList<NotezController> notezGroup;

	private Rectangle lastSavedSize;

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
		lastSavedSize = new Rectangle();

		stage.showingProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue)
			{
				if(newValue.booleanValue())
				{
					lastSavedSize.setX(stage.getX());
					lastSavedSize.setY(stage.getY());
					lastSavedSize.setWidth(stage.getWidth());
					lastSavedSize.setHeight(stage.getHeight());

					stage.showingProperty().removeListener(this);
				}
			}
		});
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
		addVisibleNodeHider(hBoxButtom, fileLink);
		addDissolve(pickNote);
		setupGestureSource(pickNote);
		setupGestureTarget(toolBar);

		loadNote(note);
		switchTo(txtNote);
		initSettings(NotezSettings.getAll());

		lastSavedText.set(txtNote.getText());
		noteChanged = lastSavedText.isNotEqualTo(txtNote.textProperty());
		noteChanged = noteChanged.or(
			lastSavedSize.xProperty().isNotEqualTo(stage.xProperty()))
			.or(lastSavedSize.yProperty().isNotEqualTo(stage.yProperty()))
			.or(lastSavedSize.widthProperty().isNotEqualTo(
				stage.widthProperty()))
			.or(lastSavedSize.heightProperty().isNotEqualTo(
				stage.heightProperty()));

		// FORTEST
		DoubleBinding b = null;
		for(Node n : toolBar.getChildrenUnmodifiable())
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

		btnSave.disableProperty().bind(noteChanged.not());
		btnSave.disabledProperty().addListener((bool, old, newOne) ->
		{
			// TODO $TTobi200
			btnSave.setClip(new Rectangle(0d, 0d, btnSave.getWidth()
													* (newOne ? .5 : 1d),
				btnSave.getHeight()));
		});
	}

	private void addDissolve(ImageView pickNote2)
	{
		// isGrouped
		pickNote2.setOnMouseClicked(event ->
		{
			if(isGrouped)
			{
				notezGroup.clear();
				changePickIcon(isGrouped = false);
			}
		});
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

		NotezLoadSplash.availableNotez.forEach(notez ->
		{
		});

		// Init remote user table
		tableRemoteuser.setItems(NotezRemoteSync.getAllUsers());
		colUsername.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
			"username"));
		colFolder.setCellValueFactory(new PropertyValueFactory<NotezRemoteUser, String>(
			"share"));
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
		btnShare.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_SHARE))));
		btnClose.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_CLOSE))));
		btnSettings.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_SETTINGS))));
		btnSave.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_SAVE))));
		btnDelete.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_DELETE))));
		btnDeleteUser.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_DELETE))));
		btnAddUser.setGraphic(new ImageView(new Image(
			NotezFileUtil.getResourceStream(ICON_ADD))));
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
		txtTitle.setText(note.getName().replace(NotezFrame.NOTEZ_FILE_POSFIX,
			""));

		if(NotezFileUtil.fileCanBeLoad(note))
		{
			NotezData data = NotezParsers.parseFile(note);
			Point2D pos = data.getPosition();

			// TODO Set size loaded out of notez-File
			Point2D size = data.getSize();
			stage.setWidth(size.getX());
			stage.setHeight(size.getY());

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

			txtNote.setText(data.getText());
			moveStageAnimatedTo(pos);

			// txtNote.setText(new String(Files.readAllBytes(note.toPath())));

			// File exists and loaded
			return true;
		}
		// File does not exists created new
		return false;
	}

	private void moveStageAnimatedTo(Point2D pos)
	{
		final Duration DUR = Duration.seconds(1d);

		stage.showingProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue)
			{
				if(newValue.booleanValue())
				{
					DoubleProperty x = new SimpleDoubleProperty(stage.getX());
					DoubleProperty y = new SimpleDoubleProperty(stage.getY());

					ChangeListener<Number> xLis = (xx, old, newOne) -> stage.setX(newOne.doubleValue());
					x.addListener(xLis);
					ChangeListener<Number> yLis = (yy, old, newOne) -> stage.setY(newOne.doubleValue());
					y.addListener(yLis);

					new Timeline(new KeyFrame(DUR, new KeyValue(x, pos.getX()),
						new KeyValue(y, pos.getY()))).play();

					stage.showingProperty().removeListener(this);
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
	 * User gets asked if he likes to save the notez.
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
	 * @throws Exception
	 *             See: {@link #saveNote(File)}
	 */
	public void closeNote(boolean askToSave) throws Exception
	{
		if(noteChanged.get())
		{
			switch(NotezDialog.showQuestionDialog(stage, "Save Changes",
				"Do you like to save the changes?"))
			{
				case CANCEL:
				case CLOSE:
					// Do nothing
					return;

				case OK:
				case YES:
					saveNote(genNotezFile(txtTitle.getText()));
				case NO:
					NotezSettings.store(new File(NotezFrame.SETTINGS_FILE));
					break;

			}
		}
		stage.hide();
	}

	private File genNotezFile(String notezName)
	{
		return NotezFileUtil.canBeUsedAsFilename(notezName) ? new File(
			NotezFrame.LOCAL_NOTEZ_FOLDER.replace(".", "") + notezName
							+ NotezFrame.NOTEZ_FILE_POSFIX) : note;
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

		// Pin grouped notez
		notezGroup.forEach(notez ->
		{
			notez.btnPin.selectedProperty().set(pinned);
			notez.pinNote();
		});
	}

	@FXML
	private void shareNote() throws IOException, InterruptedException
	{
		switch(NotezDialog.showQuestionDialog(stage, "Share Notez",
			"Share this Notez with HERTEL-1"))
		{
			default:
			case CANCEL:
			case CLOSE:
			case NO:
				return;

			case OK:
			case YES:
				NotezShareBase.shareNotez(this, note,
					NotezRemoteSync.getUser("HERTEL-1").getShare());
				break;
		}
	}

	private void switchTo(Node node)
	{
		removeAllFromStack(stack);
		stack.getChildren().add(node);
	}

	@FXML
	private void deleteNote() throws Exception
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
	 * @throws Exception
	 *             See: {@link FileWriter}
	 */
	@FXML
	private void saveNote() throws Exception
	{
		saveNote(genNotezFile(txtTitle.getText()));
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

		// try (FileWriter writer = new FileWriter(note))
		// {
		// writer.append(txtNote.getText());
		// writer.flush();
		// writer.close();
		// }
		// catch(Exception e)
		// {
		// throw e;
		// }

		NotezParsers.save(this, note);
		lastSavedText.set(txtNote.getText());
		lastSavedSize.setX(stage.getX());
		lastSavedSize.setY(stage.getY());
		lastSavedSize.setWidth(stage.getWidth());
		lastSavedSize.setHeight(stage.getHeight());
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
		stack.getChildren().clear();
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

	private void setupGestureTarget(final Node targetBar)
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
		// Notez not group with itself
		if(noteToPin != this)
		{
			notezGroup.add(noteToPin);

			noteToPin.getStage().setX(stage.getX());
			noteToPin.getStage().setY(
				stage.getY() + (toolBar.getHeight() * notezGroup.size()));

			changePickIcon(isGrouped = true);
		}
	}

	private void changePickIcon(boolean isGrouped)
	{
		pickNote.setImage(new Image(
			NotezFileUtil.getResourceStream(isGrouped ? ICON_DISSOLVE
							: ICON_PICK_NOTE)));
	}

	private void addFileLink(final Hyperlink link, File note)
	{
		link.setText(note.getAbsolutePath());

		link.setOnAction(e -> NotezFileUtil.openParentFolderInBrowser(new File(
			link.getText())));
	}

	private void addVisibleToolbarNodeHider(Parent toolBar)
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