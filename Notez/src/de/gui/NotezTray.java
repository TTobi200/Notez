package de.gui;

import static de.notez.prop.NotezProperties.*;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.util.*;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.sun.javafx.application.PlatformImpl;

import de.notez.*;
import de.notez.data.NotezData;
import de.notez.network.*;

public class NotezTray implements NotezRemoteObjectListener
{
	private static final String TRAY_ICON = "include/icons/tray.png";

	private static final String TRAY_TOOLTIP = "Service for receiving Notez!";

	public static final boolean SUPPORTED = SystemTray.isSupported();

	private TrayIcon trayIcon;
	private Stage lastAdded;
	
	private SystemTray tray;

	private CheckboxMenuItem rIOpenDirect;

	private CheckboxMenuItem rIShowMessage;

	private Menu itmNotOpened;

	private static NotezTray notezTray;

	public static void addNotezTray()
	{
		if(!SUPPORTED)
		{
			return;
		}

		if(Objects.isNull(notezTray))
		{
			notezTray = new NotezTray();
		}
		
		if(notezTray.isIconAdded())
		{
			return;
		}

		NotezServer.addRemoteListener(notezTray);
		
		notezTray.addIcon();
	}

	public static void removeTrayIcon()
	{
		if(!SUPPORTED || Objects.isNull(notezTray) || !notezTray.isIconAdded())
		{
			return;
		}
		
		NotezServer.removeRemoteListener(notezTray);
		
		notezTray.removeIcon();
	}
	
	public static boolean isNotezTrayAdded()
	{
		return Objects.nonNull(notezTray) && notezTray.isIconAdded();
	}

	private NotezTray()
	{
		PlatformImpl.runAndWait(() ->
		{
			init(
				NotezSystem.getSystemProperties()
					.getBoolean(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY),
				NotezSystem.getSystemProperties().getBoolean(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ));
		});
	}

	private void init(boolean openDirectly, boolean showMessage)
	{
		try
		{
			Toolkit.getDefaultToolkit();

			boolean openEnabled = openDirectly;
			boolean messageEnabled = showMessage;

			Image image = ImageIO.read(getClass().getClassLoader().getResource(TRAY_ICON));
			trayIcon = new TrayIcon(image);
			trayIcon.addActionListener(event -> Platform.runLater(this::showNotez));
			trayIcon.setToolTip(TRAY_TOOLTIP);

			PopupMenu popup = new PopupMenu();
			MenuItem itmExit = new MenuItem("Exit");
			MenuItem itmNewNotez = new MenuItem("New Notez");
			Menu itmNotezRec = new Menu("New Notez...");
			itmNotOpened = new Menu("Not opened yet");

			rIOpenDirect = new CheckboxMenuItem("Open directly", openEnabled);
			rIShowMessage = new CheckboxMenuItem("Show message", messageEnabled);

			NotezSystem.getSystemProperties()
				.getBooleanProperty(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY)
				.addListener((p, o, n) -> rIOpenDirect.setState(n.booleanValue()));
			rIOpenDirect.addItemListener(e ->
			{
				NotezSystem.getSystemProperties().putBoolean(
					NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY, rIOpenDirect.getState());
			});

			NotezSystem.getSystemProperties()
				.getBooleanProperty(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ)
				.addListener((p, o, n) -> rIShowMessage.setState(n.booleanValue()));
			rIShowMessage.addItemListener(e ->
			{
				NotezSystem.getSystemProperties().putBoolean(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ,
					rIShowMessage.getState());
			});

			itmNewNotez.addActionListener(e ->
			{
				creNewNotez();
			});

			itmExit.addActionListener(e ->
			{
				NotezSystem.exit();
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
			
			tray = SystemTray.getSystemTray();
		}
		catch(IOException e)
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
				// NotezFrame.createNotezFrame()
				// .getStage().show();
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

	private void showMsgNewNotez(Stage notezStage, String name)
	{
		SwingUtilities.invokeLater(() ->
		{
			boolean openDirect = rIOpenDirect.getState();
			boolean dshowMsg = rIShowMessage.getState();

			if(dshowMsg)
			{
				trayIcon.displayMessage("Received Notez from " + name, openDirect ? "Notez from "
																					+ name
																					+ " opened!"
								: "Click here to view it", MessageType.INFO);
			}

			this.lastAdded = notezStage;

			if(openDirect)
			{
				showNotez(lastAdded);
			}
			else
			{
				addToNotOpened(new NotezNotOpened(lastAdded, name));
			}
		});
	}

	private void addToNotOpened(NotezNotOpened notezNotOpened)
	{
		itmNotOpened.setEnabled(true);
		MenuItem tmp = new MenuItem(notezNotOpened.getUsername());
		itmNotOpened.add(tmp);

		tmp.addActionListener(e ->
		{
			showNotez(notezNotOpened.getStage());
			itmNotOpened.remove(tmp);

			itmNotOpened.setEnabled(itmNotOpened.getItemCount() > 0);
		});
	}

	private void removeIcon()
	{
		SwingUtilities.invokeLater(() ->
		{
			SystemTray.getSystemTray().remove(trayIcon);
		});
	}

	private void addIcon()
	{
		SwingUtilities.invokeLater(() ->
		{
			try
			{
				SystemTray.getSystemTray().add(trayIcon);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
	}
	
	private boolean isIconAdded()
	{
		return Arrays.asList(tray.getTrayIcons()).contains(trayIcon);
	}

	@Override
	public void remoteObjectReceived(NotezRemoteObjectEvent e)
	{
		if(e.getRemoteObject() instanceof NotezData)
		{
			Platform.runLater(() ->
			{
				try
				{
					showMsgNewNotez(NotezNotes.creNote((NotezData)e.getRemoteObject()).getGui(),
						e.getClient().getUser());
					// showMsgNewNotez(NotezFrame.createNotezFrame((NotezData)e.getRemoteObject())
					// .getStage(), "Username");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			});
		}
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