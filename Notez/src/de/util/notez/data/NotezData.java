package de.util.notez.data;

/**
 * An object representing all the data of a note:
 * <ul>
 * <li>a title
 * <li>{@link NotezStageData}
 * <li>{@link NotezPagedData}
 * </ul>
 *
 * @author ddd
 */
public interface NotezData
{
	/**
	 * @return the title of the note
	 */
	public String getTitle();
	/**
	 * @param title the new title for the note
	 */
	public void setTitle(String title);

	/**
	 * @return the stagedata of the note
	 */
	public NotezStageData getStageData();
	/**
	 * @return The pageddata of the note.
	 */
	public NotezPagedData getPageData();
}
