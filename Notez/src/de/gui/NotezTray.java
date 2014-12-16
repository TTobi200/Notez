package de.gui;

import static de.notez.NotezProperties.NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY;
import static de.notez.NotezProperties.NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ;
import static de.notez.NotezProperties.get;
import static de.notez.NotezProperties.setBoolean;

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
import javax.swing.SwingUtilities;

import de.notez.NotezRemoteSync;
import de.notez.data.NotezData;
import de.notez.network.NotezServer;

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

    // FIXME $TTobi won't be called ???
    public NotezTray()
    {
        Platform.runLater(() ->
        {
            init(get(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY),
                get(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ));
        });
        
        NotezServer.addRemoteListener(e ->
        {
        	if(e.getRemoteObject() instanceof NotezData)
			{
				Platform.runLater(() ->
				{
					// TODO add sender username
					try
					{
						showMsgNewNotez(NotezFrame.createNotezFrame((NotezData)e.getRemoteObject())
							.getStage(), "Username");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				});
			}
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

            boolean openEnabled = openDirectly == null ? true
                            : Boolean.valueOf(openDirectly);
            boolean messageEnabled = showMessage == null ? true
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
            MenuItem itmNewNotez = new MenuItem("New Notez");
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

            itmNewNotez.addActionListener(e ->
            {
                creNewNotez();
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
            popup.add(itmNewNotez);
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

    private void creNewNotez()
    {
        // FIXME $DDD: If all stages closed,
        // runnable never executed by Platform
        Platform.runLater(() ->
        {
            try
            {
                NotezFrame.createNotezFrame()
                    .getStage().show();
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
        });
    }

    private void showNotez()
    {
        showNotez(lastAdded);
    }

    private void showNotez(Stage stage)
    {
        if(stage != null && !stage.isShowing())
        {
            // FIXME $DDD: If all stages closed,
            // runnable never executed by Platform
            Platform.runLater(() ->
            {
                stage.show();
            });
        }
    }

    public void showMsgNewNotez(Stage notezStage, String
                    name)
    {
        SwingUtilities.invokeLater(() ->
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
        SwingUtilities.invokeLater(() ->
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