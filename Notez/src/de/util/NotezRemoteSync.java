/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util;

import java.io.File;
import java.io.IOException;

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

    public static ObservableList<File> notezFiles =
                    FXCollections.observableArrayList();

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