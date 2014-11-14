package de.gui;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotezPagedData implements NotezData
{
	private ObservableList<NotezData> pages;
	private ReadOnlyIntegerWrapper curIndex;
	private ReadOnlyObjectWrapper<NotezData> curData;

	private ReadOnlyBooleanWrapper textChanged;

	public NotezPagedData()
	{
		pages = getPagesOrig();
		curIndex = curIndexPropertyOrig();
		curIndex.set(-1);
		curData = curDataPropertyOrig();
		curData.set(null);
		textChanged = textChangedPropertyOrig();

		setListener();

		creNewPage(true);
	}

	protected void setListener()
	{
		curIndexProperty().addListener((c, o, n) ->
		{
			if(n.intValue() != -1)
			{
				curDataPropertyOrig().set(getPages().get(n.intValue()));
			}
		});
	}

	public void nextPage()
	{
		setCurIndex(curIndexProperty().get() + 1, true);
	}

	public void previousPage()
	{
		setCurIndex(curIndexProperty().get() - 1, false);
	}

	public void creNewPage(boolean switchToNewPage)
	{
		ObservableList<NotezData> pages = getPagesOrig();
		pages.add(new NotezDataImpl());
		resetTextChanged();

		if(switchToNewPage)
		{
			setCurIndex(pages.size() - 1, false);
		}
	}

	public void goToPage(int index)
	{
		setCurIndex(index, false);
	}

	public ReadOnlyObjectProperty<NotezData> curDataProperty()
	{
		return curDataPropertyOrig().getReadOnlyProperty();
	}

	protected void setCurIndex(int index, boolean creNewPage)
	{
		if(index < 0 || index > getPagesOrig().size())
		{
			return;
		}

		if(index == getPagesOrig().size())
		{
			if(!creNewPage)
			{
				return;
			}

			creNewPage(false);
		}

		curIndexPropertyOrig().set(index);
	}

	public ReadOnlyIntegerProperty curIndexProperty()
	{
		return curIndexPropertyOrig().getReadOnlyProperty();
	}

	public ObservableList<NotezData> getPages()
	{
		return FXCollections.unmodifiableObservableList(getPagesOrig());
	}
	protected ReadOnlyIntegerWrapper curIndexPropertyOrig()
	{
		return curIndex == null ? new ReadOnlyIntegerWrapper() : curIndex;
	}

	protected ReadOnlyObjectWrapper<NotezData> curDataPropertyOrig()
	{
		return curData == null ? new ReadOnlyObjectWrapper<NotezData>(null) : curData;
	}

	protected ObservableList<NotezData> getPagesOrig()
	{
		return pages == null ? FXCollections.observableArrayList() : pages;
	}

	@Override
	public StringProperty savedTextProperty()
	{
		return curDataProperty().get().savedTextProperty();
	}

	@Override
	public StringProperty curTextProperty()
	{
		return curDataProperty().get().curTextProperty();
	}

	@Override
	public ReadOnlyBooleanProperty textChangedProperty()
	{
		return textChanged.getReadOnlyProperty();
	}

	public ReadOnlyBooleanWrapper textChangedPropertyOrig()
	{
		return textChanged == null ? new ReadOnlyBooleanWrapper() : textChanged;
	}

	protected void resetTextChanged()
	{
		textChanged.unbind();

		ObservableBooleanValue b = null;

		for(NotezData page : getPages())
		{
			if(b == null)
			{
				b = page.textChangedProperty();
			}
			else
			{
				b = ((BooleanExpression)b).or(page.textChangedProperty());
			}
		}

		textChanged.bind(b);
	}
}