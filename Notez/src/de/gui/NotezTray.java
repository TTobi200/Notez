package de.gui;

import static de.notez.prop.NotezProperties.*;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import de.notez.*;
import de.notez.data.NotezData;
import de.notez.network.NotezServer;
import de.util.NotezSystemUtil;

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
            init(NotezSystemUtil.getSystemProperties().getBoolean(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY),
            	NotezSystemUtil.getSystemProperties().getBoolean(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ));
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
						showMsgNewNotez(NotezNotes.creNote((NotezData)e.getRemoteObject()).getGui(), "Username");
//						showMsgNewNotez(NotezFrame.createNotezFrame((NotezData)e.getRemoteObject())
//							.getStage(), "Username");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				});
			}
        });
    }

    private void init(boolean openDirectly, boolean showMessage)
    {
        try
        {
            Toolkit.getDefaultToolkit();

            if(!SystemTray.isSupported())
            {
                return;
            }

            boolean openEnabled = openDirectly;
            boolean messageEnabled = showMessage;

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
            	NotezSystemUtil.getSystemProperties().putBoolean(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY,
                    rIOpenDirect.getState());
            });

            rIShowMessage.addItemListener(e ->
            {
            	NotezSystemUtil.getSystemProperties().putBoolean(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ,
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
    	NotezNotes.creNote().show();
        Platform.runLater(() ->
        {
            try
            {
            	NotezNotes.creNote().show();
//                NotezFrame.createNotezFrame()
//                    .getStage().show();
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