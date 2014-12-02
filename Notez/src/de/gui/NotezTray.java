package de.gui;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class NotezTray
{
    private static final String TRAY_ICON =
                    "include/icons/tray.png";

    private static final String TRAY_TOOLTIP =
                    "Service for receiving Notez!";

    private TrayIcon trayIcon;
    private Stage stage;

    public NotezTray()
    {
        init();
    }

    private void init()
    {
        try
        {
            Toolkit.getDefaultToolkit();

            if(!SystemTray.isSupported())
            {
                return;
            }

            SystemTray tray = SystemTray.getSystemTray();

            Image image = ImageIO.read(getClass().getClassLoader()
                .getResource(TRAY_ICON));
            trayIcon = new TrayIcon(image);
            trayIcon.addActionListener(event -> Platform.runLater(
                this::showNotez));
            trayIcon.setToolTip(TRAY_TOOLTIP);

            tray.add(trayIcon);
        }
        catch(java.awt.AWTException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showNotez()
    {
        if(stage != null && !stage.isShowing())
        {
            stage.show();
        }
    }

    public void showMsgNewNotez(Stage notezStage, String
                    caption, String msg)
    {
        trayIcon.displayMessage(
            caption, msg, MessageType.INFO);
        this.stage = notezStage;
    }
}