package de.notez;

import static de.notez.NotezProperties.NOTEZ_WORK_FOLDER;
import static de.notez.NotezProperties.get;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.notez.data.NotezData;
import de.util.NotezFileUtil;

/**
 * Utility methods for {@link NotezNote}
 * 
 * @author ddd
 */
public class NotezNotes
{
	/** The standard-width a new note should have */
	public static final double DEF_WIDTH = 400d;
	/** The standard-height a new note should have */
	public static final double DEF_HEIGTH = 300d;

	/**
	 * @return A newly created note
	 */
	public static NotezNote creNote()
	{
		return creNote(new File(
			new File(get(NOTEZ_WORK_FOLDER))
							+ File.separator
							+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(
								System.currentTimeMillis()))
							+ NotezFileUtil.NOTEZ_FILE_POSFIX));
	}

	/**
	 * @param f The file for the new note
	 * @return The newly created file
	 */
	public static NotezNote creNote(File f)
	{
		NotezNote note = new NotezNote(f);

		note.getGui().setHeight(DEF_HEIGTH);
		note.getGui().setWidth(DEF_WIDTH);

		return note;
	}

	/**
	 * @param data The data for the new note
	 * @return The newly created note
	 */
	public static NotezNote creNote(NotezData data)
	{
		NotezNote note = creNote();
		note.loadData(data);

		return note;
	}
}
