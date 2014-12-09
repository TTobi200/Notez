/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.Timer;

import de.gui.NotezFrame;
import de.gui.NotezTray;
import de.util.notez.data.NotezData;

public class NotezRemoteSync
{
	public static final String THREAD_NAME = NotezRemoteSync.class.getName();

	final static ObservableList<NotezRemoteUser> availableRemoteUser = FXCollections.observableArrayList();

	public static final int SERVER_PORT = 55555;

	public static ObservableList<File> notezFiles = FXCollections.observableArrayList();

	private static ServerSocket receive;
	private static File localRemFold;
	private static Timer foldSync;
	private static Thread tcpThread;
	private static NotezTray tray;

	private NotezRemoteSync()
	{

	}

	public static void initialize(File localRemFold)
	{
		NotezRemoteSync.localRemFold = localRemFold;
		start();
	}

	public static void start()
	{
		if(foldSync == null)
		{
			foldSync = creFoldSync(localRemFold);
		}
		foldSync.start();

		if(tcpThread == null)
		{
			tcpThread = creTcpThread();
		}
		tcpThread.setDaemon(true);
		tcpThread.start();
	}

	private static Thread creTcpThread()
	{
		return new Thread(() ->
		{
			try
			{
				receive = new ServerSocket(SERVER_PORT);
			}
			catch(IOException e2)
			{
				e2.printStackTrace();
			}
			try
			{
				// TODO commit loaded params
			tray = new NotezTray();
			Socket socket;
			while((socket = receive.accept()) != null)
			{
				ObjectInputStream in = new ObjectInputStream(
					socket.getInputStream());

				NotezData data = (NotezData)in.readObject();

				Platform.runLater(() ->
				{
					// TODO add sender username
					try
					{
						tray.showMsgNewNotez(NotezFrame.createNotezFrame(data)
							.getStage(), "Username");
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				});
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}   );
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
							if(NotezFileUtil.isNotez(f)
							   && !notezFiles.contains(f))
							{
								NotezFrame.loadAllNotez(folder);
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
		return foldSync != null && tcpThread != null && foldSync.isRunning()
			   && tcpThread.isAlive();
	}

	public static void stopAll()
	{
		if(isRunning())
		{
			foldSync.stop();
			tray.remove();
			tcpThread.interrupt();

			foldSync = null;
			tcpThread = null;
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