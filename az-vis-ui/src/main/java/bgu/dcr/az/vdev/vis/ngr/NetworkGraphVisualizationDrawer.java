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
package bgu.dcr.az.vdev.vis.ngr;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.TimeLine;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.timelines.MessagesOnLineChange;
import bgu.dcr.az.vdev.timelines.MessagesOnLineChange.MessageData;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class NetworkGraphVisualizationDrawer implements Initializable, VisualizationDrawer<Image, Pane> {

    public LineChart<Integer, Integer> chart;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    int graphTrail = 100;
    ArrayList<ObservableList<Data<Integer, Integer>>> displayedData = new ArrayList<>();
    String[] messages;
    LinkedList<Data<Integer, Integer>> flyWeightStorage = new LinkedList<>();
    int[][] preFetchData;
    private TimeLine msgCountTimeLine;
    int lastFrame = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        agentSelectionList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void setupVisualization(VisualizationFrameBuffer buf, String[] messages) {
        this.messages = messages;
        createFlyWaightStorage();
        initNetworkLoadData(buf);
//        initAgentList(numAgents);
    }

    @Override
    public boolean draw(Pane canvas, Frame frame) {
        updateGraph(frame.getFrameNumber());
        return true;
    }

    private void initNetworkLoadData(VisualizationFrameBuffer fullBuffer) {
        msgCountTimeLine = fullBuffer.getTimeLine(MessagesOnLineChange.ID);
        System.out.println("start init network data");

        Map<String, Integer> conv = new HashMap<>();
        for (int i = 0; i < messages.length; i++) {
            conv.put(messages[i], i + 1);
        }

        preFetchData = new int[messages.length + 1][fullBuffer.numberOfFrames()];
        int max = -1;
        for (int frame = 0; frame < fullBuffer.numberOfFrames(); frame++) {
            final List<MessagesOnLineChange.MessageData> mdata = (List<MessagesOnLineChange.MessageData>) msgCountTimeLine.get(frame);
            final int size = mdata.size();
            if (max < size) {
                max = size;
            }

            for (MessageData md : mdata) {
                final Integer mdn = conv.get(md.name);
                if (mdn != null) {
                    preFetchData[mdn][frame]++;
                }
            }
            preFetchData[0][frame] = size;
        }

        List<XYChart.Series<Integer, Integer>> series = new LinkedList<>();
        for (int i = 0; i < messages.length + 1; i++) {
            ObservableList<Data<Integer, Integer>> cdata = FXCollections.<Data<Integer, Integer>>observableArrayList();
            displayedData.add(cdata);
            if (i == 0) {
                final Series<Integer, Integer> series1 = new XYChart.Series<>("All", cdata);
                series.add(series1);
            } else {
                series.add(new XYChart.Series<>(messages[i - 1], cdata));
            }
        }

        chart.setData(FXCollections.observableArrayList(series));
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(max + 10);
        yAxis.setTickUnit((max + 10) / 15);
    }

    private List<Data<Integer, Integer>> data(int series, int from, int to) {
        LinkedList<Data<Integer, Integer>> ret = new LinkedList<>();

        int[] take = preFetchData[series];
        for (int i = from; i <= to; i++) {
            Data<Integer, Integer> d = flyWeightStorage.remove();
            d.setXValue(i);
            d.setYValue(take[i]);
            ret.add(d);
        }

        return ret;
    }

    private void updateGraph(int frameNumber) {
//        try{
        if (frameNumber == lastFrame) {
            return;
        }

        int left = frameNumber - graphTrail;
        if (left < 0) {
            left = 0;
        }

        int i = 0;
        for (ObservableList<Data<Integer, Integer>> d : displayedData) {
            flyWeightStorage.addAll(d);
            d.clear();
            d.addAll(data(i++, left, frameNumber));
        }

//        left -= left%10;
        xAxis.setLowerBound(left);
        xAxis.setUpperBound(frameNumber);

        // CANNOT PERFORM SMART DELTA UPDATE BECAUSE OF A JAVAFX BUG - UNCOMMENT WHEN IT FIXED
//        if (frameNumber < graphRight) {
//            currentGraphData.removeAll(data(frameNumber+1, graphRight));
//            currentGraphData.addAll(data(Math.max(0, frameNumber - graphTrail), Math.max(0, graphRight - graphTrail + 1)));
//        } else {
//            currentGraphData.removeAll(data(Math.max(0, graphRight - graphTrail), Math.max(0, frameNumber - graphTrail - 1)));
//            currentGraphData.addAll(data(graphRight+1, frameNumber));
//        }
//
//        graphRight = frameNumber;
//        }catch(Exception ex){
//            ex.printStackTrace();
//            System.out.println("HERE!");
//        }
//        xAxis.setLowerBound(Math.max(0, frameNumber - graphTrail));
//        xAxis.setUpperBound(frameNumber);
    }
//
//    private void initAgentList(int numAgents) {
//        agentSelectionList.setCellFactory(AgentSelectionListCell.renderer());
//        agentSelectionList.setItems(FXCollections.observableArrayList(Agt0DSL.range(0, numAgents - 1)));
//    }

    private void createFlyWaightStorage() {
        for (int i = 0; i < (messages.length + 2) * graphTrail; i++) {
            flyWeightStorage.add(new Data<Integer, Integer>());
        }
    }
}
