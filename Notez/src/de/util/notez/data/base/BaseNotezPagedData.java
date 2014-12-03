package de.util.notez.data.base;

import java.util.ArrayList;
import java.util.List;

import de.util.notez.data.NotezPagedData;
import de.util.notez.data.NotezTextData;

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
	}

	@Override
	protected List<NotezTextData> getPagesModifiable()
	{
		return pages;
	}

}
