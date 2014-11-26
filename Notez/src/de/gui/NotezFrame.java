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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.gui.controller.NotezController;
import de.util.NotezFileUtil;
import de.util.NotezProperties;

public class NotezFrame extends Application
{
    public static final double DEF_WIDTH = 299d;
    public static final double DEF_HEIGTH = 212d;

    public static final String FXML_PATH = "include/fxml/NotezGui.fxml";
    public static final String NOTEZ_FILE_POSFIX = ".notez";

    private static final String DEF_LOCAL_NOTEZ_FOLDER = ".";
    public static File localNotezFolder;

    public static ObservableList<NotezController> notezOpened;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        notezOpened = FXCollections.observableArrayList();
        localNotezFolder = new File(NotezProperties.get(
            NotezProperties.PROP_NOTEZ_FOLDER, DEF_LOCAL_NOTEZ_FOLDER));

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
            createNotezFrame();
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
                    if(createNotezFrame(f) == null)
                    {
                        NotezLoadSplash.add(f.getName() + " failed");
                        continue;
                    }
                    foundNotes++;
                }
            }
        }

        return foundNotes;
    }

    public static Stage createNotezFrame() throws IOException
    {
        return createNotezFrame(new File(
            NotezFrame.localNotezFolder.getAbsolutePath()
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

        return stage;
    }

    public static NotezController getNotez(Integer idx)
    {
        return notezOpened.get(idx);
    }
}