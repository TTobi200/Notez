/*
 * $Header$
 * 
 * $Log$
 * Copyright © 2014 T.Ohm . All Rights Reserved.
 */
package de.gui;

import static de.notez.prop.NotezProperties.NOTEZ_MAIL_USER;

import java.io.IOException;
import java.util.*;

import javafx.beans.value.*;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import de.notez.NotezRemoteSync.NotezRemoteUser;
import de.notez.*;
import de.util.NotezFileUtil;

public class NotezDialog
{
    public static final String FXML_PATH = "include/fxml/NotezDialog.fxml";
    private static final String ICON_ERROR = "include/icons/dialog-error.png";
    private static final String ICON_QUESTION = "include/icons/dialog-question.png";
    private static final String ICON_WARNING = "include/icons/dialog-warning.png";
    private static final String ICON_INFO = "include/icons/dialog-info.png";
    private static final String ICON_CLOSE = "include/icons/icon_close.png";

    private static final double HEIGHT = 100d;
    private static final double WIDTH = 300;

    private static NotezOption option;

    public static enum NotezOption
    {
        YES(null), NO(null), CANCEL(null), CLOSE(null), OK(null);

        Object data;

        private NotezOption(Object data)
        {
            this.data = data;
        }

        public Object getData()
        {
            return data;
        }

        public NotezOption setData(Object data)
        {
            this.data = data;
            return this;
        }
    }

    public static NotezRemoteUser showAddUserDialog(Stage parent)
        throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent,
            "Add new Notez User",
            "",
            ICON_INFO,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        TextField txtName = new TextField();
        TextField txtShare = new TextField();
        VBox hBox = new VBox();

        hBox.getChildren().addAll(new VBox(new Label("Name:  "), txtName),
            new VBox(new Label("Share: "), txtShare));
        ctrl.hBoxMsg.getChildren().add(hBox);

        ctrl.stage.setHeight(HEIGHT + 50);
        relativeToOwner(ctrl.stage, parent);

        NotezOption o = ctrl.showAndWait();
        NotezRemoteUser user = new NotezRemoteUser(txtName.getText(),
            txtShare.getText());

