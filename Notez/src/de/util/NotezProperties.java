package de.util;

import static de.util.NotezXmlDomUtil.addAttribute;
import static de.util.NotezXmlDomUtil.addElement;
import static de.util.NotezXmlDomUtil.getDocumentBuilder;
import static de.util.NotezXmlDomUtil.getSingleElement;
import static de.util.NotezXmlDomUtil.getStringAttributeValue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.util.NotezRemoteSync.NotezRemoteUser;

public class NotezProperties
{
    public static final String PROP_FILE = "./Notez.properties";

    public static final String NOTEZ_XML_FILE_START = "<?xml";

    public static final String NOTEZ_XML_ROOT_ELEMENT = "Notez-Properties";

    public static final String NOTEZ_XML_FOLDER_ELEMENT = "Folder";
    public static final String NOTEZ_XML_BUTTONS_ELEMENT = "Button";
    public static final String NOTEZ_XML_SYNC_ELEMENT = "Synchronisation";
    public static final String NOTEZ_XML_SHARE_USER_ELEMENT = "ShareUser";

    public static final String NOTEZ_REMOTE_FOLDER = "NotezRemoteFolder";
    public static final String NOTEZ_WORK_FOLDER = "NotezWorkFolder";
    public static final String NOTEZ_ALWAYS_SAVE_ON_EXIT = "AlwaysSaveOnExit";

    public static final String NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY = "OpenReceivedNotezDirectly";
    public static final String NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ = "ShowMessageOnNewNotez";
    public static final String NOTEZ_LET_RECEIVER_RUNNING = "LetReceiverRunning";
    public static final String NOTEZ_RECEIVER_ON_STARTUP = "ReceiverOnStartup";

    public static final String NOTEZ_XML_Group_ATTRIBUTE = "Group";
    public static final String NOTEZ_XML_Save_ATTRIBUTE = "Save";
    public static final String NOTEZ_XML_Remove_ATTRIBUTE = "Remove";
    public static final String NOTEZ_XML_Share_ATTRIBUTE = "Share";
    public static final String NOTEZ_XML_Add_ATTRIBUTE = "Add";
    public static final String NOTEZ_XML_Print_ATTRIBUTE = "Print";
    public static final String NOTEZ_XML_Pin_ATTRIBUTE = "Pin";

    private static Map<String, String> properties;

