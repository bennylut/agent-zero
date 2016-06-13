/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bgu.dcr.az.vdev.vis.ntraf;

import bgu.dcr.az.vdev.timelines.MessageUsageChange;
import bgu.dcr.az.vdev.timelines.MessagesInQueueChange;
import bgu.dcr.az.vdev.timelines.MessagesOnLineChange;
import bgu.dcr.az.vdev.timelines.CurrentMessageHandlingChange;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.util.StepByStepPathTransition;
import bgu.dcr.az.vdev.vis.misc.AgentIcon;
import com.sun.javafx.geom.Point2D;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.effect.InnerShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Administrator
 */
public class NetworkTrafficVisualizationDrawer implements VisualizationDrawer<Image, Pane> {

    ////////////////////////////////////////////////////////////////////////////
    /// RESOURCES                                                            ///
    ////////////////////////////////////////////////////////////////////////////
    public static Image messageImage = new Image(NetworkTrafficVisualizationDrawer.class.getResourceAsStream("_msg.png"));
    public static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.DARKORANGE, Color.SKYBLUE, Color.WHITESMOKE, Color.BROWN};
    ////////////////////////////////////////////////////////////////////////////
    /// FIELDS                                                               ///
    ////////////////////////////////////////////////////////////////////////////
    private AgentView[] agentViews;
    private SparseGraph<AgentView, Link> graph = new SparseGraph<>();
    private CircleLayout<AgentView, Link> layout = new CircleLayout<>(graph);
    private List<Link> edges;
    private Link[][] edgesMatrix;
    private Group linkDrawingGroup;
    private HashMap<String, Color> messageColors = new HashMap<>();
    private Map<Long, StepByStepPathTransition> runningAnimations = new HashMap<>();
    private String[] agentHandling;
    ////////////////////////////////////////////////////////////////////////////
    /// Layout                                                               ///
    ////////////////////////////////////////////////////////////////////////////
    private TitledPane messageLagendPane = new TitledPane("Message Data", new VBox(5));
    private Pane drawingPane = new Pane();
    ////////////////////////////////////////////////////////////////////////////
    /// Message Lagend                                                       ///
    ////////////////////////////////////////////////////////////////////////////
    private HashMap<String, Label> messageLagendLabels;
    ////////////////////////////////////////////////////////////////////////////
    /// Network Load Graph                                                   ///
    ////////////////////////////////////////////////////////////////////////////
//    ArrayList<Data<Integer, Integer>> networkLoadGraphData;
//    int graphRight = 0;
//    int graphTrail = 100;
//    ObservableList<Data<Integer, Integer>> currentGraphData;
//    private NumberAxis xAxis;

    public void init(final Pane canvas, Execution ex, VisualizationFrameBuffer fullBuffer) {
        canvas.getStylesheets().add(NetworkTrafficVisualizationDrawer.class.getResource("__net.css").toExternalForm());
        canvas.getStyleClass().add("canvas");
        canvas.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                layout(canvas);
                for (StepByStepPathTransition r : runningAnimations.values()) {
                    r.cleanUp();
                }
                runningAnimations.clear();
            }
        });

        initDrawingPane(ex);
