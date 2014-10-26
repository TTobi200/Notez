/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.util.NotezFileUtil;
import de.util.NotezRemoteSync.NotezRemoteUser;

public class NotezDialog
{
    public static final String FXML_PATH = "include/fxml/NotezDialog.fxml";
    private static final String ICON_ERROR = "include/icons/dialog-error.png";
    private static final String ICON_QUESTION = "include/icons/dialog-question.png";
    private static final String ICON_WARNING = "include/icons/dialog-warning.png";
    private static final String ICON_INFO = "include/icons/dialog-info.png";

    private static final double HEIGHT = 100d;

    private static NotezOption option;

    public static enum NotezOption
    {
        YES, NO, CANCEL, CLOSE, OK;
    }

    public static NotezOption showShareWithDialog(Stage parent, String title,
                    String msg, ObservableList<NotezRemoteUser> user)
        throws IOException, InterruptedException
    {

        return showDialog(parent, title, msg, ICON_QUESTION,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);
    }

    public static NotezOption showQuestionDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {

        return showDialog(parent, title, msg, ICON_QUESTION,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);
    }

    public static NotezOption showInfoDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {

        return showDialog(parent, title, msg, ICON_INFO,
            NotezOption.OK, NotezOption.CANCEL);
    }

    public static NotezOption showWarningDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {

        return showDialog(parent, title, msg, ICON_WARNING,
            NotezOption.OK, NotezOption.CANCEL);
    }

    public static NotezOption showErrorDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {
        return showDialog(parent, title, msg, ICON_ERROR,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);
    }

    public static NotezOption showDialog(Stage parent, String title,
                    String msg, String icon, NotezOption... options)
        throws IOException,
        InterruptedException
    {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(
            NotezFileUtil.getResourceURL(NotezDialog.FXML_PATH));

        loader.setController(new NotezDialogController(stage, title,
            msg, new Image(NotezFileUtil.getResourceStream(icon)), options));
        stage.setScene(new Scene(loader.load()));
        stage.setWidth(parent.getWidth());
        stage.setHeight(HEIGHT);
        setModality(stage, parent);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();

        return option;
    }

    private static void setModality(Stage stage, Stage parent)
    {
        if(parent != null && parent.isShowing())
        {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parent.getScene().getWindow());
            relativeToOwner(stage, parent);
        }
    }

    private static void relativeToOwner(Stage stage, Stage owner)
    {
        stage.setX(owner.getX());
        stage.setY(owner.getY() + (owner.getHeight() / 2)
                   - (stage.getHeight() / 2));
    }

    static class NotezDialogController
    {
        @FXML
        private ToolBar toolBar;
        @FXML
        private Button btnClose;
        @FXML
        private Button btnYes;
        @FXML
        private Button btnNo;
        @FXML
        private Button btnOk;
        @FXML
        private Button btnCancel;
        @FXML
        private Label lblTitle;
        @FXML
        private Label lblMsg;
        @FXML
        private ImageView icon;
        @FXML
        private HBox hBoxButtons;

        private Stage stage;
        private Image imgIcon;
        private String title;
        private String msg;
        private double initialX;
        private double initialY;
        private NotezOption[] options;

        public NotezDialogController(Stage stage, String title, String msg,
                                     Image imgIcon, NotezOption... options)
        {
            this.stage = stage;
            this.imgIcon = imgIcon;
            this.title = title;
            this.msg = msg;
            this.options = options;
        }

        @FXML
        public void initialize()
        {
            loadIcons();
            addDraggableNode(toolBar);
            lblTitle.setText(title);
            lblMsg.setText(msg);

            initButtons(options);
        }

        private void initButtons(NotezOption... options)
        {
            for(NotezOption o : options)
            {
                switch(o)
                {
                    case YES:
                        btnYes.setVisible(true);
                        break;
                    case NO:
                        btnNo.setVisible(true);
                        break;
                    case OK:
                        btnOk.setVisible(true);
                        break;
                    case CANCEL:
                        btnCancel.setVisible(true);
                        break;
                    case CLOSE:
                        // Close icon always visible
                        break;
                }
            }

            removeInvisibleBtns(hBoxButtons.getChildren());
        }

        private synchronized void removeInvisibleBtns(
                        ObservableList<Node> children)
        {
            Collection<Node> tmp = new ArrayList<Node>();
            children.forEach(c ->
            {
                if(!c.isVisible())
                {
                    tmp.add(c);
                }
            });

            children.removeAll(tmp);
        }

        private void loadIcons()
        {
            btnClose.setGraphic(new ImageView(new Image(
                NotezFileUtil.getResourceStream(NotezController.ICON_CLOSE))));
            // icon.setImage(imgIcon);
            lblMsg.setGraphic(new ImageView(imgIcon));
        }

        @FXML
        public void close()
        {
            NotezDialog.option = NotezOption.CLOSE;
            stage.close();
        }

        @FXML
        public void yes()
        {
            NotezDialog.option = NotezOption.YES;
            stage.close();
        }

        @FXML
        public void ok()
        {
            NotezDialog.option = NotezOption.OK;
            stage.close();
        }

        @FXML
        public void no()
        {

            NotezDialog.option = NotezOption.NO;
            stage.close();
        }

        @FXML
        public void cancel()
        {

            NotezDialog.option = NotezOption.CANCEL;
            stage.close();
        }

        private void addDraggableNode(final Node node)
        {
            node.setOnMousePressed(me ->
			{
			    if(me.getButton() != MouseButton.MIDDLE)
			    {
			        initialX = me.getSceneX();
			        initialY = me.getSceneY();
			    }
			});

            node.setOnMouseDragged(me ->
			{
			    if(me.getButton() != MouseButton.MIDDLE)
			    {
			        node.getScene()
			            .getWindow()
			            .setX(me.getScreenX() - initialX);
			        node.getScene()
			            .getWindow()
			            .setY(me.getScreenY() - initialY);
			    }
			});
        }
    }
}