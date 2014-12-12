package de.notez.data.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.notez.data.NotezPagedData;
import de.notez.data.NotezTextData;

/**
 * Base for the implementation of a {@link NotezPagedData}
 *
 * @author ddd
 */
public abstract class NotezPagedDataBase implements NotezPagedData
{
	/** The index of the current page */
	protected int curPageIndex;

	public NotezPagedDataBase()
	{
		super();

		curPageIndex = 0;
	}

	@Override
	public int getCurPageIndex()
	{
		return curPageIndex;
	}

	@Override
	public void setCurPageIndex(int index) throws IllegalArgumentException
	{
		curPageIndex = index > -1 ? index : 0;
		List<NotezTextData> pages = getPages();
		while(!(curPageIndex < pages.size()))
		{
			addPages(new BaseNotezTextData());
		}
	}

	@Override
	public List<NotezTextData> getPages()
	{
		return Collections.unmodifiableList(getPagesModifiable());
	}

	protected abstract List<NotezTextData> getPagesModifiable();

	@Override
	public void setPages(List<NotezTextData> pages)
	{
		getPagesModifiable().clear();
		getPagesModifiable().addAll(pages);
	}

	@Override
	public void addPages(NotezTextData... pages)
	{
		getPagesModifiable().addAll(Arrays.asList(pages));
	}

	@Override
	public void removePages(NotezTextData... pages)
	{
		getPagesModifiable().removeAll(Arrays.asList(pages));
	}
}
