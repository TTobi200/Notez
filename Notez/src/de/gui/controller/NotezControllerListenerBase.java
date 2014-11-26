package de.gui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashSet;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import de.gui.NotezFrame;
import de.util.NotezFXMLInitializable;

public abstract class NotezControllerListenerBase<C extends NotezControllerBase<?, ?>>
                implements
                NotezFXMLInitializable
{
    public static DataFormat NOTEZ_CONTROLLER_DATA_FORMAT = new DataFormat(
        NotezController.class.getName(),
        NotezControllerListeners.class.getName());

    private Collection<Runnable> onShowing;

    protected C c;

    protected double curX;
    protected double curY;

    protected BooleanBinding grouped;

    protected Rectangle stageSize;

    public NotezControllerListenerBase(C controller)
    {
        this.c = controller;
    }

    @Override
    public void initialize()
    {
        stageSize = new Rectangle(c.getStage().getX(), c.getStage().getY(),
            c.getStage().getWidth(), c.getStage().getHeight());

        onShowing = new HashSet<>();

        c.stage.showingProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                            Boolean oldValue,
                            Boolean newValue)
            {
                if(newValue.booleanValue())
                {
                    onShowing.forEach(Runnable::run);
                    onShowing.clear();
                    onShowing = null;
                    c.stage.showingProperty().removeListener(this);
                }
            }
        });

        doOnFirstShowing(() -> {
            stageSize.setX(c.stage.getX());
            stageSize.setY(c.stage.getY());
            stageSize.setWidth(c.stage.getWidth());
            stageSize.setHeight(c.stage.getHeight());
        });

        try
        {
            loadNote(c.note);
        }
        catch(IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initSettings();

        bindMinSizes();

        initNoteChanged();

        initPinNote();

        initStackation();

        // must be saved
        grouped = c.noteParent.isNotNull().or(c.noteChild.isNotNull());
    }

    protected void bindMinSizes()
    {
        DoubleBinding b = null;
        for(Node n : c.toolBar.getChildrenUnmodifiable())
        {
            if(n instanceof Region)
            {
                Region r = (Region)n;
                if(b == null)
                {
                    b = Bindings.add(0d, r.widthProperty());
                }
                else
                {
                    b = b.add(r.widthProperty());
                }
            }
        }
        c.toolBar.prefWidthProperty().bind(b);

        c.stage.minWidthProperty().bind(c.toolBar.prefWidthProperty());
    }

    protected void doOnFirstShowing(Runnable run)
    {
        onShowing.add(run);
    }

    protected void setNodeForDragging(final Node node)
    {
        node.setOnMousePressed(me -> {
            if(me.getButton() == MouseButton.PRIMARY)
            {
                c.initialX = me.getSceneX();
                c.initialY = me.getSceneY();
            }
        });

        node.setOnMouseDragged(me -> {
            if(me.getButton() == MouseButton.PRIMARY)
            {
                c.stage.setX(me.getScreenX() - c.initialX);
                c.stage.setY(me.getScreenY() - c.initialY);
            }
        });
    }

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
                double height = event.getSceneY() + event.getSceneY() - curX;
                double width = event.getSceneX() + event.getSceneX() - curY;

                if(height < c.stage.getMinHeight())
                {
                    height = c.stage.getMinHeight();
                }
                if(width < c.stage.getMinWidth())
                {
                    width = c.stage.getMinWidth();
                }

                c.stage.setWidth(width);
                c.stage.setHeight(height);
                curY += tempH;
                curX += tempW;
            }
        });

        node.setCursor(Cursor.SE_RESIZE);
    }

    protected void displayChilds(Node[] nodes, boolean visible)
    {
        Timeline line = new Timeline();
        final Duration DUR = Duration.seconds(1d);
        for(Node n : nodes)
        {
            line.getKeyFrames().add(
                new KeyFrame(DUR, new KeyValue(n.opacityProperty(),
                    visible ? 1d : 0d)));
        }
        line.play();
    }

    protected void addVisibleToolbarNodeHider(Parent toolBar)
    {
        Node[] itms = toolBar.getChildrenUnmodifiable()
            .stream()
            .toArray(Node[]::new);

        // Add item visibility
        addVisibleNodeHider(toolBar, itms);

        for(Object o : itms)
        {
            if(o instanceof ButtonBase)
            {
                Tooltip tT = ((ButtonBase)o).getTooltip();
                if(tT != null)
                {
                    // Add Tooltip visibility
                    addVisibleNodeHider(tT, itms);
                }
            }
        }

        addVisibleNodeHider(toolBar, toolBar.getChildrenUnmodifiable().stream()
            .toArray(Node[]::new));
    }

    // XXX ???
    protected void addVisibleNodeHider(Tooltip tT, Node... itms)
    {
        tT.setOnShowing(arg0 -> displayChilds(itms, true));

        tT.setOnHiding(arg0 -> displayChilds(itms, true));
    }

    protected void addVisibleNodeHider(final Node node, Node... itms)
    {
        node.setOnMouseEntered(me -> displayChilds(itms, true));

        node.setOnMouseExited(me -> displayChilds(itms, false));
    }

    protected void initNoteChanged()
    {
        // TODO set notechanged correctly
        c.noteChanged = c.data.textChangedProperty()
            .or(stageSize.xProperty().isEqualTo(c.getStage().xProperty(), 0.2))
            .or(stageSize.yProperty().isEqualTo(c.getStage().yProperty(), 0.2))
            .or(stageSize.widthProperty().isEqualTo(
                c.getStage().widthProperty(), 0.2))
            .or(stageSize.heightProperty().isEqualTo(
                c.getStage().heightProperty(), 0.2));
    }

    protected abstract void initPinNote();

    protected void setAsDndSource(final Node node)
    {
        node.setOnDragDetected(event -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);

            // Copy index of this Notez to clipboard
            ClipboardContent content = new ClipboardContent();
            // content.putString(String.valueOf(c.idx));
            content.put(NOTEZ_CONTROLLER_DATA_FORMAT, c.idx);
            db.setContent(content);

            event.consume();
        });

        node.setCursor(Cursor.HAND);
    }

    protected void setAsDndTarget(final Node node)
    {
        node.setOnDragOver(event -> {

            if(event.getDragboard().hasContent(NOTEZ_CONTROLLER_DATA_FORMAT)
               && !event.getDragboard()
                   .getContent(NOTEZ_CONTROLLER_DATA_FORMAT)
                   .equals(c.idx))
            {
                // Accept the move
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        node.setOnDragDropped(event -> {

            // Get dropped notez id from clipbord and pin it to this notez
            if(event.isAccepted())
            {
                Dragboard db = event.getDragboard();
                // NotezController ctrl =
                // NotezFrame.getNotez(Integer.valueOf(db.getString()));
                NotezController ctrl = NotezFrame.getNotez((Integer)db
                    .getContent(NOTEZ_CONTROLLER_DATA_FORMAT));
                ctrl.pinToNode(c);
            }

            event.setDropCompleted(true);
            event.consume();
        });
    }

    protected void moveStageAnimatedTo(Point2D pos)
    {
        final Duration DUR = Duration.seconds(1d);

        DoubleProperty x = new SimpleDoubleProperty(c.stage.getX());
        DoubleProperty y = new SimpleDoubleProperty(c.stage.getY());

        ChangeListener<Number> xLis = (xx, old, newOne) -> c.stage.setX(newOne.doubleValue());
        x.addListener(xLis);
        ChangeListener<Number> yLis = (yy, old, newOne) -> c.stage.setY(newOne.doubleValue());
        y.addListener(yLis);

        new Timeline(new KeyFrame(DUR, new KeyValue(x, pos.getX()),
            new KeyValue(y, pos.getY())))
            .play();
    }

    // TODO by god gon't know why unchecked
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void initStackation()
    {
        // TODO case delete
        // TODO dissolve
        c.noteParent.addListener((ChangeListener<NotezControllerBase>)(p, o, n) ->
        {
            NotezControllerBase nn = n;
            if(nn.noteChild.get() != null)
            {
                // get recursive to the last part
                c.noteParent.set((NotezControllerBase<?, ?>)nn.noteChild.get());
                return;
            }

            nn.noteChild.set(c);

            final Stage stage = c.getStage();
            stage.setX(nn.getStage().getX());
            nn.getStage().xProperty().addListener((x, oo, nnn) -> {
                stage.setX(nnn.doubleValue());
            });
            stage.setY(nn.getStage().getY() + nn.toolBar.getHeight());
            nn.getStage().yProperty().addListener((y, oo, nnn) -> {
                stage.setY(nnn.doubleValue() + c.toolBar.getHeight());
            });

            c.getStage().xProperty().addListener((x, oo, nnn) -> {
                nn.getStage().setX(nnn.doubleValue());
            });
            c.getStage().yProperty().addListener((y, oo, nnn) -> {
                nn.getStage().setY(nnn.doubleValue() - c.toolBar.getHeight());
            });
        });
    }

    /**
     * Method to load note from file.
     *
     * @param note
     *            = the {@link File} to load note from
     * @return true if file could be loaded
     * @throws IOException
     *             See: {@link Files#readAllBytes(java.nio.file.Path)}
     */
    protected abstract boolean loadNote(File note) throws IOException;

    protected abstract void initSettings();
}
