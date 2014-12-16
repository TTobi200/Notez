package de.notez.data.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import de.notez.data.NotezData;
import de.notez.data.NotezPagedData;
import de.notez.data.NotezStageData;
import de.notez.data.NotezTextData;
import de.notez.network.NotezRemoteObject;

/**
 * A serializable Version of the {@link NotezData}-interface
 *
 * @author ddd
 */
public class SerializableNotezData extends BaseNotezData implements Externalizable, NotezRemoteObject
{
	private static final long serialVersionUID = 1L;
	
	public SerializableNotezData()
	{
	}

	public SerializableNotezData(NotezData data)
	{
		super(data.getTitle(), new SerializableNotezStageData(Objects.requireNonNull(data
				.getStageData())), new SerializableNotezPagedData(Objects.requireNonNull(data
				.getPageData())));
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeBytes(title);
		out.writeObject(stageData);
		out.writeObject(pageData);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
		ClassNotFoundException
	{
		title = in.readLine();
		stageData = (NotezStageData)in.readObject();
		pageData = (NotezPagedData)in.readObject();
	}

	/**
	 * A serializable version of {@link BaseNotezPagedData}
	 *
	 * @author ddd
	 */
	private static final class SerializableNotezPagedData extends BaseNotezPagedData implements
			Externalizable
	{
		private static final long serialVersionUID = SerializableNotezData.serialVersionUID;
		
		public SerializableNotezPagedData()
		{
		}

		public SerializableNotezPagedData(NotezPagedData pagedData)
		{
			super(new ArrayList<>());

			setPages(pagedData.getPages().stream().map(page -> new SerializableNotezTextData(page)).collect(Collectors.toList()));
			
			setCurPageIndex(pagedData.getCurPageIndex());
		}
		
		@Override
		public void writeExternal(ObjectOutput out) throws IOException
		{
			out.writeInt(getPages().size());
			
			for(NotezTextData page : getPages())
			{
				out.writeObject(page);
			}
			
			out.writeInt(getCurPageIndex());
		}

		@Override
		public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
		{
			int size = in.readInt();
			
			getCurPage().setText(((NotezTextData)in.readObject()).getText());
			
			for(int i = 1; i < size; i++)
			{
				addPages((NotezTextData)in.readObject());
			}
			
			setCurPageIndex(in.readInt());
		}

		/**
		 * A serializable version of {@link BaseNotezTextData}
		 *
		 * @author ddd
		 */
		private static final class SerializableNotezTextData extends BaseNotezTextData implements
				Externalizable, NotezTextData
		{
			private static final long serialVersionUID = SerializableNotezData.serialVersionUID;

			public SerializableNotezTextData()
			{
			}
			
			public SerializableNotezTextData(NotezTextData textData)
			{
				super(textData.getText());
			}

			@Override
			public void writeExternal(ObjectOutput out) throws IOException
			{
				out.writeBytes(getText());
			}

			@Override
			public void readExternal(ObjectInput in) throws IOException,
				ClassNotFoundException
			{
				setText(in.readLine());
			}
			
			
		}
	}

	/**
	 * A serializable version of {@link BaseNotezStageData}
	 *
	 * @author ddd
	 */
	private static final class SerializableNotezStageData extends BaseNotezStageData implements
			Externalizable
	{
		private static final long serialVersionUID = SerializableNotezData.serialVersionUID;
		
		public SerializableNotezStageData()
		{
		}

		public SerializableNotezStageData(NotezStageData stageData)
		{
			super(stageData.getStageX(), stageData.getStageY(), stageData.getStageWidth(),
					stageData.getStageHeight());
		}

		@Override
		public void writeExternal(ObjectOutput out) throws IOException
		{
			out.writeDouble(getStageX());
			out.writeDouble(getStageY());
			out.writeDouble(getStageWidth());
			out.writeDouble(getStageHeight());
		}

		@Override
		public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
		{
			setStageX(in.readDouble());
			setStageY(in.readDouble());
			setStageWidth(in.readDouble());
			setStageHeight(in.readDouble());
		}
	}
}
