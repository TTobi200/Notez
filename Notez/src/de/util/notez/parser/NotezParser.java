/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.notez.parser;

import java.io.File;
import java.io.IOException;

import de.gui.controller.NotezControllerBase;
import de.util.notez.NotezData;

public interface NotezParser
{
    public static final String STRING_VERSION = "Version";

    public default NotezData parse(String path) throws IOException
    {
        return parse(new File(path));
    }

    public NotezData parse(File file) throws IOException;

    public default void save(NotezControllerBase<?, ?> controller)
        throws IOException
    {
        save(controller, controller.getNoteFile());
    }

    public default void save(NotezControllerBase<?, ?> controller, String path)
        throws IOException
    {
        save(controller, new File(path));
    }

    public void save(NotezControllerBase<?, ?> controller, File file)
        throws IOException;
}
