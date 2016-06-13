/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.totc;

import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.vdev.timelines.TotalCcChange;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class TotalCCVisualizationDrawer implements Initializable, VisualizationDrawer<Image, Pane> {

    public StackedBarChart<String, Integer> chart;
    public CategoryAxis xAxis;
    
    private Map<String, XYChart.Data[]> seriesData;
    private int nagents;
    private int lastFrame = -1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setup(int nagent, String[] messages){
        this.nagents = nagent;
        initializeChart(messages);
    }

    private void initializeChart(String[] messages) {
        String[] agents = new  String[nagents];
        for (int i=0; i<agents.length; i++) agents[i] = "" + i;
        xAxis.setCategories(FXCollections.observableArrayList(agents));
        LinkedList<XYChart.Series<String, Integer>> seriesList = new LinkedList<>();
        seriesData = new HashMap<>();

        for (int i = 0; i < messages.length; i++) {
            XYChart.Series<String, Integer> s = new XYChart.Series<>();
            s.setName(messages[i]);
            LinkedList<Data<String, Integer>> sdl = new LinkedList<>();
            Data[] d = new Data[nagents];
            seriesData.put(messages[i], d);
            
            for (int j = 0; j < nagents; j++) {
                d[j] = new Data<String, Integer>(""+j, 0);
                sdl.add(d[j]);
            }
            
            s.setData(FXCollections.observableArrayList(sdl));
            seriesList.add(s);
        }
        
        chart.setData(FXCollections.observableArrayList(seriesList));
    }

    @Override
    public boolean draw(Pane canvas, Frame frame) {
//        if (this.lastFrame == frame.getFrameNumber()) return true;
        this.lastFrame = frame.getFrameNumber();
        
        Map<String, int[]> change = TotalCcChange.extract(frame);
        int[] last = new int[nagents];
        for (Entry<String, int[]> e : change.entrySet()){
            for (int i=0; i<nagents; i++){
                final Data d = seriesData.get(e.getKey())[i];
                last[i] = e.getValue()[i];
                d.setYValue(last[i]);
            }
        }
        
        return true;
    }
}