//        initNetworkLoadData(fullBuffer);
        canvas.getChildren().add(drawingPane);
        canvas.getChildren().add(messageLagendPane);

        layout(canvas);
    }

    private Color getMessageColor(String msg) {
        Color c = messageColors.get(msg);
        if (c == null) {
            c = colors[messageColors.size()];
            messageColors.put(msg, c);
        }

        return c;
    }

    private void layout(Pane canvas) {
        int margin = 5;
        messageLagendPane.setTranslateX(margin);
        messageLagendPane.setTranslateY(margin);
        messageLagendPane.setPrefSize(200, canvas.getHeight() - margin * 2);
//        messageLagendPane.setPrefHeight(Pane.USE_COMPUTED_SIZE);
        drawingPane.setTranslateX(messageLagendPane.getTranslateX() + messageLagendPane.getPrefWidth() + margin);
        drawingPane.setTranslateY(0);
        drawingPane.setPrefSize(canvas.getWidth() - drawingPane.getTranslateX(), canvas.getHeight());

        //layout drawing pane
        final Dimension dimension = new Dimension((int) drawingPane.getPrefWidth() - 48, (int) drawingPane.getPrefHeight() - 48);
        //positioning 
        layout.setSize(dimension);
        layout.setRadius(-1);
        layout.initialize();
        for (int i = 0; i < agentViews.length; i++) {
            agentViews[i].layout(drawingPane);
        }

        for (Link link : edges) {
            link.layout(drawingPane);
        }
    }

    @Override
    public boolean draw(final Pane canvas, Frame frame) {
        updateAgentColors(frame);
        HashMap<Long, StepByStepPathTransition> running = new HashMap<>();

        List<MessagesOnLineChange.MessageData> online = MessagesOnLineChange.extract(frame);
        for (MessagesOnLineChange.MessageData md : online) {
            if (md.receivedInFrame.getValue() < frame.getFrameNumber()) {
                continue;
            }

            StepByStepPathTransition pt;
            if (runningAnimations.containsKey(md.messageId)) {
                pt = runningAnimations.get(md.messageId);
            } else {
                pt = createAnimation(drawingPane, md);
            }
            running.put(md.messageId, pt);
            pt.interpolate((double) (frame.getFrameNumber() - md.sentInFrame) / (double) (md.receivedInFrame.getValue() - md.sentInFrame));
        }

        Set<Long> notRunningAnymore = new HashSet<>(runningAnimations.keySet());
        notRunningAnymore.removeAll(running.keySet());

        for (Long mid : notRunningAnymore) {
            runningAnimations.get(mid).cleanUp();
        }

//        int[] miq = MessagesInQueueChange.extract(frame);
//        for (int i = 0; i < agentViews.length; i++) {
//            agentViews[i].messagesInQueue.setText("" + miq[i]);
//        }

        updateLagend(frame);
//        updateGraph((int) frame.getFrameNumber());

        runningAnimations = running;

        return true;
    }

    private StepByStepPathTransition createAnimation(final Pane canvas, MessagesOnLineChange.MessageData md) {

//        final VBox node = new VBox();
        final ImageView iv = new ImageView(messageImage);
//        node.getChildren().add(new Text("" + md.receivedInFrame.getValue()));
//        node.getChildren().add(iv);
        final StepByStepPathTransition pt = new StepByStepPathTransition() {
            @Override
            public void cleanUp() {
                canvas.getChildren().remove(iv);
            }
        };

        pt.setOrientation(StepByStepPathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);


        final Link sel = edgesMatrix[md.sender][md.receiver];//graph.findEdge(agentViews[key], agentViews[value]);
        pt.setPath(sel);

        iv.setEffect(InnerShadowBuilder.create().radius(20).color(getMessageColor(md.name)).height(10).width(10).build());
        iv.setCache(true);
        iv.setCacheHint(CacheHint.SPEED);

        iv.setMouseTransparent(true);
        pt.setNode(iv);
        canvas.getChildren().add(iv);
        iv.setTranslateX(drawingPane.getTranslateX() + sel.getSourcePoint().x);
        iv.setTranslateY(sel.getSourcePoint().y);

        runningAnimations.put(md.messageId, pt);
        pt.initialize();
        return pt;
    }

    private void updateAgentColors(Frame f) {

        for (Entry<Integer, String> h : CurrentMessageHandlingChange.extract(f).entrySet()) {
            if (!h.getValue().equals(this.agentHandling[h.getKey()])) {
                this.agentHandling[h.getKey()] = (h.getValue().isEmpty() ? null : h.getValue());
                agentViews[h.getKey()].ai.removeGlow();//.iv.setEffect(null);
                if (this.agentHandling[h.getKey()] != null) {
                    agentViews[h.getKey()].ai.glow(getMessageColor(this.agentHandling[h.getKey()]));
                }
            }
        }
    }

    private void initDrawingPane(Execution ex) {

        final int n = ex.getGlobalProblem().getNumberOfVariables();
        agentHandling = new String[n];

        agentViews = new AgentView[n];
        for (int i = 0; i < n; i++) {
            agentViews[i] = new AgentView(i);
            graph.addVertex(agentViews[i]);
        }

        edgesMatrix = new Link[n][n];
        edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                //if (state.neighbores[i][j]) {
                final Link link = new Link(i, j);
                graph.addEdge(link, agentViews[i], agentViews[j]);
                edges.add(link);
                edgesMatrix[i][j] = link;
                //}
            }
        }


        for (int i = 0; i < agentViews.length; i++) {
            agentViews[i].initialize(drawingPane);
        }

        linkDrawingGroup = new Group();
        for (Link link : edges) {
            link.initialize(drawingPane);
        }
        drawingPane.getChildren().add(linkDrawingGroup);
        linkDrawingGroup.toBack();

        linkDrawingGroup.setCache(true);
        linkDrawingGroup.setCacheHint(CacheHint.QUALITY);
    }

    private void initMessageLagendPane(Frame frame) {
        messageLagendPane.getStyleClass().add("lagend");
        messageLagendPane.setCollapsible(false);

        Map<String, Long> initial = MessageUsageChange.extract(frame);
        this.messageLagendLabels = new HashMap<>();

        for (Entry<String, Long> i : initial.entrySet()) {
            Label l = new Label("" + i.getKey() + ": 0");
            l.setTextFill(getMessageColor(i.getKey()));
            l.setFont(Font.font("Consolas", 14));
            messageLagendLabels.put(i.getKey(), l);
            ((VBox) messageLagendPane.getContent()).getChildren().add(l);
        }
    }

    private void updateLagend(Frame frame) {
        if (messageLagendLabels == null) {
            initMessageLagendPane(frame);
        }

        Map<String, Long> usage = MessageUsageChange.extract(frame);
        for (Entry<String, Long> u : usage.entrySet()) {
            Label m = messageLagendLabels.get(u.getKey());
            if (m != null) {
                m.setText(u.getKey() + " #" + u.getValue());
            }
        }
    }

    private class Link extends Path {

        int source;
        int dest;
        Point2D sourcePoint;
        Point2D destPoint;

        public Link(int source, int dest) {
            this.source = source;
            this.dest = dest;
        }

        public void initialize(Pane canvas) {

            setStroke(Color.ANTIQUEWHITE);
            linkDrawingGroup.getChildren().add(this);
        }

        public void layout(Pane canvas) {
            sourcePoint = new Point2D((float) agentViews[source].getX() + 24, (float) agentViews[source].getY() + 24);
            destPoint = new Point2D((float) agentViews[dest].getX() + 24, (float) agentViews[dest].getY() + 24);

            getElements().clear();
            getElements().addAll(
                    new MoveTo(sourcePoint.x, sourcePoint.y),
                    new LineTo(destPoint.x, destPoint.y));

        }

        public Point2D getSourcePoint() {
            return sourcePoint;
        }

        public Point2D getDestPoint() {
            return destPoint;
        }
    }

    private class AgentView extends Group {

//        int id;
//        private ImageView iv;
//        private Text text;
        private AgentIcon ai = new AgentIcon();
//        private Text messagesInQueue;

        public AgentView(int id) {
            ai.setAgentId(id);
        }

        public void initialize(Pane canvas) {
            //Image
//            iv = new ImageView(AgentIcon.agentImage);
//            iv.setFitHeight(48);
//            iv.setFitWidth(48);
            canvas.getChildren().add(ai);
//            messagesInQueue = new Text("inq");
//            messagesInQueue.setFill(Color.YELLOWGREEN);
//            canvas.getChildren().add(messagesInQueue);

            //Text
//            text = new Text("" + id);
//            text.setFill(Color.CORAL);

//            text.setFont(Font.font("Verdana", 17));
//            canvas.getChildren().add(text);
//            text.setMouseTransparent(true);

        }

        public void layout(Pane canvas) {
            ai.setTranslateX(layout.getX(this));
            ai.setTranslateY(layout.getY(this));

            //Id test
//            if (id >= 10) {
//                text.setX(iv.getX() + 13);
//            } else {
//                text.setX(iv.getX() + 20);
//            }

//            text.setY(iv.getY() + 40);

//            messagesInQueue.setX(ai.getTranslateX());
//            messagesInQueue.setY(ai.getTranslateY() - 16);

        }

        public final double getY() {
            return ai.getTranslateY();
        }

        public final double getX() {
            return ai.getTranslateX();
        }
    }
}
