package de.gui;

import static de.util.NotezProperties.NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY;
import static de.util.NotezProperties.NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ;
import static de.util.NotezProperties.get;
import static de.util.NotezProperties.setBoolean;

import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import de.util.NotezRemoteSync;

public class NotezTray
{
    private static final String TRAY_ICON =
                    "include/icons/tray.png";

    private static final String TRAY_TOOLTIP =
                    "Service for receiving Notez!";

    private TrayIcon trayIcon;
    private Stage lastAdded;

    private SystemTray tray;

    private PopupMenu popup;

    private CheckboxMenuItem rIOpenDirect;

    private CheckboxMenuItem rIShowMessage;

    private Menu itmNotOpened;

    public NotezTray()
    {
        Platform.runLater(() ->
        {
            init(get(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY),
                get(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ));
        });
    }

    private void init(String openDirectly, String showMessage)
    {
        try
        {
            Toolkit.getDefaultToolkit();

            if(!SystemTray.isSupported())
            {
                return;
            }

            boolean openEnabled = openDirectly == "null" ? true
                            : Boolean.valueOf(openDirectly);
            boolean messageEnabled = showMessage == "null" ? true
                            : Boolean.valueOf(showMessage);

            tray = SystemTray.getSystemTray();

            Image image = ImageIO.read(getClass().getClassLoader()
                .getResource(TRAY_ICON));
            trayIcon = new TrayIcon(image);
            trayIcon.addActionListener(event -> Platform.runLater(
                this::showNotez));
            trayIcon.setToolTip(TRAY_TOOLTIP);

            popup = new PopupMenu();
            MenuItem itmExit = new MenuItem("Exit");
            Menu itmNotezRec = new Menu("New Notez...");
            itmNotOpened = new Menu("Not opened yet");

            rIOpenDirect = new CheckboxMenuItem(
                "Open directly", openEnabled);
            rIShowMessage = new CheckboxMenuItem(
                "Show message", messageEnabled);

            rIOpenDirect.addItemListener(e ->
            {
                setBoolean(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY,
                    rIOpenDirect.getState());
            });

            rIShowMessage.addItemListener(e ->
            {
                setBoolean(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ,
                    rIShowMessage.getState());
            });

            itmExit.addActionListener(e ->
            {
                NotezRemoteSync.stopAll();
            });

            itmNotezRec.add(rIOpenDirect);
            itmNotezRec.add(rIShowMessage);

            popup.add(itmNotOpened);
            popup.add(itmNotezRec);
            popup.addSeparator();
            popup.add(itmExit);

            itmNotOpened.setEnabled(false);

            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);
        }
        catch(java.awt.AWTException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showNotez()
    {
        showNotez(lastAdded);
    }

    private void showNotez(Stage stage)
    {
        if(stage != null && !stage.isShowing())
        {
            Platform.runLater(() ->
            {
                stage.show();
            });
        }
    }

    public void showMsgNewNotez(Stage notezStage, String
                    name)
    {
        Platform.runLater(() ->
        {
            boolean openDirect = rIOpenDirect.getState();
            boolean dshowMsg = rIShowMessage.getState();

            if(dshowMsg)
            {
                trayIcon.displayMessage(
                    "Received Notez from " + name,
                    openDirect ?
                                    "Notez from " + name + " opened!" :
                                    "Click here to view it", MessageType.INFO);
            }

            this.lastAdded = notezStage;

            if(openDirect)
            {
                showNotez(lastAdded);
            }
            else
            {
                addToNotOpened(new NotezNotOpened(
                    lastAdded, name));
            }
        });
    }

    private void addToNotOpened(NotezNotOpened notezNotOpened)
    {
        itmNotOpened.setEnabled(true);
        MenuItem tmp = new MenuItem(
            notezNotOpened.getUsername());
        itmNotOpened.add(tmp);

        tmp.addActionListener(e ->
        {
            showNotez(notezNotOpened.getStage());
            itmNotOpened.remove(tmp);

            itmNotOpened.setEnabled(itmNotOpened.getItemCount() > 0);
        });
    }

    public void remove()
    {
        Platform.runLater(() ->
        {
            tray.remove(trayIcon);
        });
    }

    public class NotezNotOpened
    {
        private String username;
        private Stage stage;

        public NotezNotOpened(Stage stage, String username)
        {
            this.username = username;
            this.stage = stage;
        }

        public Stage getStage()
        {
            return stage;
        }

        public String getUsername()
        {
            return username;
        }
    }
}