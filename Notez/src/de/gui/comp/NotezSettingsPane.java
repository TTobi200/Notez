/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui.comp;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import de.util.NotezSettings.Setting;

@SuppressWarnings("unchecked")
public class NotezSettingsPane extends GridPane
{
    public static enum NotezSettingsType
    {
        TEXT, COMBO, TICK;
    }

    private Node node;
    private Setting<Object>[] setting;
    private NotezSettingsType type;

    public NotezSettingsPane(Setting<Object>... s)
    {
        this(NotezSettingsType.TEXT, s);
    }

    public NotezSettingsPane(NotezSettingsType type, Setting<Object>... setting)
    {
        this.setting = setting;
        this.type = type;
        init(type, setting);
    }

    public void init(NotezSettingsType type, Setting<Object>... setting)
    {
        for(Setting<Object> s : setting)
        {
            switch(type)
            {
                default:
                case TEXT:
                    node = new TextField((String)s.getValue());
                    break;

                case COMBO:
                    node = new ComboBox<Setting<Object>>();
                    ((ComboBox<Setting<Object>>)node).getItems()
                        .addAll(setting);
                    break;

                case TICK:
                    node = new CheckBox();
                    break;
            }

            add(new Label(s.getName()), 1, 1);
            add(node, 2, 1);
        }
    }

    public void saveSetting()
    {
        for(Setting<Object> s : setting)
        {
            String value = "";
            switch(type)
            {
                default:
                case TEXT:
                    value = ((TextField)node).getText();
                    break;

                case COMBO:
                    value = String.valueOf(((ComboBox<Setting<Object>>)node).getSelectionModel()
                        .getSelectedItem()
                        .getValue());
                    break;

                case TICK:
                    value = String.valueOf(((CheckBox)node).isSelected());
                    break;
            }
            s.setValue(value);
        }
    }
}