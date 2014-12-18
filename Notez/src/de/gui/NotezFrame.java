/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import static de.notez.NotezProperties.NOTEZ_RECEIVER_ON_STARTUP;
import static de.notez.NotezProperties.NOTEZ_REMOTE_FOLDER;
import static de.notez.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.notez.NotezProperties.get;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.gui.controller.NotezController;
import de.notez.NotezRemoteSync;
import de.notez.data.NotezData;
import de.util.NotezFileUtil;
import de.util.log.NotezLog;

public class NotezFrame
{
    public static final String NOTEZ_FILE_POSFIX = ".notez";

    public static void startReceiver() throws Exception
    {
        switch(NotezDialog.showRememberQuestionDialog(null,
            "Receiving service",
            "Do you like to start the receiving notez service?",
            NOTEZ_RECEIVER_ON_STARTUP))
        {
            default:
            case CANCEL:
            case CLOSE:
            case NO:
                // Do nothing
                break;

            case OK:
            case YES:
                NotezRemoteSync.initialize(new File(get(
                    NOTEZ_REMOTE_FOLDER,
                    DEF_REMOTE_NOTEZ_FOLDER)));
                break;
        }
    }
    
    public static NotezController createNotezFrame() throws IOException
    {
        return createNotezFrame(new File(
            new File(get(
                NOTEZ_WORK_FOLDER,
                DEF_LOCAL_NOTEZ_FOLDER))
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
        	NotezLog.error("Could not load NotezData!", e);
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
        stage.getIcons().add(
            new Image(NotezFileUtil.getResourceStream(NOTEZ_LOGO)));
        // stage.show();

        // XXX Add this to gain drop shadow (2/2)
        // g.getChildren().add(root);

        notezOpened.add(ctrl);

        return ctrl;
    }
}