    static
    {
        properties = new HashMap<>(15);
        try
        {
            load();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private NotezProperties()
    {
    }

    public static void save()
        throws IOException
    {
        try
        {
            Document doc = getDocumentBuilder().newDocument();

            Element root = addElement(doc, doc, NOTEZ_XML_ROOT_ELEMENT);
            saveFolderSettings(doc, root);
            saveButtonSettings(doc, root);
            saveSyncSettings(doc, root);
            saveShareUserSettings(doc, root);

            saveFile(doc, new File(PROP_FILE));
            System.out.println("Properties saved!");
        }
        catch(Exception e)
        {
            throw new IOException(e);
        }
    }

    protected static void saveFolderSettings(Document doc, Element root)
    {
        Element folder = addElement(doc, root, NOTEZ_XML_FOLDER_ELEMENT);

        addAttr(doc, folder, NOTEZ_REMOTE_FOLDER);
        addAttr(doc, folder, NOTEZ_WORK_FOLDER);
        addAttr(doc, folder, NOTEZ_ALWAYS_SAVE_ON_EXIT);
    }

    protected static void saveButtonSettings(Document doc, Element root)
    {
        Element button = addElement(doc, root, NOTEZ_XML_BUTTONS_ELEMENT);

        addAttr(doc, button, NOTEZ_XML_Group_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Save_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Remove_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Share_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Add_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Print_ATTRIBUTE);
        addAttr(doc, button, NOTEZ_XML_Pin_ATTRIBUTE);
    }

    protected static void saveSyncSettings(Document doc, Element root)
    {
        Element sync = addElement(doc, root, NOTEZ_XML_SYNC_ELEMENT);

        addAttr(doc, sync, NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY);
        addAttr(doc, sync, NOTEZ_LET_RECEIVER_RUNNING);
        addAttr(doc, sync, NOTEZ_RECEIVER_ON_STARTUP);
    }

    protected static void saveShareUserSettings(Document doc, Element root)
    {
        Element shareUser = addElement(doc, root, NOTEZ_XML_SHARE_USER_ELEMENT);

        for(NotezRemoteUser user : NotezRemoteSync.getAllUsers())
        {
            Attr uAttr = addAttr(doc, shareUser, user.getUsername());
            uAttr.setNodeValue(String.valueOf(user.getShare()));
        }
    }

    protected static void saveFile(Document doc, File file)
        throws TransformerException
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
            "{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);
    }

    public static void load() throws IOException
    {
        try
        {
            File file = new File(PROP_FILE);

            if(!file.exists())
            {
                System.out.println("Can't find properties-file "
                                   + PROP_FILE);
                return;
            }

            Element root = getDocumentBuilder().parse(file)
                .getDocumentElement();

            loadFolderSettings(root);
            loadButtonSettings(root);
            loadSyncSettings(root);
            loadUserSettings(root);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    protected static void loadFolderSettings(Element root)
    {
        Element folder = getSingleElement(root,
            NOTEZ_XML_FOLDER_ELEMENT);

        if(Objects.nonNull(folder))
        {
            storeProp(folder,
                NOTEZ_REMOTE_FOLDER);
            storeProp(folder,
                NOTEZ_WORK_FOLDER);
            storeProp(folder,
                NOTEZ_ALWAYS_SAVE_ON_EXIT);
        }
    }

    protected static void loadButtonSettings(Element root)
    {
        Element buttons = getSingleElement(root,
            NOTEZ_XML_BUTTONS_ELEMENT);

        if(Objects.nonNull(buttons))
        {
            storeProp(buttons,
                NOTEZ_XML_Group_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Save_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Remove_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Share_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Add_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Print_ATTRIBUTE);
            storeProp(buttons,
                NOTEZ_XML_Pin_ATTRIBUTE);
        }
    }

    protected static void loadSyncSettings(Element root)
    {
        Element sync = getSingleElement(root,
            NOTEZ_XML_SYNC_ELEMENT);

        if(Objects.nonNull(sync))
        {
            storeProp(sync,
                NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY);
            storeProp(sync,
                NOTEZ_LET_RECEIVER_RUNNING);
            storeProp(sync,
                NOTEZ_RECEIVER_ON_STARTUP);
        }
    }

    protected static void loadUserSettings(Element root)
    {
        Element shareUser = getSingleElement(root,
            NOTEZ_XML_SHARE_USER_ELEMENT);

        if(Objects.nonNull(shareUser))
        {
            NamedNodeMap user = shareUser.getAttributes();
            for(int i = 0; i < user.getLength(); i++)
            {
                Node usrNode = user.item(i);
                NotezRemoteSync.addUser(new NotezRemoteUser(
                    usrNode.getNodeName(), usrNode.getNodeValue()));
            }
        }
    }

    protected static void storeProp(Element parent, String attribute)
    {
        properties.put(attribute, getStringAttributeValue(parent,
            attribute));
    }

    protected static Attr addAttr(Document doc, Element parent, String attribute)
    {
        return addAttribute(doc, parent, attribute,
            properties.get(attribute));
    }

    public static boolean contains(String key)
    {
        return properties.containsKey(key);
    }

    public static boolean getBoolean(String key)
    {
        return Boolean.valueOf(get(key));
    }

    public static String get(String key)
    {
        return String.valueOf(properties.get(key));
    }

    public static void setBoolean(String key, boolean bool)
    {
        properties.put(key, String.valueOf(bool));
    }

    public static String get(String key, String defaultValue)
    {
        String val = properties.get(key);
        return val != null ? val : defaultValue;
    }

    public static void set(String key, String value)
    {
        if(value == null)
        {
            value = properties.get(key);
        }
        properties.put(key, value);
    }

    public static Map<String, String> getAll()
    {
        return properties;
    }
}