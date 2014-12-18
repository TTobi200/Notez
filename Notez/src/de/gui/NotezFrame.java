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
}