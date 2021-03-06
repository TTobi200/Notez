/*
 * $Header$
 * 
 * $Log$
 * Copyright � 2014 T.Ohm . All Rights Reserved.
 */
package de.notez.share;

import static de.notez.prop.NotezProperties.*;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import de.gui.NotezDialog;
import de.gui.comp.NotezSettingsPane.NotezSettingsPaneTabPane;
import de.notez.*;
import de.notez.data.NotezData;

public class NotezMailShare extends NotezShareBase
{
    private String receipAdress;

    public NotezMailShare(String receipAdress)
    {
        this.receipAdress = receipAdress;
    }

    @Override
    public NotezShareResult shareNotez(NotezNote note, File notez)
        throws IOException
    {
        try
        {
            String host = NotezSystem.getSystemProperties().getString(NOTEZ_MAIL_HOST);
            String port = NotezSystem.getSystemProperties().getString(NOTEZ_MAIL_PORT);
            boolean useSSL = NotezSystem.getSystemProperties().getBoolean(NOTEZ_MAIL_USE_SSL);
            if(isNullOrEmpty(host) || isNullOrEmpty(port))
            {
                switch(NotezDialog.showQuestionDialog(note.getGui(),
                    "E-Mail settings missing",
                    "Some E-Mail-Settings are missing.\r\n"
                                    + "Do you like to edit them now?"))
                {
                    case OK:
                    case YES:
                        note.getGui().getSettingsPane().switchToPane(NotezSettingsPaneTabPane.EMAIL);
                    default:
                    case CANCEL:
                    case CLOSE:
                    case NO:
                        return NotezShareResult.CANCELD_BY_USER;
                }
            }

            String[] loginData = NotezDialog.showMailLoginDialog(
                note.getGui(),
                "Send Notez to " + receipAdress);

            if(loginData == null)
            {
                return NotezShareResult.CANCELD_BY_USER;
            }

            String userName = loginData[0];
            String password = loginData[1];

            Properties props = System.getProperties();

            props.put("mail.smtp.user", userName);
            props.put("mail.smtp.password", password);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            // props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable",
                useSSL);
            props.put("mail.smtp.EnableSSL.enable",
                useSSL);

            Session session = Session.getInstance(props, null);
            // session.setDebug(true);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(NotezSystem.getSystemProperties().getString(NOTEZ_MAIL_USER)));

            message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(receipAdress));

            NotezData notezData = note.getData();
            message.setSubject(notezData.getTitle());
            message.setContent(
                "<html>Add Notez text here in html formatted!</html>",
                "text/html");

            Transport transport = session.getTransport("smtp");

            transport.connect(host, userName,
                password);
            transport.sendMessage(message, message.getAllRecipients());

            return NotezShareResult.SHARED;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return NotezShareResult.NOT_SUPPORTED;
        }
    }

    private boolean isNullOrEmpty(String string)
    {
        if(Objects.nonNull(string))
        {
            return string.isEmpty();
        }

        return true;
    }

    public class NotezMailData
    {
        private String host;
        private String user;
        private String password;
        private String port;
        private List<String> toList;
        private String htmlBody;
        private String subject;

        public NotezMailData(String host, String user, String password,
                             String port, List<String> toList,
                             String htmlBody, String subject)
        {
            this.host = host;
            this.user = user;
            this.password = password;
            this.port = port;
            this.toList = toList;
            this.htmlBody = htmlBody;
            this.subject = subject;
        }

        public String getHost()
        {
            return host;
        }

        public String getUser()
        {
            return user;
        }

        public String getPassword()
        {
            return password;
        }

        public String getPort()
        {
            return port;
        }

        public List<String> getToList()
        {
            return toList;
        }

        public String getHtmlBody()
        {
            return htmlBody;
        }

        public String getSubject()
        {
            return subject;
        }
    }
}