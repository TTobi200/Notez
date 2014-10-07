/*
 * Copyright © 2014 Unitechnik Systems GmbH. All Rights Reserved.
 */
package de.gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import de.util.NotezSettings.Setting;

public class NotezSettingsPane extends GridPane
{
    private TextField txtName;
    private Setting<Object> setting;

    public NotezSettingsPane(Setting<Object> s)
    {
        this.setting = s;
        init(s);
    }

    public void init(Setting<?> setting)
    {
        String name = setting.getName();
        txtName = new TextField((String)setting.getValue());

        add(new Label(name), 1, 1);
        add(txtName, 2, 1);
    }

    public void saveSetting()
    {
        setting.setValue(txtName.getText());
    }
}