package de.gui;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class NotezPagedData implements NotezData
{
	private ObservableList<NotezData>			pages;
	private ReadOnlyIntegerWrapper				curIndex;
	private ReadOnlyObjectWrapper<NotezData>	curData;

	private ReadOnlyBooleanWrapper				textChanged;

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
		curIndexProperty().addListener((c, o, n) -> {
			if (n.intValue() != -1)
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

		if (switchToNewPage)
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
		ObservableList<NotezData> pages = getPages();

		if (index < 0 || index > pages.size())
		{
			return;
		}

		if (index == pages.size())
		{
			if (!creNewPage || pages.get(pages.size() - 1).curTextProperty().get().isEmpty())
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
		// return FXCollections.unmodifiableObservableList(getPagesOrig());
		return new UnmodifiableObservableList(getPagesOrig());
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
			if (b == null)
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

	private class UnmodifiableObservableList<E> implements ObservableList<E>
	{
		private Collection<InvalidationListener>			invaliList;
		private Collection<ListChangeListener<? super E>>	changeList;

		private ObservableList<E>							list;

		public UnmodifiableObservableList(ObservableList<E> list)
		{
			this.list = list;

			invaliList = new LinkedList<>();
			changeList = new LinkedList<>();

			this.list.addListener((InvalidationListener)o -> invaliList.forEach(l -> l
					.invalidated(this)));
			this.list.addListener((ListChangeListener)c ->
			{
				if(!changeList.isEmpty())
				{
					Change ch = new Change(c);
					changeList.forEach(lis -> lis.onChanged(ch));
				}
			});

			// FIXME further listchangeevents
		}

		@Override
		public int size()
		{
			return list.size();
		}

		@Override
		public boolean isEmpty()
		{
			return list.isEmpty();
		}

		@Override
		public boolean contains(Object o)
		{
			return list.contains(o);
		}

		@Override
		public Iterator<E> iterator()
		{
			return list.iterator();
		}

		@Override
		public Object[] toArray()
		{
			return list.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a)
		{
			return list.toArray(a);
		}

		@Override
		public boolean add(E e)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c)
		{
			return list.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends E> c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public E get(int index)
		{
			return list.get(index);
		}

		@Override
		public E set(int index, E element)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, E element)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove(int index)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object o)
		{
			return list.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o)
		{
			return list.lastIndexOf(o);
		}

		@Override
		public ListIterator<E> listIterator()
		{
			return list.listIterator();
		}

		@Override
		public ListIterator<E> listIterator(int index)
		{
			return list.listIterator(index);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex)
		{
			return list.subList(fromIndex, toIndex);
		}

		@Override
		public void addListener(InvalidationListener listener)
		{
			invaliList.add(listener);
		}

		@Override
		public void removeListener(InvalidationListener listener)
		{
			invaliList.remove(listener);
		}

		@Override
		public void addListener(ListChangeListener<? super E> listener)
		{
			changeList.add(listener);
		}

		@Override
		public void removeListener(ListChangeListener<? super E> listener)
		{
			changeList.remove(listener);
		}

		@Override
		public boolean addAll(E... elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean setAll(E... elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean setAll(Collection<? extends E> col)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(E... elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(E... elements)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void remove(int from, int to)
		{
			throw new UnsupportedOperationException();
		}

		private class Change extends javafx.collections.ListChangeListener.Change<E>
		{
			private javafx.collections.ListChangeListener.Change<E>	ch;

			public Change(javafx.collections.ListChangeListener.Change<E>	ch)
			{
				super(UnmodifiableObservableList.this);

				this.ch = ch;
			}

			@Override
			public boolean next()
			{
				return ch.next();
			}

			@Override
			public void reset()
			{
				ch.reset();
			}

			@Override
			public int getFrom()
			{
				return ch.getFrom();
			}

			@Override
			public int getTo()
			{
				return ch.getTo();
			}

			@Override
			public List<E> getRemoved()
			{
				return ch.getRemoved();
			}

			@Override
			public int getPermutation(int i)
			{
				return ch.getPermutation(i);
			}

			@Override
			public boolean wasPermutated()
			{
				// TODO Auto-generated method stub
				return ch.wasPermutated();
			}

			@Override
			protected int[] getPermutation()
			{
				return null;
			}

		}
	}
}