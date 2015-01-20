package de.notez.data.base;

import java.util.Objects;

import javafx.beans.property.*;
import javafx.collections.*;
import de.notez.data.*;

/**
 * Abase implementation of the {@link NotezTextDataProperties}-interface
 *
 * @author ddd
 */
public class BaseNotezPagedDataProperties extends NotezPagedDataPropertiesBase
{
	/** property holding the current page index */
	protected ReadOnlyIntegerWrapper curPageIndex;
	/** Property holding the current page */
	protected ReadOnlyObjectWrapper<NotezTextDataProperties> curPage;
	/** Property holding the pages of this data */
	protected ObservableList<NotezTextDataProperties> pages;

	public BaseNotezPagedDataProperties()
	{
		super();
	}

	public BaseNotezPagedDataProperties(NotezPagedData pagedData)
	{
		super(pagedData);
	}

	public BaseNotezPagedDataProperties(NotezTextData... pages)
	{
		super(pages);
	}

	@Override
	protected ReadOnlyIntegerWrapper curPageIndexPropertyModifiable()
	{
		if(Objects.isNull(curPageIndex))
		{
			curPageIndex = new ReadOnlyIntegerWrapper(0);
		}
		return curPageIndex;
	}

	@Override
	protected ReadOnlyObjectWrapper<NotezTextDataProperties> curPagePropertyModifiable()
	{
		if(Objects.isNull(curPage))
		{
			curPage = new ReadOnlyObjectWrapper<>(null);
		}
		return curPage;
	}

	@Override
	protected ObservableList<NotezTextDataProperties> getPagesModifiable()
	{
		if(Objects.isNull(pages))
		{
			pages = FXCollections.observableArrayList();
		}

		return pages;
	}
}
