package de.gui;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.gui.comp.NotezButtonBar;
import de.gui.comp.NotezComponent;
import de.gui.comp.NotezSettingsPane;
import de.gui.comp.NotezTextPane;
import de.util.NotezFileUtil;
import de.util.NotezListenerUtil;
import de.util.log.NotezLog;

public class NotezGui extends Stage
{
	public static final String ICON_LOGO = "include/icons/logo.png";
	public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
	public static final String NOTEZ_LOGO = "include/icons/logo.png";

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

	protected NotezGuiBody body;

	public NotezGui(NotezNote note) throws IOException
	{
		super(StageStyle.UNDECORATED);

		this.note = note;

		FXMLLoader loader = new FXMLLoader(NotezFileUtil.getResourceURL(FXML_PATH));
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
		note.noteFileProperty().addListener((p, o, n) -> {
			if (Objects.nonNull(n))
			{
				fileLink.setText(n.getAbsolutePath());
			}
		});

		fileLink.setOnAction(e -> {
			try
			{
				// FIXME: the given path is invalid
				NotezFileUtil.openParentFolderInBrowser(note.getNoteFile().getParentFile());
			}
			catch(Exception e1)
			{
				try
				{
					NotezDialog.showExceptionDialog(this, "Error while opening folder",
						"Could not open the parent folder!", e1);
				}
				catch(Exception e2)
				{
					NotezLog.error("", e1);
				}
			}
		});

		NotezListenerUtil.setAsResizeNode(resize, this);
	}

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

	public static enum NotezGuiBody
	{
		TEXT,
		SETTINGS;
	}
}
