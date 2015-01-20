package de.notez.data.base;

import java.util.*;

import de.notez.data.*;

/**
 * A basic implementation of the {@link NotezPagedData}-interface
 *
 * @author ddd
 */
public class BaseNotezPagedData extends NotezPagedDataBase
{
	/** The pages of this pagedData */
	protected List<NotezTextData> pages;

	public BaseNotezPagedData()
	{
		this(null);
	}

	public BaseNotezPagedData(List<NotezTextData> pages)
	{
		super();
		this.pages = pages == null ? new ArrayList<>() : pages;

		setCurPageIndex(0);
	}

	@Override
	protected List<NotezTextData> getPagesModifiable()
	{
		return pages;
	}

}
