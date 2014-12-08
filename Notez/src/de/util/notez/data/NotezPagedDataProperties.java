package de.util.notez.data;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

/**
 * {@link NotezPagedData} that hold their data with properties.
 *
 * @author ddd
 */
public interface NotezPagedDataProperties extends NotezPagedData, NotezTextDataProperties
{
	/**
	 * @return The property holding the current pageindex
	 */
	public ReadOnlyIntegerProperty curPageIndexProperty();

	@Override
	public default int getCurPageIndex()
	{
		return curPageIndexProperty().get();
	}

	@Override
	public void setCurPageIndex(int index) throws IllegalArgumentException;

	/**
	 * @return The property holding the curretn page
	 */
	public ReadOnlyObjectProperty<NotezTextDataProperties> curPageProperty();

	@Override
	public default NotezTextDataProperties getCurPage()
	{
		return curPageProperty().get();
	}

	public ObservableList<NotezTextDataProperties> getPagesObservable();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public default ObservableList<NotezTextData> getPages()
	{
		ObservableList ret = getPagesObservable();
		return ret;
	}

	@Override
	public default String getText()
	{
		return NotezTextDataProperties.super.getText();
	}

	@Override
	public default void setText(String text)
	{
		NotezTextDataProperties.super.setText(text);
	}

	/**
	 * @return A property holding the size of this pages.
	 */
	public ReadOnlyIntegerProperty sizeProperty();
}
