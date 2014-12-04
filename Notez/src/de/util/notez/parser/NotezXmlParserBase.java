package de.util.notez.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.gui.controller.NotezControllerBase;
import de.util.notez.data.NotezData;
import de.util.notez.data.NotezPagedData;
import de.util.notez.data.NotezStageData;
import de.util.notez.data.NotezTextData;
import de.util.notez.data.base.BaseNotezData;
import de.util.notez.data.base.BaseNotezPagedData;
import de.util.notez.data.base.BaseNotezStageData;
import de.util.notez.data.base.BaseNotezTextData;

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
	public void save(NotezControllerBase<?> controller, File file) throws IOException
	{
		NotezData data = controller.getData();

		try
		{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();

			// ************************************************************************************

			Element root = doc.createElement(NOTEZ_XML_ROOT_ELEMENT);
			doc.appendChild(root);

			Attr version = doc.createAttribute(STRING_VERSION);
			version.setValue(getVersionString());
			root.setAttributeNode(version);

			Element title = doc.createElement(NOTEZ_XML_TITLE_ELEMENT);
			title.setTextContent(data.getTitle());
			root.appendChild(title);

			Element stage = doc.createElement(NOTEZ_XML_STAGE_ELEMENT);
			root.appendChild(stage);

			Attr stageX = doc.createAttribute(NOTEZ_XML_STAGEX_ATTRIBUTE);
			stageX.setValue(String.valueOf(data.getStageData().getStageX()));
			stage.setAttributeNode(stageX);

			Attr stageY = doc.createAttribute(NOTEZ_XML_STAGEY_ATTRIBUTE);
			stageY.setValue(String.valueOf(data.getStageData().getStageY()));
			stage.setAttributeNode(stageY);

			Attr stageWidth = doc.createAttribute(NOTEZ_XML_STAGEWIDTH_ATTRIBUTE);
			stageWidth.setValue(String.valueOf(data.getStageData().getStageWidth()));
			stage.setAttributeNode(stageWidth);

			Attr stageHeight = doc.createAttribute(NOTEZ_XML_STAGEHEIGHT_ATTRIBUTE);
			stageHeight.setValue(String.valueOf(data.getStageData().getStageHeight()));
			stage.setAttributeNode(stageHeight);

			Element pages = doc.createElement(NOTEZ_XML_PAGES_ELEMENT);
			root.appendChild(pages);

			Attr curIndex = doc.createAttribute(NOTEZ_XML_CURINDEX_ATTRIBUTE);
			curIndex.setValue(String.valueOf(data.getPageData().getCurPageIndex()));
			pages.setAttributeNode(curIndex);

			for(NotezTextData textData : data.getPageData().getPages())
			{
				Element page = doc.createElement(NOTEZ_XML_PAGE_ELEMENT);
				page.setTextContent(textData.getText());
				pages.appendChild(page);
			}

			// ************************************************************************************

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");
		}
		catch(Exception e)
		{
			throw new IOException(e);
		}
	}

	public abstract String getVersionString();

	@Override
	protected NotezData parseImpl(File file) throws IOException
	{
		NotezData data;
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			Element root = doc.getDocumentElement();

			// title
			NodeList titles = root.getElementsByTagName(NOTEZ_XML_TITLE_ELEMENT);
			String title = titles.getLength() == 0 ? file.getName() : titles.item(0).getTextContent();
			if (title.isEmpty())
			{
				title = file.getName();
			}

			// stage
			NodeList stages = root.getElementsByTagName(NOTEZ_XML_STAGE_ELEMENT);
			NotezStageData stageData = new BaseNotezStageData();

			if (stages.getLength() != 0)
			{
				Element stage = (Element)stages.item(0);
				stageData.setStageX(getDoubleAttributeValue(stage, NOTEZ_XML_STAGEX_ATTRIBUTE));
				stageData.setStageY(getDoubleAttributeValue(stage, NOTEZ_XML_STAGEY_ATTRIBUTE));
				stageData.setStageWidth(getDoubleAttributeValue(stage,
					NOTEZ_XML_STAGEWIDTH_ATTRIBUTE));
				stageData.setStageHeight(getDoubleAttributeValue(stage,
					NOTEZ_XML_STAGEHEIGHT_ATTRIBUTE));
			}

			NodeList pages = root.getElementsByTagName(NOTEZ_XML_PAGES_ELEMENT);
			NotezPagedData pageData = new BaseNotezPagedData();

			if (pages.getLength() != 0)
			{
				NodeList page = ((Element)pages.item(0))
						.getElementsByTagName(NOTEZ_XML_PAGE_ELEMENT);

				if (page.getLength() != 0)
				{
					pageData.setText((page.item(0).getTextContent()));
					for(int i = 1; i < page.getLength(); i++)
					{
						pageData.addPages(new BaseNotezTextData(page.item(i).getTextContent()));
					}
				}

				int curIndex;
				try
				{
					curIndex = Integer.parseInt(((Element)pages.item(0))
							.getAttribute(NOTEZ_XML_CURINDEX_ATTRIBUTE));
				}
				catch(NumberFormatException e)
				{
					curIndex = 0;
				}

				pageData.setCurPageIndex(curIndex);
			}

			data = new BaseNotezData(title, stageData, pageData);
		}
		catch(Exception e)
		{
			data = null;
		}

		return data;
	}

	private double getDoubleAttributeValue(Element element, String attribute)
	{
		double ret;

		Attr attr = element.getAttributeNode(attribute);

		try
		{
			ret = Double.valueOf(attr.getValue());
		}
		catch(NumberFormatException e)
		{
			ret = 0d;
		}
		return ret;
	}
}
