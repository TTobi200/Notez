/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.gui.controller.NotezController;
import de.util.NotezFileUtil;
import de.util.NotezProperties;
import de.util.NotezRemoteSync;
import de.util.NotezRemoteSync.NotezRemoteUser;
import de.util.notez.data.NotezData;

public class NotezFrame extends Application
{
    public static final double DEF_WIDTH = 400d;
    public static final double DEF_HEIGTH = 300d;

    public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
    public static final String NOTEZ_FILE_POSFIX = ".notez";

    public static final String DEF_LOCAL_NOTEZ_FOLDER = ".";

    public static ObservableList<NotezController> notezOpened;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // FORTEST added remote sync
        switch(NotezDialog.showRememberQuestionDialog(null,
            "Receiving service",
            "Do you like to start the receiving notez service?",
            NotezProperties.PROP_START_RECEIVER))
        {
            default:
            case CANCEL:
            case CLOSE:
            case NO:
                // Do nothing
                break;

            case OK:
            case YES:
                NotezRemoteSync.initialize(new File(NotezProperties.get(
                    NotezProperties.PROP_NOTEZ_REMOTE_FOLDER,
                    DEF_LOCAL_NOTEZ_FOLDER)));
                NotezRemoteSync.addUser(new NotezRemoteUser(
                    "localhost", "127.0.0.1"));
                break;
        }

        notezOpened = FXCollections.observableArrayList();
        File localNotezFolder = new File(NotezProperties.get(
            NotezProperties.PROP_NOTEZ_WORK_FOLDER, DEF_LOCAL_NOTEZ_FOLDER));

        int foundNotes = 0;
        if(localNotezFolder.exists())
        {
            foundNotes = loadAllNotez(localNotezFolder);
        }
        else
        {
            NotezDialog.showWarningDialog(
                primaryStage,
                "Warning - Notez-Folder",
                "Cannot find Notez-Folder! "
                                + "(" + localNotezFolder + ")");

            localNotezFolder = new File(DEF_LOCAL_NOTEZ_FOLDER);
        }

        // No notes found? create default new one
        if(foundNotes == 0)
        {
            // switch to settings (init)
            createNotezFrame().getStage().show();
        }
    }

    public static int loadAllNotez(File notezFolder) throws IOException
    {
        int foundNotes = 0;
        File[] notez = notezFolder.listFiles();
        if(notez != null)
        {
            for(File f : notez)
            {
                if(NotezFileUtil.isNotez(f))
                {
                    NotezLoadSplash.add(f.getName());
                    NotezController ctrl;
                    if((ctrl = createNotezFrame(f)) == null)
                    {
                        NotezLoadSplash.add(f.getName() + " failed");
                        continue;
                    }
                    else
                    {
                        ctrl.getStage().show();
                        foundNotes++;
                    }
                }
            }
        }

        return foundNotes;
    }

    public static NotezController createNotezFrame() throws IOException
    {
        return createNotezFrame(new File(
            new File(NotezProperties.get(
                NotezProperties.PROP_NOTEZ_WORK_FOLDER, DEF_LOCAL_NOTEZ_FOLDER))
                            + File.separator
                            + new SimpleDateFormat(
                                "yyyy-MM-dd_HH-mm-ss")
                                .format(new Date(
                                    System.currentTimeMillis()))
                            + NotezFrame.NOTEZ_FILE_POSFIX));
    }

    public static NotezController createNotezFrame(File f) throws IOException
    {
        return createNotezFrame(new Stage(), f);
    }

    public static NotezController createNotezFrame(NotezData data)
    {
        NotezController ctrl = null;
        try
        {
            ctrl = createNotezFrame();
            ctrl.loadNote(data);
        }
        catch(IOException e)
        {
            // TODO Exception handling here
            e.printStackTrace();
        }

        return ctrl;
    }

    public static NotezController createNotezFrame(Stage stage, File f)
        throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
            NotezFileUtil.getResourceURL(FXML_PATH));

        NotezController ctrl = new NotezController(stage, f, notezOpened.size());

        loader.setController(ctrl);
        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        // XXX Add this to gain drop shadow (1/2)
        // Group g = new Group();
        // Scene scene = new Scene(g);
        // scene.setFill(null);
        // root.setPadding(new Insets(10, 10, 10, 10));
        // root.setEffect(new DropShadow());

        stage.setScene(scene);
        // Fixed set height/width needed for dialogs (relative to)
        stage.setHeight(DEF_HEIGTH);
        stage.setWidth(DEF_WIDTH);
        stage.initStyle(StageStyle.TRANSPARENT);
        // stage.show();

        // XXX Add this to gain drop shadow (2/2)
        // g.getChildren().add(root);

        notezOpened.add(ctrl);

        return ctrl;
    }

    public static NotezController getNotez(Integer idx)
    {
        return notezOpened.get(idx);
    }
}