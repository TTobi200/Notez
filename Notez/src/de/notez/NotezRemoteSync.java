/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez;

import java.io.*;
import java.util.Objects;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.stage.Stage;

import javax.swing.Timer;

import de.gui.*;
import de.notez.prop.NotezSystemProperties;
import de.util.NotezFileUtil;

public class NotezRemoteSync
{
	public static final String THREAD_NAME = NotezRemoteSync.class.getName();

	private final static ObservableList<NotezRemoteUser> availableRemoteUser = FXCollections.observableArrayList();

	public static ObservableList<File> notezFiles = FXCollections.observableArrayList();

	private static File localRemFold;
	private static Timer foldSync;

	private NotezRemoteSync()
	{

	}

	public static void initialize(File localRemFold)
	{
		if(isInitialized())
		{
			return;
		}
		
		NotezRemoteSync.localRemFold = localRemFold;
		start();
	}
	
	public static boolean isInitialized()
	{
		return Objects.nonNull(foldSync);
	}

	public static void start()
	{
		if(foldSync == null)
		{
			foldSync = creFoldSync(localRemFold);
		}
		foldSync.start();

		if(NotezSystem.getSystemProperties().getBoolean(
			NotezSystemProperties.NOTEZ_RECEIVER_ON_STARTUP, false))
		{
			NotezTray.addNotezTray();
		}
	}

	private static Timer creFoldSync(File folder)
	{
		return new Timer(1000, ae ->
		{
			Platform.runLater(() ->
			{
				try
				{
					if(NotezFileUtil.directoryExists(folder))
					{
						for(File f : folder.listFiles())
						{
							if(NotezFileUtil.isNotez(f) && !notezFiles.contains(f))
							{

								NotezLoadSplash.loadAllNotez(folder);
								notezFiles.add(f);
							}
						}
					}
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			});
		});
	}

	public static synchronized void addUser(NotezRemoteUser user)
	{
		availableRemoteUser.add(user);
	}

	public static synchronized void removeUser(NotezRemoteUser user)
	{
		availableRemoteUser.remove(user);
	}

	public static void addNewUser(Stage stage) throws IOException, InterruptedException
	{
		NotezRemoteUser user = NotezDialog.showAddUserDialog(stage);

		if(user != null && user.getUsername() != null && user.getShare() != null)
		{
			addUser(user);
		}
	}

	public static NotezRemoteUser getUser(String username)
	{
		for(NotezRemoteUser u : availableRemoteUser)
		{
			if(u.getUsername().equals(username))
			{
				return u;
			}
		}

		return null;
	}

	public static ObservableList<NotezRemoteUser> getAllUsers()
	{
		return availableRemoteUser;
	}

	public static boolean isRunning()
	{
		return (foldSync != null && foldSync.isRunning()) || NotezTray.isNotezTrayAdded();
	}

	public static void stopAll()
	{
		if(isRunning())
		{
			if(Objects.nonNull(foldSync))
			{
				foldSync.stop();
				foldSync = null;
			}

			NotezTray.removeTrayIcon();
		}
	}

	public static class NotezRemoteUser
	{
		private final SimpleStringProperty username;
		private final SimpleObjectProperty<Object> share;

		public NotezRemoteUser(String userName, String share)
		{
			this.username = new SimpleStringProperty(userName);
			this.share = new SimpleObjectProperty<Object>(share);
		}

		public Object getShare()
		{
			return share.get();
		}

		public void setShare(String share)
		{
			this.share.set(share);
		}

		public String getUsername()
		{
			return username.get();
		}

		public void setUsername(String username)
		{
			this.username.set(username);
		}

		@Override
		public String toString()
		{
			return getUsername();
		}
	}
}