/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.util.notez;

import java.io.File;
import java.io.IOException;

import de.gui.NotezController;

public interface NotezParser
{
	public static final String STRING_VERSION = "Version";
	
	public default NotezData parse(String path) throws IOException
	{
		return parse(new File(path));
	}
	
	public NotezData parse(File file) throws IOException;
	
	public default void save(NotezController controller) throws IOException
	{
		save(controller, controller.getNoteFile());
	}
	
	public default void save(NotezController controller, String path) throws IOException
	{
		save(controller, new File(path));
	}
	
	public void save(NotezController controller, File file) throws IOException;
}
