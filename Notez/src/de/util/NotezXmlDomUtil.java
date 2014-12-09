package de.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NotezXmlDomUtil
{
    public static Attr addAttribute(Document doc, Element parent,
                    String attribute, Object value)
    {
        Attr attr = doc.createAttribute(attribute);
        attr.setValue(String.valueOf(value));
        parent.setAttributeNode(attr);

        return attr;
    }

    public static Element addElement(Document doc, Node parent, String element)
    {
        return addElement(doc, parent, element, null);
    }

    public static Element addElement(Document doc, Node parent, String element,
                    Object textValue)
    {
        Element elem = doc.createElement(element);
        if(Objects.nonNull(textValue))
        {
            elem.setTextContent(String.valueOf(textValue));
        }
        parent.appendChild(elem);

        return elem;
    }

    public static List<Element> getElements(Element root, String element)
    {
        NodeList list = root.getElementsByTagName(element);
        List<Element> ret = new ArrayList<>(list.getLength());

        for(int i = 0; i < list.getLength(); i++)
        {
            ret.add((Element)list.item(i));
        }

        return ret;
    }

    public static Element getSingleElement(Element root, String element)
    {
        NodeList list = root.getElementsByTagName(element);

        return isListEmpty(list) ? null : (Element)list.item(0);
    }

    public static boolean isListEmpty(NodeList list)
    {
        return list.getLength() == 0;
    }

    public static DocumentBuilder getDocumentBuilder()
        throws ParserConfigurationException
    {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

    public static double getDoubleAttributeValue(Element element,
                    String attribute)
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

    public static int getIntAttributeValue(Element element, String attribute)
    {
        int ret;

        Attr attr = element.getAttributeNode(attribute);

        try
        {
            ret = Integer.valueOf(attr.getValue());
        }
        catch(NumberFormatException e)
        {
            ret = 0;
        }
        return ret;
    }

    public static String getStringAttributeValue(Element element,
                    String attribute)
    {
        return element.getAttributeNode(attribute).getValue();
    }
}