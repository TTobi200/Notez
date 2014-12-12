package de.notez.data.base;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import de.notez.data.NotezData;
import de.notez.data.NotezDataProperties;
import de.notez.data.NotezPagedData;
import de.notez.data.NotezPagedDataProperties;
import de.notez.data.NotezStageData;
import de.notez.data.NotezStageDataProperties;
import de.util.NotezNonNullStringProperty;

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
}
