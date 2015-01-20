package de.notez.parser;

import static de.util.NotezXmlDomUtil.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import de.notez.NotezNote;
import de.notez.data.*;
import de.notez.data.base.*;
import de.util.NotezFileUtil;

public abstract class NotezXmlParserBase extends NotezParserBase
{
    public static final String NOTEZ_XML_FILE_START = "<?xml";

    public static final String NOTEZ_XML_ROOT_ELEMENT = "notez";

    public static final String NOTEZ_XML_TITLE_ELEMENT = "title";
    public static final String NOTEZ_XML_STAGE_ELEMENT = "stage";
    public static final String NOTEZ_XML_PAGES_ELEMENT = "pages";
    public static final String NOTEZ_XML_PAGE_ELEMENT = "page";

    public static final String NOTEZ_XML_STAGEX_ATTRIBUTE = "stagex";
    public static final String NOTEZ_XML_STAGEY_ATTRIBUTE = "stagey";
    public static final String NOTEZ_XML_STAGEWIDTH_ATTRIBUTE = "stagewidth";
    public static final String NOTEZ_XML_STAGEHEIGHT_ATTRIBUTE = "stageheight";
    public static final String NOTEZ_XML_CURINDEX_ATTRIBUTE = "curindex";

    public NotezXmlParserBase()
    {
        super();
    }

    @Override
    public void save(NotezNote note, File file)
        throws IOException
    {
        NotezData data = note.getData();

        try
        {
            Document doc = getDocumentBuilder().newDocument();

            Element root = addElement(doc, doc, NOTEZ_XML_ROOT_ELEMENT);

            addAttribute(doc, root, STRING_VERSION, getVersionString());

            saveTitle(doc, root, data.getTitle());

            saveStageData(doc, root, data.getStageData());

            savePagedData(doc, root, data.getPageData());

            saveFile(doc, file);

            System.out.println("File saved!");
        }
        catch(Exception e)
        {
            throw new IOException(e);
        }
    }

    protected void saveTitle(Document doc, Element root, String title)
    {
        addElement(doc, root, NOTEZ_XML_TITLE_ELEMENT, title);
    }

    protected void saveStageData(Document doc, Element root,
                    NotezStageData stageData)
    {
        Element stage = addElement(doc, root, NOTEZ_XML_STAGE_ELEMENT);

        addAttribute(doc, stage, NOTEZ_XML_STAGEX_ATTRIBUTE,
            stageData.getStageX());
        addAttribute(doc, stage, NOTEZ_XML_STAGEY_ATTRIBUTE,
            stageData.getStageY());
        addAttribute(doc, stage, NOTEZ_XML_STAGEWIDTH_ATTRIBUTE,
            stageData.getStageWidth());
        addAttribute(doc, stage, NOTEZ_XML_STAGEHEIGHT_ATTRIBUTE,
            stageData.getStageHeight());
    }

    protected void savePagedData(Document doc, Element root,
                    NotezPagedData pageData)
    {
        Element pages = addElement(doc, root, NOTEZ_XML_PAGES_ELEMENT);

        addAttribute(doc, pages, NOTEZ_XML_CURINDEX_ATTRIBUTE,
            pageData.getCurPageIndex());

        pageData.getPages().forEach(
            page -> addElement(doc, pages, NOTEZ_XML_PAGE_ELEMENT,
                page.getText()));
    }

    protected void saveFile(Document doc, File file)
        throws TransformerException
    {
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
        // "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
            "{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);

        // Output to console for testing
        // StreamResult res = new StreamResult(System.out);

        transformer.transform(source, result);
        // transformer.transform(source, res);
    }

    public abstract String getVersionString();

    @Override
    protected NotezData parseImpl(File file) throws IOException
    {
        NotezData data;
        try
        {
            Element root = getDocumentBuilder().parse(file)
                .getDocumentElement();

            String title = loadTitle(root);

            NotezStageData stageData = loadStageData(root);

            NotezPagedData pageData = loadPagedData(root);

            data = new BaseNotezData(title, stageData, pageData);
        }
        catch(Exception e)
        {
            data = null;
        }

        return data;
    }

    protected String loadTitle(Element root)
    {
        Element title = getSingleElement(root, NOTEZ_XML_TITLE_ELEMENT);

        return Objects.isNull(title) ? NotezFileUtil.removeEnding(getFile().getName())
                        : title.getTextContent();
    }

    protected NotezStageData loadStageData(Element root)
    {
        Element stage = getSingleElement(root, NOTEZ_XML_STAGE_ELEMENT);
        NotezStageData stageData = new BaseNotezStageData();

        if(Objects.nonNull(stage))
        {
            stageData.setStageX(getDoubleAttributeValue(stage,
                NOTEZ_XML_STAGEX_ATTRIBUTE));
            stageData.setStageY(getDoubleAttributeValue(stage,
                NOTEZ_XML_STAGEY_ATTRIBUTE));
            stageData.setStageWidth(getDoubleAttributeValue(stage,
                NOTEZ_XML_STAGEWIDTH_ATTRIBUTE));
            stageData
                .setStageHeight(getDoubleAttributeValue(stage,
                    NOTEZ_XML_STAGEHEIGHT_ATTRIBUTE));
        }

        return stageData;
    }

    protected NotezPagedData loadPagedData(Element root)
    {
        Element pages = getSingleElement(root, NOTEZ_XML_PAGES_ELEMENT);
        NotezPagedData pageData = new BaseNotezPagedData();

        if(Objects.nonNull(pages))
        {
            List<Element> pageList = getElements(pages, NOTEZ_XML_PAGE_ELEMENT);

            if(!pageList.isEmpty())
            {
                pageData.setPages(pageList.stream()
                    .map(page -> new BaseNotezTextData(page.getTextContent()))
                    .collect(Collectors.toList()));
            }

            int curIndex = getIntAttributeValue(pages,
                NOTEZ_XML_CURINDEX_ATTRIBUTE);

            pageData.setCurPageIndex(curIndex);
        }

        return pageData;
    }
}
