package de.util.notez.data;

import java.util.List;

/**
 * An Object representing several pages of a note shown as {@link NotezTextData}
 *
 * @author ddd
 */
public interface NotezPagedData extends NotezTextData
{
	/**
	 * @return The current index, starting at 0
	 */
	public int getCurPageIndex();

	/**
	 * @param index
	 *            the new pageindex starting at 0
	 * @throws IllegalArgumentException
	 *             if this data don't accept the given index
	 */
	public void setCurPageIndex(int index) throws IllegalArgumentException;

	/**
	 * @return The currently selected page
	 */
	public default NotezTextData getCurPage()
	{
		return getPages().get(getCurPageIndex());
	}

	/**
	 * @return All pages hold by this data
	 */
	public List<NotezTextData> getPages();

	/**
	 * @param pages
	 *            the new pages to be hold by this data
	 */
	public void setPages(List<NotezTextData> pages);

	/**
	 * Go to the next page of this data<br>
	 * Behavior when trying to go on while already being on the last side is not definde andhandled
	 * by the implementation
	 */
	public default void nextPage()
	{
		setCurPageIndex(getCurPageIndex() + 1);
	}

	/**
	 * Go to the previous page of this data, if we are not already on the first one
	 */
	public default void prevPage()
	{
		if (getCurPageIndex() > 0)
		{
			setCurPageIndex(getCurPageIndex() - 1);
		}
	}

	/**
	 * @param pages
	 *            The pages to be added to this data
	 */
	public void addPages(NotezTextData... pages);

	/**
	 * @param pages
	 *            The pages to be removes from this data
	 */
	public void removePages(NotezTextData... pages);

	/**
	 * just remove the current page from this data. Nothing is done, if just one page is hold
	 */
	public default void removeCurPage()
	{
		if (!getPages().isEmpty())
		{
			removePages(getCurPage());
		}
	}

	@Override
	public default String getText()
	{
		return getCurPage().getText();
	}

	@Override
	public default void setText(String text)
	{
		getCurPage().setText(text);
	}
}
