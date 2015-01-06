package de.gui;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import de.gui.comp.NotezButtonBar;
import de.gui.comp.NotezComponent;
import de.gui.comp.NotezSettingsPane;
import de.gui.comp.NotezTextPane;
import de.notez.NotezNote;
import de.notez.NotezNotes;
import de.util.NotezFileUtil;
import de.util.NotezListenerUtil;
import de.util.log.NotezLog;

/**
 * The graphical interpretation of a {@link NotezNote}
 * 
 * @author ddd
 */
public class NotezGui extends Stage
{
	public static final String ICON_LOGO = "include/icons/logo.png";
	public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
	public static final String NOTEZ_LOGO = "include/icons/logo.png";

	/** The note represented by this gui */
	protected NotezNote note;

	@FXML
	private NotezButtonBar btns;
	@FXML
	private NotezSettingsPane settings;
	@FXML
	private NotezTextPane text;

	@FXML
	private ImageView resize;
	@FXML
	private Hyperlink fileLink;

	@FXML
	private StackPane stack;

	/** the body currently showing */
	protected NotezGuiBody body;

	/**
	 * Create a new gui representing the given note
	 * 
	 * @param note the note to be represented by this gui
	 * @throws IOException
	 */
	public NotezGui(NotezNote note) throws IOException
	{
		super(StageStyle.UNDECORATED);

		this.note = note;

		FXMLLoader loader = new FXMLLoader(
			NotezFileUtil.getResourceURL(FXML_PATH));
		loader.setController(this);
		BorderPane root = loader.load();
		Scene scene = new Scene(root);

		// XXX Add this to gain drop shadow (1/2)
		// Group g = new Group();
		// Scene scene = new Scene(g);
		// scene.setFill(null);
		// root.setPadding(new Insets(10, 10, 10, 10));
		// root.setEffect(new DropShadow());

		setScene(scene);
		// Fixed set height/width needed for dialogs (relative to)
		initStyle(StageStyle.TRANSPARENT);
		getIcons().add(new Image(NotezFileUtil.getResourceStream(NOTEZ_LOGO)));

		// XXX Add this to gain drop shadow (2/2)
		// g.getChildren().add(root);

		Collection<NotezComponent> comps = new HashSet<>();
		comps.add(btns);
		comps.add(settings);
		comps.add(text);

		for(NotezComponent comp : comps)
		{
			comp.setGui(this);
			comp.setListener();
		}

		switchToBody(NotezGuiBody.TEXT);

		setListeners();
	}

	private void setListeners()
	{
		fileLink.setText(note.getNoteFile().getAbsolutePath());
		getNote().noteFileProperty().addListener((p, o, n) ->
		{
			if(Objects.nonNull(n))
			{
				fileLink.setText(n.getAbsolutePath());
			}
		});

		fileLink.setOnAction(e ->
		{
			try
			{
				// FIXME: the given path is invalid
				NotezFileUtil.openParentFolderInBrowser(note.getNoteFile()
					.getParentFile());
			}
			catch(Exception e1)
			{
				try
				{
					NotezDialog.showExceptionDialog(this,
						"Error while opening folder",
						"Could not open the parent folder!", e1);
				}
				catch(Exception e2)
				{
					NotezLog.error("", e1);
				}
			}
		});

		NotezListenerUtil.setAsResizeNode(resize, this);

		DoubleBinding b = null;
		for(Node n : btns.getChildrenUnmodifiable())
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
		btns.prefWidthProperty().bind(b);

		minWidthProperty().bind(btns.prefWidthProperty());

		doOnShowing(() ->
		{
			final Duration DUR = Duration.seconds(1d);

	        DoubleProperty x = new SimpleDoubleProperty(getX());
	        DoubleProperty y = new SimpleDoubleProperty(getY());

	        ChangeListener<Number> xLis = (xx, old, newOne) -> setX(newOne.doubleValue());
	        x.addListener(xLis);
	        ChangeListener<Number> yLis = (yy, old, newOne) -> setY(newOne.doubleValue());
	        y.addListener(yLis);

	        new Timeline(new KeyFrame(DUR, new KeyValue(x, getNote().getData().getStageData().getStageX()),
	            new KeyValue(y, getNote().getData().getStageData().getStageY())))
	            .play();
			
//			setX(getNote().getData().getStageData().getStageX());
//			setY(getNote().getData().getStageData().getStageY());
//			setWidth(getNote().getData().getStageData().getStageWidth());
//			setHeight(getNote().getData().getStageData().getStageHeight());
			getNote().getData().getStageData().bind(this);
			
			setAccelerators();
		});
		
		NotezListenerUtil.addVisibleNodeHider(fileLink, fileLink);
	}

	protected void setAccelerators()
	{
		Scene s = getScene();

		addAcceleratorToScene(s, KeyCode.S, () -> getNote().save());
		addAcceleratorToScene(s, KeyCode.N, () -> NotezNotes.creNote());
	}

	protected void addAcceleratorToScene(Scene s, KeyCode key, Runnable run)
	{
		s.getAccelerators().put(
			new KeyCodeCombination(key, KeyCombination.SHORTCUT_DOWN), run);
	}

	/**
	 * @param body The body to be shown in this gui
	 */
	public void switchToBody(NotezGuiBody body)
	{
		if(this.body != body)
		{
			switch(body)
			{
				case SETTINGS:
				{
					stack.getChildren().setAll(settings);
					settings.updateSettings();
					break;
				}
				case TEXT:
				{
					stack.getChildren().setAll(text);
				}
			}

			this.body = body;
		}
	}

	/**
	 * @param run The operation to only be done if this gui is showing
	 */
	public void doOnShowing(Runnable run)
	{
		if(isShowing())
		{
			run.run();
		}
		else
		{
			showingProperty().addListener((p, o, n) ->
			{
				if(n.booleanValue())
				{
					run.run();
				}
			});
		}
	}

	public NotezGuiBody getCurrentBody()
	{
		return body;
	}

	public NotezNote getNote()
	{
		return note;
	}

	public NotezButtonBar getButtonBar()
	{
		return btns;
	}

	public NotezTextPane getTextPane()
	{
		return text;
	}

	public NotezSettingsPane getSettingsPane()
	{
		return settings;
	}

	/**
	 * enum for representing the different bodies a gui can have.
	 * 
	 * @author ddd
	 */
	public static enum NotezGuiBody
	{
		/** The textual representation of the pageddata of the note */
		TEXT,
		/** A pane for manipulating the settings of the gui. */
		SETTINGS;
	}
}
