package de.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    public static ObservableList<String> availableNotez =
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
        // TODO show splash before creating notez frame
        // For that splash has to be modal and always on top!
        new NotezFrame().start(initStage);
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
                String nextNote = availableNotez.get(i);
                foundNotez.add(nextNote);
                updateMessage("Notez " + nextNote + " loaded.");
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
                fadeSplash.setOnFinished(new EventHandler<ActionEvent>()
                {
                    @Override
                    public void handle(ActionEvent arg0)
                    {
                        initStage.hide();
                    }
                });
                fadeSplash.play();
            }
        }
    }

    public static void add(String name)
    {
        availableNotez.add(name);
    }
}