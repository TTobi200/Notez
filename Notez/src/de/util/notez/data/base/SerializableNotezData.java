package de.util.notez.data.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import de.util.notez.data.NotezData;
import de.util.notez.data.NotezPagedData;
import de.util.notez.data.NotezStageData;
import de.util.notez.data.NotezTextData;

/**
 * A serializable Version of the {@link NotezData}-interface
 *
 * @author ddd
 */
public class SerializableNotezData extends BaseNotezData implements Serializable
{
	private static final long serialVersionUID = 1L;

	public SerializableNotezData(NotezData data)
	{
		super(data.getTitle(), new SerializableNotezStageData(Objects.requireNonNull(data
				.getStageData())), new SerializableNotezPagedData(Objects.requireNonNull(data
				.getPageData())));
	}

	/**
	 * A serializable version of {@link BaseNotezPagedData}
	 *
	 * @author ddd
	 */
	private static final class SerializableNotezPagedData extends BaseNotezPagedData implements
			Serializable
	{
		private static final long serialVersionUID = SerializableNotezData.serialVersionUID;

		public SerializableNotezPagedData(NotezPagedData pagedData)
		{
			super(new ArrayList<>());

			pagedData.getPages().forEach(this::addPages);
		}

		@Override
		public void addPages(NotezTextData... pages)
		{
			super.addPages(Arrays.stream(pages).map(page -> new SerializableNotezTextData(page))
					.toArray(NotezTextData[]::new));
		}

		/**
		 * A serializable version of {@link BaseNotezTextData}
		 *
		 * @author ddd
		 */
		private static final class SerializableNotezTextData extends BaseNotezTextData implements
				Serializable
		{
			private static final long serialVersionUID = SerializableNotezData.serialVersionUID;

			public SerializableNotezTextData(NotezTextData textData)
			{
				super(textData.getText());
			}
		}
	}

	/**
	 * A serializable version of {@link BaseNotezStageData}
	 *
	 * @author ddd
	 */
	private static final class SerializableNotezStageData extends BaseNotezStageData implements
			Serializable
	{
		private static final long serialVersionUID = SerializableNotezData.serialVersionUID;

		public SerializableNotezStageData(NotezStageData stageData)
		{
			super(stageData.getStageX(), stageData.getStageY(), stageData.getStageWidth(),
					stageData.getStageHeight());
		}
	}
}
