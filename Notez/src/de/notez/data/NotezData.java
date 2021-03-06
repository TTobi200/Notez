package de.notez.data;

import java.util.Objects;

import de.notez.data.base.SerializableNotezData;

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

	/**
     * @return a serializable version of this data
     */
	public default SerializableNotezData asSerializableData()
    {
        return asSerializableData(this);
    }

	/**
     * @param data
     *            The data to be serialized
     * @return a serializable version of the given data
     * @throws NullPointerException
     */
    public static SerializableNotezData asSerializableData(NotezData data) throws NullPointerException
    {
        return new SerializableNotezData(Objects.requireNonNull(data));
    }
}
