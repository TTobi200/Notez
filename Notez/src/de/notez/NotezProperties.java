/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.notez;

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

import javafx.beans.property.BooleanProperty;

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
import org.w3c.dom.NodeList;

import de.notez.NotezRemoteSync.NotezRemoteUser;

public class NotezProperties
{
    public static final String PROP_FILE = "./Notez.properties";

    public static final String NOTEZ_XML_FILE_START = "<?xml";

    public static final String NOTEZ_XML_ROOT_ELEMENT = "Notez-Properties";

    public static final String NOTEZ_XML_FOLDER_ELEMENT = "Folder";
    public static final String NOTEZ_XML_BUTTONS_ELEMENT = "Button";
    public static final String NOTEZ_XML_SYNC_ELEMENT = "Synchronisation";
    public static final String NOTEZ_XML_SHARE_USER_ELEMENT = "ShareUser";
    public static final String NOTEZ_XML_EMAIL_SETTINGS_ELEMENT = "EMailSettings";

    public static final String NOTEZ_XML_USER_ELEMENT = "User";

    public static final String NOTEZ_REMOTE_FOLDER = "NotezRemoteFolder";
    public static final String NOTEZ_WORK_FOLDER = "NotezWorkFolder";
    public static final String NOTEZ_ALWAYS_SAVE_ON_EXIT = "AlwaysSaveOnExit";

    public static final String NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY = "OpenReceivedNotezDirectly";
    public static final String NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ = "ShowMessageOnNewNotez";
    public static final String NOTEZ_LET_RECEIVER_RUNNING = "LetReceiverRunning";
    public static final String NOTEZ_RECEIVER_ON_STARTUP = "ReceiverOnStartup";

    public static final String NOTEZ_BTN_GROUP = "Group";
    public static final String NOTEZ_BTN_SAVE = "Save";
    public static final String NOTEZ_BTN_REMOVE = "Remove";
    public static final String NOTEZ_BTN_SHARE = "Share";
    public static final String NOTEZ_BTN_ADD = "Add";
    public static final String NOTEZ_BTN_PRINT = "Print";
    public static final String NOTEZ_BTN_PIN = "Pin";

    public static final String NOTEZ_REMOTE_USERNAME = "Username";
    public static final String NOTEZ_REMOTE_SHARE_ATTRIBUTE = "Share";

    public static final String NOTEZ_MAIL_HOST = "Hostname";
    public static final String NOTEZ_MAIL_USER = "Username";
    public static final String NOTEZ_MAIL_PORT = "Port";
    public static final String NOTEZ_MAIL_USE_SSL = "UseSSL";

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
            saveEMailSettings(doc, root);

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

