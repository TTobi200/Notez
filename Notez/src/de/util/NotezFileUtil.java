/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import de.gui.NotezFrame;

public class NotezFileUtil
{
	public static final String INCLUDE_FOLDER = "include";
	public static final String FXML_FOLDER = INCLUDE_FOLDER + File.separator + "fxml";
	
    public static boolean fileCanBeLoad(String filePath)
    {
        return fileCanBeLoad(new File(filePath));
    }

    public static boolean fileCanBeLoad(File file)
    {
        return fileExists(file) && file.canRead();
    }

    public static boolean fileCanBeSaved(String filePath)
    {
        return fileCanBeSaved(new File(filePath));
    }

    public static boolean fileCanBeSaved(File file)
    {
        return fileExists(file) && file.canWrite();
    }

    public static boolean fileExists(File file)
    {
        if(file != null)
        {
            if(file.exists() && file.isFile())
            {
                return true;
            }
        }
        return false;
    }

    public static boolean directoryExists(File directory)
    {
        if(directory != null)
        {
            if(directory.exists() && directory.isDirectory())
            {
                return true;
            }
        }
        return false;
    }

    public static File creOpenDialog(Window parent, String initPath)
    {
        FileChooser f = new FileChooser();
        f.setInitialDirectory(new File(initPath));
        return f.showOpenDialog(parent);
    }

    public static String getResourceFile(String path)
    {
        return NotezFileUtil.class.getClassLoader().getResource(path).getFile();
    }

    public static URL getResourceURL(String path)
    {
        return NotezFileUtil.class.getClassLoader().getResource(path);
    }

    public static InputStream getResourceStream(String icon)
    {
        return NotezFileUtil.class.getClassLoader()
            .getResourceAsStream(icon);
    }
    
    public static void openParentFolderInBrowser(File file) throws IOException
    {
        if(NotezFileUtil.directoryExists(file))
        {
            if(Desktop.isDesktopSupported())
            {
                Desktop desktop = Desktop.getDesktop();

                if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
                {
                    desktop.browse(file.toURI());
                }
            }
        }
    }

    public static boolean canBeUsedAsFilename(String filename)
    {
        // TODO add specifications
        return filename != null && !filename.isEmpty() &&
               !filename.contains("/");
    }

    public static boolean isNotez(File f)
    {
        return f.getName().endsWith(
            NotezFrame.NOTEZ_FILE_POSFIX);
    }

    public static String removeEnding(String fileName)
    {
        return fileName.replace(NotezFrame.NOTEZ_FILE_POSFIX, "");
    }
}