        return o == NotezOption.YES ? user : null;
    }

    public static NotezRemoteUser showShareWithDialog(Stage parent,
                    String title,
                    String msg, ObservableList<NotezRemoteUser> user)
        throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg,
            ICON_QUESTION,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        ComboBox<NotezRemoteUser> cbUser = new ComboBox<>(user);

        ctrl.hBoxMsg.getChildren().
            addAll(cbUser);

        return ctrl.showAndWait() == NotezOption.YES ?
                        cbUser.getSelectionModel().getSelectedItem() : null;
    }

    public static NotezOption showRememberQuestionDialog(Stage parent,
                    String title,
                    String msg,
                    String propKey) throws IOException,
        InterruptedException
    {
        return showRememberQuestionDialog(parent, title, msg, propKey, false);
    }

    public static NotezOption showRememberQuestionDialog(Stage parent,
                    String title,
                    String msg,
                    String propKey,
                    boolean evenShowDialog) throws IOException,
        InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg,
            ICON_QUESTION,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        if(NotezSystem.getSystemProperties().containsKey(propKey) && !evenShowDialog)
        {
            return NotezSystem.getSystemProperties().getBoolean(propKey) ?
                            NotezOption.YES : NotezOption.NO;
        }

        CheckBox cbRemember = new CheckBox(
            "Remember my decission ");
        ctrl.hBoxButtons.getChildren().add(0,
            cbRemember);

        NotezOption o = ctrl.showAndWait();

        if(cbRemember.isSelected())
        {
            switch(o)
            {
                default:
                case CANCEL:
                case CLOSE:
                    // Do nothing
                    break;

                case NO:
                	NotezSystem.getSystemProperties().putBoolean(propKey, false);
                    break;

                case OK:
                case YES:
                	NotezSystem.getSystemProperties().putBoolean(propKey, true);
                    break;
            }
        }

        return o;
    }

    public static NotezOption showQuestionDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg,
            ICON_QUESTION,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        return ctrl.showAndWait();
    }

    public static NotezOption showInfoDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg, ICON_INFO,
            NotezOption.OK, NotezOption.CANCEL);

        return ctrl.showAndWait();
    }

    public static NotezOption showWarningDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg,
            ICON_WARNING,
            NotezOption.OK, NotezOption.CANCEL);

        return ctrl.showAndWait();
    }

    public static NotezOption showExceptionDialog(Stage parent, String title,
                    String msg, Throwable t) throws IOException,
        InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, "", ICON_ERROR,
            NotezOption.OK, NotezOption.CANCEL);

        StringBuilder stack = new StringBuilder(t.getLocalizedMessage());
        stack.append("\n");

        StackTraceElement[] trace = t.getStackTrace();
        for(StackTraceElement element : trace)
        {
            stack.append(element)
                .append("\n");
        }

        VBox vBoxEx = new VBox();

        vBoxEx.getChildren().addAll(new Label(msg),
            new TextArea(stack.toString()));

        ctrl.hBoxMsg.getChildren().add(vBoxEx);
        ctrl.stage.setHeight(200);
        relativeToOwner(ctrl.stage, parent);

        return ctrl.showAndWait();
    }

    public static String[] showMailLoginDialog(Stage parent, String title)
        throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent,
            title,
            "",
            ICON_INFO,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        String userName = NotezSystem.getSystemProperties().getString(NOTEZ_MAIL_USER);

        TextField txtName = new TextField(userName);
        PasswordField txtPass = new PasswordField();
        VBox hBox = new VBox();

        hBox.getChildren().addAll(new Label(
            "Please enter your mail login data."),
            new VBox(new Label("E-Mail:  "), txtName),
            new VBox(new Label("Password: "), txtPass));
        ctrl.hBoxMsg.getChildren().add(hBox);

        ctrl.stage.setHeight(HEIGHT + 60);
        relativeToOwner(ctrl.stage, parent);

        NotezOption o = ctrl.showAndWait();

        return o == NotezOption.YES ? new String[] { txtName.getText(),
            txtPass.getText() } : null;
    }

    public static NotezOption showErrorDialog(Stage parent, String title,
                    String msg) throws IOException, InterruptedException
    {
        NotezDialogController ctrl = showDialog(parent, title, msg, ICON_ERROR,
            NotezOption.YES, NotezOption.NO, NotezOption.CANCEL);

        return ctrl.showAndWait();
    }

    public static NotezDialogController showDialog(Stage parent, String title,
                    String msg, String icon, NotezOption... options)
        throws IOException,
        InterruptedException
    {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(
            NotezFileUtil.getResourceURL(NotezDialog.FXML_PATH));

        NotezDialogController ctrl = new NotezDialogController(stage, title,
            msg, new Image(NotezFileUtil.getResourceStream(icon)), options);

        loader.setController(ctrl);
        stage.setScene(new Scene(loader.load()));
        stage.setWidth(WIDTH);
        // stage.setWidth(parent.isShowing() ?
        // parent.getWidth() : WIDTH);
        stage.setHeight(HEIGHT);
        setModality(stage, parent);
        stage.initStyle(StageStyle.UNDECORATED);

        return ctrl;
    }

    private static void setModality(Stage stage, Stage parent)
    {
        if(parent != null && parent.isShowing())
        {
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parent.getScene().getWindow());
            relativeToOwner(stage, parent);
            grayOutParent(stage, parent);
        }
    }

    private static void grayOutParent(Stage stage, Stage parent)
    {
        // FORTEST Gray out satck pane of gui
        StackPane stack = (StackPane)parent.getScene().lookup(
            "#stack");
        if(stack != null)
        {
            Pane grayPane = new Pane();
            grayPane.setBackground(new Background(new BackgroundFill(
                Color.GRAY,
                CornerRadii.EMPTY, Insets.EMPTY)));
            grayPane.setOpacity(0.4);
            stack.setEffect(new BoxBlur());
            stack.getChildren().add(grayPane);

            stage.showingProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(
                                ObservableValue<? extends Boolean> observable,
                                Boolean oldValue,
                                Boolean newValue)
                {
                    if(!newValue.booleanValue())
                    {
                        stack.setEffect(null);
                        stack.getChildren().remove(grayPane);
                        stage.showingProperty().removeListener(this);
                    }
                }
            });
        }
    }

    private static void relativeToOwner(Stage stage, Stage owner)
    {
        stage.setX(owner.getX()
                   + (owner.getWidth() / 2 - (stage.getWidth() / 2)));
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
        @FXML
        private HBox hBoxMsg;
        @FXML
        private ImageView resize;

        private Stage stage;
        private Image imgIcon;
        private String title;
        private String msg;
        private double initialX;
        private double initialY;
        private NotezOption[] options;
        private double curX;
        private double curY;

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
            setAsResizeCorner(resize);
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
                NotezFileUtil.getResourceStream(ICON_CLOSE))));
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

        public NotezOption showAndWait()
        {
            stage.showAndWait();
            return option;
        }

        // TODO put this in one util class?
        protected void setAsResizeCorner(final Node node)
        {
            node.setOnMousePressed(event -> {
                if(event.getButton() == MouseButton.PRIMARY)
                {
                    curX = event.getSceneX();
                    curY = event.getSceneY();
                }
            });

            node.setOnMouseDragged(event -> {
                if(event.getButton() == MouseButton.PRIMARY)
                {
                    double tempH = event.getSceneX() - curY;
                    double tempW = event.getSceneY() - curX;
                    double height = event.getSceneY() + event.getSceneY()
                                    - curX;
                    double width = event.getSceneX() + event.getSceneX() - curY;

                    if(height < stage.getMinHeight())
                    {
                        height = stage.getMinHeight();
                    }
                    if(width < stage.getMinWidth())
                    {
                        width = stage.getMinWidth();
                    }

                    stage.setWidth(width);
                    stage.setHeight(height);
                    curY += tempH;
                    curX += tempW;
                }
            });

            // node.setCursor(Cursor.SE_RESIZE);
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