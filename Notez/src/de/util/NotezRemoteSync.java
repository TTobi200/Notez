/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.swing.Timer;

import de.gui.NotezFrame;

public class NotezRemoteSync implements ActionListener
{
    public static final String THREAD_NAME = NotezRemoteSync.class.getName();

    public static ObservableList<NotezRemoteUser> availableRemoteUser =
                    FXCollections.observableArrayList();

    public NotezRemoteSync()
    {
        Timer sync = new Timer(1000, this);
        sync.start();
    }

    public synchronized void addUser(NotezRemoteUser user)
    {
        availableRemoteUser.add(user);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        for(NotezRemoteUser u : availableRemoteUser)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        NotezFrame.loadAllNotez(u.getRemoteFolder());
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
        private File remoteFolder;
        private String userName;

        public NotezRemoteUser(String userName, File remoteFolder)
        {
            this.userName = userName;
            this.remoteFolder = remoteFolder;
        }

        public File getRemoteFolder()
        {
            return remoteFolder;
        }

        public String getUserName()
        {
            return userName;
        }
    }
}