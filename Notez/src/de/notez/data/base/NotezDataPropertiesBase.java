package de.notez.data.base;

import javafx.beans.property.*;
import de.notez.data.*;
import de.util.*;

/**
 * a basis for implementations of the {@link NotezDataProperties}-interface
 *
 * @author ddd
 */
public abstract class NotezDataPropertiesBase implements NotezDataProperties
{
	/** property holding the title */
	protected StringProperty title;

	protected NotezDataPropertiesBase()
	{
		this((String)null);
	}

	protected NotezDataPropertiesBase(NotezData data)
	{
		this(data.getTitle(), data.getStageData(), data.getPageData());
	}

	protected NotezDataPropertiesBase(String title, NotezStageData stageData, NotezPagedData pageData)
	{
		this(title);

		stageDataPropertyModifiable().set(stageData instanceof NotezStageDataProperties ?
				(NotezStageDataProperties)stageData :
					new BaseNotezStageDataProperties(stageData));
		pageDataPropertyModifiable().set(pageData instanceof NotezPagedDataProperties ?
			(NotezPagedDataProperties)pageData :
				new BaseNotezPagedDataProperties(pageData));
	}

	protected NotezDataPropertiesBase(String title)
	{
		this.title = new NotezNonNullStringProperty(title);
	}

	@Override
	public ReadOnlyObjectProperty<NotezPagedDataProperties> pageDataProperty()
	{
		return pageDataPropertyModifiable().getReadOnlyProperty();
	}

	@Override
	public ReadOnlyObjectProperty<NotezStageDataProperties> stageDataProperty()
	{
		return stageDataPropertyModifiable().getReadOnlyProperty();
	}

	@Override
	public StringProperty titleProperty()
	{
		return title;
	}

	/**
	 * @return the modifiable property for the pageData
	 */
	protected abstract ReadOnlyObjectWrapper<NotezPagedDataProperties> pageDataPropertyModifiable();

	/**
	 * @return the modifiable property for the stageData
	 */
	protected abstract ReadOnlyObjectWrapper<NotezStageDataProperties> stageDataPropertyModifiable();
	
	@Override
	public String toString()
	{
		return NotezDataUtil.toString(this);
	}
}
