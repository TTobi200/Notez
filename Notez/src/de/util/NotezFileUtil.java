/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class NotezFileUtil
{
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

	public static File creOpenDialog(Window parent, String initPath)
	{
		FileChooser f = new FileChooser();
		f.setInitialDirectory(new File(initPath));
		return f.showOpenDialog(parent);
	}
}