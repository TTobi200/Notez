package de.gui.comp;

import java.io.*;

import javafx.beans.binding.Bindings;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import de.gui.NotezGui;
import de.util.*;

public class NotezTextPane extends BorderPane implements NotezComponent
{
    public static final String FXML = "NotezTextPane.fxml";

    @FXML
    private HTMLEditor txt;

    @FXML
    private Button btnPrevPage;

    @FXML
    private Button btnNextPage;

    @FXML
    private Label lblPage;

    public NotezTextPane() throws IOException
    {
        if(!NotezSystemUtil.isRunningInSceneBuilder())
        {
            FXMLLoader loader = new FXMLLoader(
                NotezFileUtil.getResourceURL(NotezFileUtil.FXML_FOLDER
                                             + File.separator + FXML));
            loader.setRoot(this);
            loader.setController(this);

            loader.load();

            // XXX $TTobi always throws exceptions
//            editToolBar(txt);
        }
    }

    private void editToolBar(HTMLEditor txt)
    {
        moveFromTo(txt, "PopupButton", 0, "ToolBar", 2);
        moveFromTo(txt, "PopupButton", 1, "ToolBar", 2);

        moveFromTo(txt, "Separator", 4, "ToolBar", 2);
        moveFromTo(txt, "ComboBox", 2, "ToolBar", 2);
        moveFromTo(txt, "Separator", 5, "ToolBar", 2);

        moveFromTo(txt, "ToggleButton", 6, "ToolBar", 2);
        moveFromTo(txt, "ToggleButton", 7, "ToolBar", 2);
        moveFromTo(txt, "ToggleButton", 8, "ToolBar", 2);

        removeFrom(txt, "ToolBar", 1);
        removeFrom(txt, "ToolBar", 0);
    }

    public void moveFromTo(HTMLEditor he, String t, int c, String t2, int c2)
    {
        Node nCb = new Button(); // just has to be sth.

        // Copy From:
        int i = 0;
        switch(t)
        {
            case "Button":
                for(Node candidate : (he.lookupAll("Button")))
                {
                    if(candidate instanceof Button)
                    {
                        Button cb = (Button)candidate;
                        if(i == c)
                        {
                            nCb = cb;
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "Separator":
                for(Node candidate : (he.lookupAll("Separator")))
                {
                    if(candidate instanceof Separator)
                    {
                        Separator cb = (Separator)candidate;
                        if(i == c)
                        {
                            nCb = cb;
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "ComboBox":
                for(Node candidate : (he.lookupAll("ComboBox")))
                {
                    if(candidate instanceof ComboBox)
                    {
                        ComboBox cb = (ComboBox)candidate;
                        if(i == c)
                        {
                            nCb = cb;
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "ToggleButton":
                for(Node candidate : (he.lookupAll("ToggleButton")))
                {
                    if(candidate instanceof ToggleButton)
                    {
                        ToggleButton cb = (ToggleButton)candidate;
                        if(i == c)
                        {
                            nCb = cb;
                            break;
                        }
                    }
                    i++;
                }
                break;
        }
        // Copy To:
        i = 0;
        switch(t2)
        {
            case "ToolBar":
                for(Node candidate : (he.lookupAll("ToolBar")))
                {
                    if(candidate instanceof ToolBar)
                    {
                        ToolBar cb2 = (ToolBar)candidate;
                        if(i == c2)
                        {
                            cb2.getItems().add(nCb);
                            break;
                        }
                    }
                    i++;
                }
                break;
        }
    }

    public void removeFrom(HTMLEditor he, String t, int c)
    {
        int i = 0;

        switch(t)
        {
            case "ToolBar":
                for(Node candidate : (he.lookupAll("ToolBar")))
                {
                    if(candidate instanceof ToolBar)
                    {
                        ToolBar cb = (ToolBar)candidate;
                        if(i == c)
                        {
                            Node nCb = cb;
                            ((Pane)nCb.getParent()).getChildren().remove(nCb);
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "PopupButton":
                for(Node candidate : (he.lookupAll("PopupButton")))
                {
                    if(i == c)
                    {
                        Node nCb = candidate;
                        nCb.setVisible(false);
                        nCb.setManaged(false);
                        break;
                    }
                    i++;
                }
                break;
            case "ToggleButton":
                for(Node candidate : (he.lookupAll("ToggleButton")))
                {
                    if(candidate instanceof ToggleButton)
                    {
                        ToggleButton cb = (ToggleButton)candidate;
                        if(i == c)
                        {
                            Node nCb = cb;
                            nCb.setVisible(false);
                            nCb.setManaged(false);
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "Separator":
                for(Node candidate : (he.lookupAll("Separator")))
                {
                    if(candidate instanceof Separator)
                    {
                        Separator cb = (Separator)candidate;
                        if(i == c)
                        {
                            Node nCb = cb;
                            nCb.setVisible(false);
                            nCb.setManaged(false);
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "Button":
                for(Node candidate : (he.lookupAll("Button")))
                {
                    if(candidate instanceof Button)
                    {
                        Button cb = (Button)candidate;
                        if(i == c)
                        {
                            Node nCb = cb;
                            nCb.setVisible(false);
                            nCb.setManaged(false);
                            break;
                        }
                    }
                    i++;
                }
                break;
            case "ComboBox":
                for(Node candidate : (he.lookupAll("ComboBox")))
                {
                    if(candidate instanceof ComboBox)
                    {
                        ComboBox cb = (ComboBox)candidate;
                        if(i == c)
                        {
                            Node nCb = cb;
                            nCb.setVisible(false);
                            nCb.setManaged(false);
                            break;
                        }
                    }
                    i++;
                }
                break;
        }
    }

    @FXML
    private void prevPage()
    {
        getNote().getData().getPageData().prevPage();
    }

    @FXML
    private void nextPage()
    {
        getNote().getData().getPageData().nextPage();
    }

    public HTMLEditor getTxt()
    {
        return txt;
    }

    protected NotezGui gui;

    @Override
    public void setGui(NotezGui gui)
    {
        this.gui = gui;
    }

    @Override
    public NotezGui getGui()
    {
        return gui;
    }

    @Override
    public void setListener()
    {
        lblPage.textProperty()
            .bind(
                Bindings.concat(
                    getNote().getData()
                        .getPageData()
                        .curPageIndexProperty()
                        .add(1),
                    " / ",
                    getNote().getData().getPageData().sizeProperty()));

        btnPrevPage.disableProperty().bind(
            getNote().getData()
                .getPageData()
                .curPageIndexProperty()
                .isEqualTo(0));
        // FIXME: No textChangedProperty - try to do this with listener (1 / 2)
        // btnNextPage.disableProperty().bind(
        // getNote().getData().getPageData().curPageIndexProperty().isEqualTo(
        // getNote().getData().getPageData().sizeProperty()
        // .subtract(1))
        // .and(txt.textProperty().isEmpty()));

        txt.setHtmlText(getNote().getData().getPageData().getText());
        // FIXME: No textChangedProperty - try to do this with listener (2 / 2)
        // txt.textProperty().bindBidirectional(
        // getNote().getData().getPageData().textProperty());
    }
}
