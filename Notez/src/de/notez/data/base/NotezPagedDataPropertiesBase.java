package de.notez.data.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import de.notez.data.NotezPagedData;
import de.notez.data.NotezPagedDataProperties;
import de.notez.data.NotezTextData;
import de.notez.data.NotezTextDataProperties;
import de.util.NotezDataUtil;
import de.util.NotezObservablesUtil;

/**
 * A basis for {@link NotezPagedDataProperties}
 *
 * @author ddd
 */
public abstract class NotezPagedDataPropertiesBase extends BaseNotezTextDataProperties implements
		NotezPagedDataProperties
{
	protected NotezPagedDataPropertiesBase()
	{
		super();

		addPages(new BaseNotezTextDataProperties());
		setCurPageIndex(0);
		curPagePropertyModifiable().set(getPagesObservable().get(0));

		initListeners();
	}

	protected NotezPagedDataPropertiesBase(NotezPagedData pagedData)
	{
		this(pagedData.getPages().stream().toArray(NotezPagedData[]::new));
	}

	protected NotezPagedDataPropertiesBase(NotezTextData... pages)
	{
		this();

		addPages(pages);
	}

	/**
	 * initialize the listeners needed for this class to work
	 */
	protected void initListeners()
	{
		curPageIndexPropertyModifiable().addListener((p, o, n) -> {
			curPagePropertyModifiable().set(toTextDataProperties(getPages().get(n.intValue())));
		});

		curPageProperty().addListener((p, o, n) ->
		{
			textProperty().set(n.getText());
		});

		textProperty().addListener((p, o, n) ->
		{
			getCurPage().setText(n);
		});
	}

	@Override
	public void setPages(List<NotezTextData> pages)
	{
		getPagesModifiable().setAll(
			pages.stream().map(NotezPagedDataPropertiesBase::toTextDataProperties)
					.collect(Collectors.toList()));

		setCurPageIndex(0);
		curPagePropertyModifiable().set(getPagesObservable().get(0));
	}

	@Override
	public void addPages(NotezTextData... pages)
	{
		getPagesModifiable().addAll(
			Arrays.stream(pages).map(NotezPagedDataPropertiesBase::toTextDataProperties)
					.collect(Collectors.toCollection(HashSet::new)));
	}

	@Override
	public void removePages(NotezTextData... pages)
	{
		getPagesModifiable().removeAll(
			Arrays.stream(pages).map(NotezPagedDataPropertiesBase::toTextDataProperties)
					.collect(Collectors.toCollection(HashSet::new)));
	}

	@Override
	public ReadOnlyIntegerProperty curPageIndexProperty()
	{
		return curPageIndexPropertyModifiable().getReadOnlyProperty();
	}

	@Override
	public void setCurPageIndex(int index) throws IllegalArgumentException
	{
		if (index < 0 || index > getPages().size())
		{
			return;
		}

		if (index == getPages().size())
		{
			if (getPages().get(index - 1).getText().isEmpty())
			{
				return;
			}
			else
			{
				addPages(new BaseNotezTextDataProperties());
			}
		}

		curPageIndexPropertyModifiable().set(index);
	}

	/**
	 * Check whether the given data is a {@link NotezPagedDataProperties} and transform it if not.
	 *
	 * @param data
	 *            the data to check
	 * @return depending on the given object, the object itself is returned casted, or a new
	 *         {@link NotezTextDataProperties} holding the same data.
	 */
	protected static NotezTextDataProperties toTextDataProperties(NotezTextData data)
	{
		return data instanceof NotezTextDataProperties ? (NotezTextDataProperties)data
				: new BaseNotezTextDataProperties(data);
	}

	@Override
	public ReadOnlyObjectProperty<NotezTextDataProperties> curPageProperty()
	{
		return curPagePropertyModifiable().getReadOnlyProperty();
	}

	@Override
	public ObservableList<NotezTextDataProperties> getPagesObservable()
	{
		return FXCollections.unmodifiableObservableList(getPagesModifiable());
	}

	/**
	 * @return the modifiable property of the current pageindex
	 */
	protected abstract ReadOnlyIntegerWrapper curPageIndexPropertyModifiable();

	/**
	 * @return the modifiable property of the current page
	 */
	protected abstract ReadOnlyObjectWrapper<NotezTextDataProperties> curPagePropertyModifiable();

	/**
	 * @return the modifiable version of the pages
	 */
	protected abstract ObservableList<NotezTextDataProperties> getPagesModifiable();

	@Override
	public ReadOnlyIntegerProperty sizeProperty()
	{
		return NotezObservablesUtil.sizePropertyForList(getPagesModifiable());
	}
	
	@Override
	public String toString()
	{
		return NotezDataUtil.toString(this);
	}
}
