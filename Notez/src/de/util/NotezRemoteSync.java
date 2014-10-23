/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.Timer;

import de.gui.NotezController;
import de.gui.NotezFrame;
import de.util.notez.NotezParsers;

public class NotezRemoteSync extends Timer
{
    private static final long serialVersionUID = 1L;

    public static final String THREAD_NAME = NotezRemoteSync.class.getName();

    final static ObservableList<NotezRemoteUser> availableRemoteUser =
                    FXCollections.observableArrayList();

    public NotezRemoteSync(File localRemoteNotezFold)
    {
        super(1000, new NotezSyncAction(localRemoteNotezFold));
    }

    public synchronized void addUser(NotezRemoteUser user)
    {
        availableRemoteUser.add(user);
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

    public static class NotezSyncAction implements ActionListener
    {
        private File remoteNotezFold;

        public NotezSyncAction(File remoteNotezFold)
        {
            this.remoteNotezFold = remoteNotezFold;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        NotezFrame.loadAllNotez(remoteNotezFold);
                    }
                    catch(IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });
        }
    }

    public static class NotezRemoteUser
    {
        // private File remoteFolder;
        private final SimpleStringProperty remoteFolder;
        // private String userName;
        private final SimpleStringProperty username;

        public NotezRemoteUser(String userName, String remoteFolder)
        {
            // this.userName = userName;
            // this.remoteFolder = remoteFolder;
            this.username = new SimpleStringProperty(userName);
            this.remoteFolder = new SimpleStringProperty(remoteFolder);
        }

        public String getRemoteFolder()
        {
            return remoteFolder.get();
        }

        public void setRemoteFolder(String remoteFolder)
        {
            this.remoteFolder.set(remoteFolder);
        }

        public String getUsername()
        {
            return username.get();
        }

        public void setUsername(String username)
        {
            this.username.set(username);
        }

        public void shareNotez(NotezController ctrl, File notez)
            throws IOException
        {
            File remoteFolder = new File(this.remoteFolder.get());

            if(remoteFolder != null && remoteFolder.exists()
               && remoteFolder.canWrite())
            {
                NotezParsers.save(ctrl,
                    new File(remoteFolder.getAbsolutePath() + File.separator
                             + notez.getName()));
            }
        }
    }

    public static ObservableList<NotezRemoteUser> getAllUsers()
    {
        return availableRemoteUser;
    }
}