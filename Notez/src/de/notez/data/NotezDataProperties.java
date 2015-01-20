package de.notez.data;

import javafx.beans.property.*;

/**
 * {@link NotezData} holding their data with properties
 *
 * @author ddd
 */
public interface NotezDataProperties extends NotezData
{
	/**
	 * @return A property holding the pagedata
	 */
	public ReadOnlyObjectProperty<NotezPagedDataProperties> pageDataProperty();

	@Override
	public default NotezPagedDataProperties getPageData()
	{
		return pageDataProperty().get();
	}

	/**
	 * @return A property holding the stagedata
	 */
	public ReadOnlyObjectProperty<NotezStageDataProperties> stageDataProperty();

	@Override
	public default NotezStageDataProperties getStageData()
	{
		return stageDataProperty().get();
	}

	/**
	 * @return A property holding the title
	 */
	public StringProperty titleProperty();

	@Override
	public default String getTitle()
	{
		return titleProperty().get();
	}

	@Override
	public default void setTitle(String title)
	{
		titleProperty().set(title);
	}
}
