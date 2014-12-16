/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.Timer;

import de.gui.NotezLoadSplash;
import de.gui.NotezTray;
import de.util.NotezFileUtil;

public class NotezRemoteSync
{
    public static final String THREAD_NAME = NotezRemoteSync.class.getName();

    final static ObservableList<NotezRemoteUser> availableRemoteUser = FXCollections.observableArrayList();

    public static ObservableList<File> notezFiles = FXCollections.observableArrayList();

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
        
        tray = new NotezTray();
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

                                NotezLoadSplash.loadAllNotez(folder, true);
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