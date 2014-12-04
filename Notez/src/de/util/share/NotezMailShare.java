/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.util.share;

import java.io.File;
import java.io.IOException;

import de.gui.controller.NotezController;

public class NotezMailShare extends NotezShareBase
{
    protected String mailAdress;

    public NotezMailShare(String mailAdress)
    {
        this.mailAdress = mailAdress;
    }

    @Override
    public NotezShareResult shareNotez(NotezController ctrl, File notez)
        throws IOException
    {
        // TODO add e-mail share
        // Properties props = new Properties();
        // Session session = Session.getDefaultInstance(props, null);
        //
        // try
        // {
        // Message msg = new MimeMessage(session);
        // // TODO add sender e-mail here
        // msg.setFrom(new InternetAddress(mailAdress,
        // genName(mailAdress)));
        //
        // msg.addRecipient(Message.RecipientType.TO,
        // new InternetAddress(mailAdress,
        // genName(mailAdress)));
        //
        // // TODO add username of sender here
        // msg.setSubject("New Notez from Username");
        // msg.setText("TEST");
        // Transport.send(msg);
        //
        // return NotezShareResult.SHARED;
        // }
        // catch(AddressException e)
        // {
        // e.printStackTrace();
        // }
        // catch(MessagingException e)
        // {
        // e.printStackTrace();
        // }

        return NotezShareResult.NOT_SUPPORTED;
    }

    // private String genName(String mail)
    // {
    // return mail.substring(0, mail.indexOf("@"));
    // }
}