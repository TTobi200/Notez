package de.gui;

import static de.util.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.util.NotezProperties.get;

import java.io.File;
import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import de.gui.controller.NotezController;
import de.util.NotezFileUtil;

public class NotezLoadSplash extends Application
{
    private Pane splashLayout;
    private ProgressBar loadProgress;
    private Label progressText;

    private static final int SPLASH_WIDTH = 300;
    protected static final long SLEEP_TIME = 500;

    public static final String DEF_LOCAL_NOTEZ_FOLDER = ".";

    public static ObservableList<NotezController> availableNotez =
                    FXCollections.observableArrayList();

    @Override
    public void init()
    {
        ImageView splash = new ImageView(new Image(
            NotezFileUtil.getResourceStream(NotezController.ICON_LOGO)));
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
        NotezFrame.notezOpened = FXCollections.observableArrayList();
        File localNotezFolder = new File(get(
            NOTEZ_WORK_FOLDER,
            DEF_LOCAL_NOTEZ_FOLDER));

        int foundNotes = 0;
        if(localNotezFolder.exists())
        {
            foundNotes = loadAllNotez(localNotezFolder, false);
        }
        else
        {
            NotezDialog.showWarningDialog(
                initStage,
                "Warning - Notez-Folder",
                "Cannot find Notez-Folder! "
                                + "(" + localNotezFolder + ")");

            localNotezFolder = new File(DEF_LOCAL_NOTEZ_FOLDER);
        }

        // No notes found? create default new one
        if(foundNotes == 0)
        {
            // switch to settings (init)
            NotezFrame.createNotezFrame().getStage().show();
        }

        showSplash(initStage, new NotezLoadingTask());
    }

    private void showSplash(final Stage initStage,
                    Task<ObservableList<String>> task)
    {
        progressText.textProperty().bind(task.messageProperty());
        loadProgress.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener(new NotezDissapear(initStage));
        initStage.initStyle(StageStyle.UNDECORATED);
        initStage.setScene(new Scene(splashLayout));
        initStage.setAlwaysOnTop(true);
        initStage.show();

        new Thread(task).start();
    }

    private class NotezLoadingTask extends Task<ObservableList<String>>
    {
        @Override
        protected ObservableList<String> call() throws Exception
        {
            ObservableList<String> foundNotez =
                            FXCollections.<String> observableArrayList();

            updateMessage("Loading Notez...");
            for(int i = 0; i < availableNotez.size(); i++)
            {
                Thread.sleep(SLEEP_TIME);
                updateProgress(i + 1, availableNotez.size());
                NotezController nextNote = availableNotez.get(i);
                foundNotez.add(nextNote.toString());
                updateMessage("Notez " + nextNote.getNoteFile().getName()
                              + " loaded.");

                Platform.runLater(() -> {
                    nextNote.getStage().show();
                });
            }
            Thread.sleep(SLEEP_TIME);

            return foundNotez;
        }

    }

    private class NotezDissapear implements ChangeListener<Worker.State>
    {
        private Stage initStage;

        public NotezDissapear(Stage initStage)
        {
            this.initStage = initStage;
        }

        @Override
        public void changed(
                        ObservableValue<? extends Worker.State> observableValue,
                        Worker.State oldState, Worker.State newState)
        {
            if(newState == Worker.State.SUCCEEDED)
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
        }
    }

    public static int loadAllNotez(File notezFolder, boolean show)
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
                    NotezController ctrl = NotezFrame.createNotezFrame(f);
                    if(ctrl != null)
                    {
                        if(show)
                        {
                            ctrl.getStage().show();
                        }
                        else
                        {
                            NotezLoadSplash.add(ctrl);
                            foundNotes++;
                        }
                    }
                }
            }
        }

        return foundNotes;
    }

    public static void add(NotezController ctrl)
    {
        availableNotez.add(ctrl);
    }
}