        addAttr(doc, button, NOTEZ_BTN_GROUP);
        addAttr(doc, button, NOTEZ_BTN_SAVE);
        addAttr(doc, button, NOTEZ_BTN_REMOVE);
        addAttr(doc, button, NOTEZ_BTN_SHARE);
        addAttr(doc, button, NOTEZ_BTN_ADD);
        addAttr(doc, button, NOTEZ_BTN_PRINT);
        addAttr(doc, button, NOTEZ_BTN_PIN);
    }

    protected static void saveSyncSettings(Document doc, Element root)
    {
        Element sync = addElement(doc, root, NOTEZ_XML_SYNC_ELEMENT);

        addAttr(doc, sync, NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY);
        addAttr(doc, sync, NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ);
        addAttr(doc, sync, NOTEZ_LET_RECEIVER_RUNNING);
        addAttr(doc, sync, NOTEZ_RECEIVER_ON_STARTUP);
    }

    protected static void saveShareUserSettings(Document doc, Element root)
    {
        Element shareUser = addElement(doc, root, NOTEZ_XML_SHARE_USER_ELEMENT);

        for(NotezRemoteUser user : NotezRemoteSync.getAllUsers())
        {
            Element u = addElement(doc, shareUser, NOTEZ_XML_USER_ELEMENT);
            u.setAttribute(NOTEZ_REMOTE_SHARE_ATTRIBUTE,
                String.valueOf(user.getShare()));
            u.setAttribute(NOTEZ_REMOTE_USERNAME,
                user.getUsername());
        }
    }

    protected static void saveEMailSettings(Document doc, Element root)
    {
        Element email = addElement(doc, root, NOTEZ_XML_EMAIL_SETTINGS_ELEMENT);

        addAttr(doc, email, NOTEZ_MAIL_HOST);
        addAttr(doc, email, NOTEZ_MAIL_USER);
        addAttr(doc, email, NOTEZ_MAIL_PORT);
        addAttr(doc, email, NOTEZ_MAIL_USE_SSL);
    }

    protected static void saveFile(Document doc, File file)
        throws TransformerException
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
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
            loadEMailSettings(root);
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
                NOTEZ_BTN_GROUP);
            storeProp(buttons,
                NOTEZ_BTN_SAVE);
            storeProp(buttons,
                NOTEZ_BTN_REMOVE);
            storeProp(buttons,
                NOTEZ_BTN_SHARE);
            storeProp(buttons,
                NOTEZ_BTN_ADD);
            storeProp(buttons,
                NOTEZ_BTN_PRINT);
            storeProp(buttons,
                NOTEZ_BTN_PIN);
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
                NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ);
            storeProp(sync,
                NOTEZ_LET_RECEIVER_RUNNING);
            storeProp(sync,
                NOTEZ_RECEIVER_ON_STARTUP);
        }
    }

    protected static void loadUserSettings(Element root)
    {
        Element shareUserElement = getSingleElement(root,
            NOTEZ_XML_SHARE_USER_ELEMENT);

        if(Objects.nonNull(shareUserElement))
        {
            NodeList shareUsers = shareUserElement.getElementsByTagName(
                NOTEZ_XML_USER_ELEMENT);

            if(Objects.nonNull(shareUsers))
            {
                for(int i = 0; i < shareUsers.getLength(); i++)
                {
                    NamedNodeMap attrs = shareUsers.item(i).getAttributes();

                    NotezRemoteSync.addUser(new NotezRemoteUser(
                        String.valueOf(attrs.getNamedItem(
                            NOTEZ_REMOTE_USERNAME).getNodeValue()),
                        String.valueOf(attrs.getNamedItem(
                            NOTEZ_REMOTE_SHARE_ATTRIBUTE).getNodeValue())));
                }
            }
        }
    }

    protected static void loadEMailSettings(Element root)
    {
        Element email = getSingleElement(root,
            NOTEZ_XML_EMAIL_SETTINGS_ELEMENT);

        if(Objects.nonNull(email))
        {
            storeProp(email,
                NOTEZ_MAIL_HOST);
            storeProp(email,
                NOTEZ_MAIL_USER);
            storeProp(email,
                NOTEZ_MAIL_PORT);
            storeProp(email,
                NOTEZ_MAIL_USE_SSL);
        }
    }

    protected static void storeProp(Element parent, String attribute)
    {
        String value = getStringAttributeValue(parent,
            attribute);

        if(value != null)
        {
            properties.put(attribute, value);
        }
        else
        {
            System.out.println("No value for: " + attribute);
        }
    }

    protected static Attr addAttr(Document doc, Element parent, String attribute)
    {
        String attr = properties.get(attribute);

        if(attr != null)
        {
            return addAttribute(doc, parent, attribute,
                attr);
        }
        else
        {
            System.out.println("Not saved attribute for: " + attribute);
            return null;
        }
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
        return properties.get(key);
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

    public static void bindBoolean(String attribute,
                    BooleanProperty property)
    {
        // If set before refresh the property
        if(contains(attribute))
        {
            property.set(getBoolean(attribute));
        }

        property.addListener((o, a, n) ->
        {
            // Change setting if property changed
            set(attribute,
                String.valueOf(property.get()));
        });
    }
}