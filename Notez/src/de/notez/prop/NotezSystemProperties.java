/*
 * Copyright © 2015 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.notez.prop;

import static de.util.NotezXmlDomUtil.*;

import java.io.File;
import java.util.Objects;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import de.notez.*;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.util.log.NotezLog;

public class NotezSystemProperties extends NotezFinalProperties
{
	public static final String PROP_FILE = "./Notez.properties";
	private static NotezSystemProperties systemProperties = new NotezSystemProperties();

	public static NotezSystemProperties getSystemProperties()
	{
		return systemProperties;
	}

	public static void setSystemProperties(NotezSystemProperties systemProperties)
	{
		if(Objects.nonNull(systemProperties))
		{
			NotezSystemProperties.systemProperties = systemProperties;
		}
	}

	protected NotezSystemProperties()
	{
		try
		{
			File file = new File(PROP_FILE);

			if(!file.exists())
			{
				System.out.println("Can't find properties-file " + PROP_FILE);
				return;
			}

			Element root = getDocumentBuilder().parse(file).getDocumentElement();

			loadFolderSettings(root);
			loadButtonSettings(root);
			loadSyncSettings(root);
			loadUserSettings(root);
			loadEMailSettings(root);
		}
		catch(Exception e)
		{
			NotezLog.error("", e);
		}
	}

	protected void loadFolderSettings(Element root)
	{
		Element folder = getSingleElement(root, NOTEZ_XML_FOLDER_ELEMENT);

		if(Objects.nonNull(folder))
		{
			storeProp(folder, NOTEZ_REMOTE_FOLDER, DEF_REMOTE_NOTEZ_FOLDER);
			storeProp(folder, NOTEZ_WORK_FOLDER, DEF_LOCAL_NOTEZ_FOLDER);
			storePropBoolean(folder, NOTEZ_ALWAYS_SAVE_ON_EXIT);
		}
	}

	protected void loadButtonSettings(Element root)
	{
		Element buttons = getSingleElement(root, NOTEZ_XML_BUTTONS_ELEMENT);

		if(Objects.nonNull(buttons))
		{
			storePropBoolean(buttons, NOTEZ_BTN_GROUP);
			storePropBoolean(buttons, NOTEZ_BTN_SAVE);
			storePropBoolean(buttons, NOTEZ_BTN_REMOVE);
			storePropBoolean(buttons, NOTEZ_BTN_SHARE);
			storePropBoolean(buttons, NOTEZ_BTN_ADD);
			storePropBoolean(buttons, NOTEZ_BTN_PRINT);
			storePropBoolean(buttons, NOTEZ_BTN_PIN);
		}
	}

	protected void loadSyncSettings(Element root)
	{
		Element sync = getSingleElement(root, NOTEZ_XML_SYNC_ELEMENT);

		if(Objects.nonNull(sync))
		{
			storePropBoolean(sync, NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY);
			storePropBoolean(sync, NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ);
			storePropBoolean(sync, NOTEZ_LET_RECEIVER_RUNNING);
			storePropBoolean(sync, NOTEZ_RECEIVER_ON_STARTUP);
		}
	}

	protected void loadUserSettings(Element root)
	{
		Element shareUserElement = getSingleElement(root, NOTEZ_XML_SHARE_USER_ELEMENT);

		if(Objects.nonNull(shareUserElement))
		{
			NodeList shareUsers = shareUserElement.getElementsByTagName(NOTEZ_XML_USER_ELEMENT);

			if(Objects.nonNull(shareUsers))
			{
				for(int i = 0; i < shareUsers.getLength(); i++)
				{
					NamedNodeMap attrs = shareUsers.item(i).getAttributes();

					NotezRemoteSync.addUser(new NotezRemoteUser(String.valueOf(attrs.getNamedItem(
						NOTEZ_REMOTE_USERNAME).getNodeValue()), String.valueOf(attrs.getNamedItem(
						NOTEZ_REMOTE_SHARE_ATTRIBUTE).getNodeValue())));
				}
			}
		}
	}

	protected void loadEMailSettings(Element root)
	{
		Element email = getSingleElement(root, NOTEZ_XML_EMAIL_SETTINGS_ELEMENT);

		if(Objects.nonNull(email))
		{
			storeProp(email, NOTEZ_MAIL_HOST);
			storeProp(email, NOTEZ_MAIL_USER);
			storeProp(email, NOTEZ_MAIL_PORT);
			storePropBoolean(email, NOTEZ_MAIL_USE_SSL);
		}
	}

	protected void storeProp(Element parent, String attribute)
	{
		storeProp(parent, attribute, "");
	}

	protected void storeProp(Element parent, String attribute, String def)
	{
		String value = getStringAttributeValue(parent, attribute);

		if(value != null)
		{
			putString(attribute, value);
		}
		else
		{
			NotezLog.debug(new StringBuilder("No value for: ").append(attribute)
				.append(System.lineSeparator())
				.append(" using ")
				.append(def)
				.append(" instead")
				.toString());

			putString(attribute, def);
		}
	}

	protected void storePropBoolean(Element parent, String attribute)
	{
		String value = getStringAttributeValue(parent, attribute);

		putBoolean(attribute, Boolean.valueOf(value));
	}

	public boolean save()
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

			return true;
		}
		catch(Exception e)
		{
			NotezLog.error("error while saving properties", e);

			return false;
		}
	}

	protected void saveFolderSettings(Document doc, Element root)
	{
		Element folder = addElement(doc, root, NOTEZ_XML_FOLDER_ELEMENT);

		addAttribute(doc, folder, NOTEZ_REMOTE_FOLDER, get(NOTEZ_REMOTE_FOLDER).getValue());
		addAttribute(doc, folder, NOTEZ_WORK_FOLDER, get(NOTEZ_WORK_FOLDER).getValue());
		addAttribute(doc, folder, NOTEZ_ALWAYS_SAVE_ON_EXIT,
			get(NOTEZ_ALWAYS_SAVE_ON_EXIT).getValue());
	}

	protected void saveButtonSettings(Document doc, Element root)
	{
		Element button = addElement(doc, root, NOTEZ_XML_BUTTONS_ELEMENT);

		addAttribute(doc, button, NOTEZ_BTN_GROUP, get(NOTEZ_BTN_GROUP).getValue());
		addAttribute(doc, button, NOTEZ_BTN_SAVE, get(NOTEZ_BTN_SAVE).getValue());
		addAttribute(doc, button, NOTEZ_BTN_REMOVE, get(NOTEZ_BTN_REMOVE).getValue());
		addAttribute(doc, button, NOTEZ_BTN_SHARE, get(NOTEZ_BTN_SHARE).getValue());
		addAttribute(doc, button, NOTEZ_BTN_ADD, get(NOTEZ_BTN_ADD).getValue());
		addAttribute(doc, button, NOTEZ_BTN_PRINT, get(NOTEZ_BTN_PRINT).getValue());
		addAttribute(doc, button, NOTEZ_BTN_PIN, get(NOTEZ_BTN_PIN).getValue());
	}

	protected void saveSyncSettings(Document doc, Element root)
	{
		Element sync = addElement(doc, root, NOTEZ_XML_SYNC_ELEMENT);

		addAttribute(doc, sync, NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY,
			get(NOTEZ_OPEN_RECEIVED_NOTEZ_DIRECTLY).getValue());
		addAttribute(doc, sync, NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ,
			get(NOTEZ_SHOW_MESSAGE_ON_NEW_NOTEZ).getValue());
		addAttribute(doc, sync, NOTEZ_LET_RECEIVER_RUNNING,
			get(NOTEZ_LET_RECEIVER_RUNNING).getValue());
		addAttribute(doc, sync, NOTEZ_RECEIVER_ON_STARTUP,
			get(NOTEZ_RECEIVER_ON_STARTUP).getValue());
	}

	protected void saveShareUserSettings(Document doc, Element root)
	{
		Element shareUser = addElement(doc, root, NOTEZ_XML_SHARE_USER_ELEMENT);

		for(NotezRemoteUser user : NotezRemoteSync.getAllUsers())
		{
			Element u = addElement(doc, shareUser, NOTEZ_XML_USER_ELEMENT);
			u.setAttribute(NOTEZ_REMOTE_SHARE_ATTRIBUTE, String.valueOf(user.getShare()));
			u.setAttribute(NOTEZ_REMOTE_USERNAME, user.getUsername());
		}
	}

	protected void saveEMailSettings(Document doc, Element root)
	{
		Element email = addElement(doc, root, NOTEZ_XML_EMAIL_SETTINGS_ELEMENT);

		addAttribute(doc, email, NOTEZ_MAIL_HOST, get(NOTEZ_MAIL_HOST).getValue());
		addAttribute(doc, email, NOTEZ_MAIL_USER, get(NOTEZ_MAIL_USER).getValue());
		addAttribute(doc, email, NOTEZ_MAIL_PORT, get(NOTEZ_MAIL_PORT).getValue());
		addAttribute(doc, email, NOTEZ_MAIL_USE_SSL, get(NOTEZ_MAIL_USE_SSL).getValue());
	}

	protected void saveFile(Document doc, File file) throws TransformerException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);

		transformer.transform(source, result);
	}

	public static void saveSystemProperties()
	{
		getSystemProperties().save();
	}
}
