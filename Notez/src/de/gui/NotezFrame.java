/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.util.NotezFileUtil;
import de.util.NotezSettings;

public class NotezFrame extends Application
{
    public static final double DEF_WIDTH = 299d;
    public static final double DEF_HEIGTH = 212d;

    public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
    public static final String SETTINGS_FILE = "./Settings";
    public static final String NOTEZ_FILE_POSFIX = ".notez";

    public static String LOCAL_NOTEZ_FOLDER = "LOCAL_NOTEZ_FOLDER";

    public static ObservableList<NotezController> notezOpened;

    public static ObservableList<File> notezFiles =
                    FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // FORTEST test remote notez
        // new NotezRemoteSync().addUser(new NotezRemoteUser("User 1", new File(
        // "./remote")));

        notezOpened = FXCollections.observableArrayList();
        NotezSettings.load(new File(SETTINGS_FILE));

        LOCAL_NOTEZ_FOLDER = NotezSettings.getString(LOCAL_NOTEZ_FOLDER);

        // TODO not working inside jar?
        // if(NotezFileUtil.fileCanBeLoad(fxmlFile))
        {
            int foundNotes = 0;
            File notezFolder = new File(LOCAL_NOTEZ_FOLDER);
            if(notezFolder.exists())
            {
                foundNotes = loadAllNotez(notezFolder);
            }
            else
            {
                NotezDialog.showWarningDialog(
                    primaryStage,
                    "Warning - Notez-Folder",
                    "Cannot find Notez-Folder! " + LOCAL_NOTEZ_FOLDER);
            }

            // No notes found? create default new one
            if(foundNotes == 0)
            {
                // switch to settings (init)
                createNotezFrame();
            }
        }
        // TODO errorcannot load fxml
    }

    public static int loadAllNotez(File notezFolder) throws IOException
    {
        int foundNotes = 0;
        for(File f : notezFolder.listFiles())
        {
            if(f.getName().endsWith(NOTEZ_FILE_POSFIX)
               && !notezFiles.contains(f))
            {
                NotezLoadSplash.add(f.getName());
                if(createNotezFrame(f) == null)
                {
                	NotezLoadSplash.add(f.getName() + " failed");
                	continue;
                }
                foundNotes++;
            }
        }

        return foundNotes;
    }

    public static Stage createNotezFrame() throws IOException
    {
        return createNotezFrame(new File(
            NotezFrame.LOCAL_NOTEZ_FOLDER
                            + File.separator
                            + new SimpleDateFormat(
                                "yyyy-MM-dd_HH-mm-ss")
                                .format(new Date(
                                    System.currentTimeMillis()))
                            + NotezFrame.NOTEZ_FILE_POSFIX));
    }

    public static Stage createNotezFrame(File f) throws IOException
    {
        return createNotezFrame(new Stage(), f);
    }

    public static Stage createNotezFrame(Stage stage, File f)
        throws IOException
    {
        // FXMLLoader loader = new FXMLLoader(
        // fxmlFile.toURI().toURL());
        FXMLLoader loader = new FXMLLoader(
            NotezFileUtil.getResourceURL(FXML_PATH));

        NotezController ctrl = new NotezController(stage, f, notezOpened.size());

        loader.setController(ctrl);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setHeight(DEF_HEIGTH);
        stage.setWidth(DEF_WIDTH);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        notezOpened.add(ctrl);
        notezFiles.add(f);

        return stage;
    }

    public static NotezController getNotez(Integer idx)
    {
        return notezOpened.get(idx);
    }
}