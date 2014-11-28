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

public class NotezRemoteSync extends Timer
{
    private static final long serialVersionUID = 1L;

    public static final String THREAD_NAME = NotezRemoteSync.class.getName();

    final static ObservableList<NotezRemoteUser> availableRemoteUser =
                    FXCollections.observableArrayList();

    public static final int SERVER_PORT = 55555;

    public static ObservableList<File> notezFiles =
                    FXCollections.observableArrayList();

    private ServerSocket receive;

    public NotezRemoteSync(File localRemoteNotezFold)
    {
        super(1000, e ->
        {
            Platform.runLater(() ->
            {
                try
                {
                    for(File f : localRemoteNotezFold.listFiles())
                    {
                        if(NotezFileUtil.isNotez(f) &&
                           !notezFiles.contains(f))
                        {
                            NotezFrame.loadAllNotez(localRemoteNotezFold);
                            notezFiles.add(f);
                        }
                    }
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                }
            });
        });

        try
        {
            receive = new ServerSocket(SERVER_PORT);
        }
        catch(IOException e2)
        {
            e2.printStackTrace();
        }

        // FORTEST add server for receiving notez
        Thread t = new Thread(() ->
		{
		    try
		    {
		    	Socket socket;
		    	while((socket = receive.accept()) != null)
		    	{
//		            BufferedReader bufferedReader =
//		                            new BufferedReader(
//		                                new InputStreamReader(
//		                                    socket.getInputStream()));
//		            char[] buffer = new char[200];
//		            int anzahlZeichen = bufferedReader.read(buffer, 0, 200);
		    		
		    		ObjectInputStream in = new ObjectInputStream(
		    			socket.getInputStream());
		    		
		    		// Read data
//		    		NotezData data = (NotezData)in.readObject();

		    	}
		    }
		    catch(Exception e1)
		    {
		        e1.printStackTrace();
		    }
		});
        t.setDaemon(true);
        t.start();
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

    public static ObservableList<NotezRemoteUser> getAllUsers()
    {
        return availableRemoteUser;
    }
}