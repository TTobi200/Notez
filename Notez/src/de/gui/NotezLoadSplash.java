package de.gui;

import static de.notez.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.notez.NotezProperties.get;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import de.notez.NotezNote;
import de.notez.NotezNotes;
import de.util.NotezFileUtil;

public class NotezLoadSplash extends Application
{
	private Pane splashLayout;
	private ProgressBar loadProgress;
	private Label progressText;

	private static final int SPLASH_WIDTH = 300;
	protected static final long SLEEP_TIME = 500;

	public static final String DEF_LOCAL_NOTEZ_FOLDER = ".";

	@Override
	public void init()
	{
		ImageView splash = new ImageView(new Image(
			NotezFileUtil.getResourceStream(NotezGui.ICON_LOGO)));
		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		progressText = new Label("Notez starting...");
		splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash, loadProgress, progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(final Stage initStage) throws Exception
	{
		File localNotezFolder = new File(get(NOTEZ_WORK_FOLDER,
			DEF_LOCAL_NOTEZ_FOLDER));

		if(localNotezFolder.exists())
		{
			loadAllNotez(localNotezFolder);
		}
		else
		{
			NotezDialog.showWarningDialog(initStage, "Warning - Notez-Folder",
				"Cannot find Notez-Folder! " + "(" + localNotezFolder + ")");

			localNotezFolder = new File(DEF_LOCAL_NOTEZ_FOLDER);
		}

		// No notes found? create default new one
		if(NotezNote.notezList().isEmpty())
		{
			// switch to settings (init)
			NotezNotes.creNote().show();
		}

		showSplash(initStage, new Task<ObservableList<String>>()
		{

			@Override
			protected ObservableList<String> call() throws Exception
			{
				ObservableList<String> foundNotez = FXCollections.<String> observableArrayList();

				updateMessage("Loading Notez...");
				List<NotezNote> availableNotez = NotezNote.notezList();
				for(int i = 0; i < availableNotez.size(); i++)
				{
					Thread.sleep(SLEEP_TIME);
					updateProgress(i + 1, availableNotez.size());
					NotezNote nextNote = availableNotez.get(i);
					foundNotez.add(nextNote.toString());
					updateMessage("Notez " + nextNote.getTitle()
								  + " loaded.");

					Platform.runLater(() ->
					{
						nextNote.show();
					});
				}
				Thread.sleep(SLEEP_TIME);

				return foundNotez;
			}

		});
	}

	private void showSplash(final Stage initStage,
					Task<ObservableList<String>> task)
	{
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());
		task.stateProperty().addListener((p, o, n) ->
		{
			if(n == Worker.State.SUCCEEDED)
			{
				loadProgress.progressProperty().unbind();
				loadProgress.setProgress(1);
				initStage.toFront();
				// OMG i like that shit :-)
				FadeTransition fadeSplash = new FadeTransition(
					Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(arg0 -> initStage.hide());
				fadeSplash.play();
			}
		});
		initStage.initStyle(StageStyle.UNDECORATED);
		initStage.setScene(new Scene(splashLayout));
		initStage.setAlwaysOnTop(true);
		initStage.show();

		new Thread(task).start();
	}

	public static int loadAllNotez(File notezFolder)
		throws IOException
	{
		int foundNotes = 0;
		File[] notez = notezFolder.listFiles();
		if(notez != null)
		{
			for(File f : notez)
			{
				if(NotezFileUtil.isNotez(f))
				{
					NotezNotes.creNote(f);
				}
			}
		}

		return foundNotes;
	}
}