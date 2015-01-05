/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import static de.notez.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.notez.NotezProperties.get;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.notez.data.NotezData;
import de.util.NotezFileUtil;
import de.util.log.NotezLog;

public class NotezNotes
{
	public static final double DEF_WIDTH = 400d;
	public static final double DEF_HEIGTH = 300d;

	public static NotezNote creNote()
	{
		return creNote(new File(
			new File(get(NOTEZ_WORK_FOLDER))
							+ File.separator
							+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(
								System.currentTimeMillis()))
							+ NotezFileUtil.NOTEZ_FILE_POSFIX));
	}

	public static NotezNote creNote(File f)
	{
		NotezNote note;
		try
		{
			note = new NotezNote(f);
		}
		catch(IOException e)
		{
			NotezLog.warn("could not create note for file " + f.getName(), e);
			return null;
		}

		note.getGui().setHeight(DEF_HEIGTH);
		note.getGui().setWidth(DEF_WIDTH);

		return note;
	}

	public static NotezNote creNote(NotezData data)
	{
		NotezNote note = creNote();
		note.loadData(data);

		return note;
	}
}
