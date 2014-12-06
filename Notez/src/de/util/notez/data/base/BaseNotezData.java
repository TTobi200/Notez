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
 * A basic implementation of the {@link NotezData}-interface.<br>
 * This class also provides the ability to transform any notezdata into a
 * serializable pendant via {@link #asSerializableData(NotezData)}
 *
 * @author ddd
 */
public class BaseNotezData implements NotezData
{
    /** The title of this data */
    protected String title;
    /** The stagedata of this data */
    protected NotezStageData stageData;
    /** The pagedata of this data */
    protected NotezPagedData pageData;

    public BaseNotezData()
    {
        // TODO Auto-generated constructor stub
    }

    public BaseNotezData(String title, NotezStageData stageData,
                         NotezPagedData pageData)
    {
        super();

        this.title = title;
        this.stageData = stageData == null ? new BaseNotezStageData()
                        : stageData;
        this.pageData = pageData == null ? new BaseNotezPagedData() : pageData;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void setTitle(String title)
    {
        this.title = title == null ? "" : title;
    }

    @Override
    public NotezStageData getStageData()
    {
        return stageData;
    }

    @Override
    public NotezPagedData getPageData()
    {
        return pageData;
    }

    /**
     * @return a serializable version of this data
     */
    public NotezData asSerializableData()
    {
        return asSerializableData(this);
    }

    /**
     * @param data
     *            The data to be serialized
     * @return a serializable version of the given data
     */
    public static NotezData asSerializableData(NotezData data)
    {
        return new SerializableNotezData(Objects.requireNonNull(data));
    }

    /**
     * A serializable voerion of {@link BaseNotezData}
     *
     * @author ddd
     */
    private static final class SerializableNotezData extends BaseNotezData
                    implements Serializable
    {
        private static final long serialVersionUID = 1L;

        public SerializableNotezData(NotezData data)
        {
            super(data.getTitle(),
                new SerializableNotezStageData(
                    Objects.requireNonNull(data.getStageData())),
                new SerializableNotezPagedData(
                    Objects.requireNonNull(data.getPageData())));
        }

        /**
         * A serializable version of {@link BaseNotezPagedData}
         *
         * @author ddd
         */
        private static final class SerializableNotezPagedData extends
                        BaseNotezPagedData implements
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
                super.addPages(Arrays.stream(pages)
                    .map(page -> new SerializableNotezTextData(page))
                    .toArray(NotezTextData[]::new));
            }

            /**
             * A serializable version of {@link BaseNotezTextData}
             *
             * @author ddd
             */
            private static final class SerializableNotezTextData extends
                            BaseNotezTextData
                            implements Serializable
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
        private static final class SerializableNotezStageData extends
                        BaseNotezStageData implements
                        Serializable
        {
            private static final long serialVersionUID = SerializableNotezData.serialVersionUID;

            public SerializableNotezStageData(NotezStageData stageData)
            {
                super(stageData.getStageX(), stageData.getStageY(),
                    stageData.getStageWidth(),
                    stageData.getStageHeight());
            }
        }
    }
}
