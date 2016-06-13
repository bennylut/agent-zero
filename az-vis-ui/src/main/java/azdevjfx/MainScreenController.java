/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package azdevjfx;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.exen.ExperimentImpl;
import com.sun.javafx.collections.ObservableListWrapper;
import java.net.URL;
import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 *
 * @author Administrator
 */
public class MainScreenController implements Initializable {

    /**
     * FX
     */
    public Pane canvas;
    public ListView<Visualization> visList;
//    private VisualizationDrawerManager vdraw;
    /**
     * Fields
     */
    
    

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //startVisualization();
    }

    public void setup(ExperimentImpl exp) {
//        this.vdraw = vdraw;
//        setupVisualizationList(exp);
        startVisualization();
    }

    private void startVisualization() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000/5), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
//                vdraw.play(canvas, null);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupVisualizationList() {
//        visList.setCellFactory(VisualizationListCell.createCallback());
//        List<Visualization> visualizations = exp.getVisualizations();
//        Agt0DSL.panicIf(visualizations.isEmpty(), "no visualizations to show");

//        visList.setItems(new ObservableListWrapper<>(visualizations));
        
        //select first item
        visList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Visualization>() {

            @Override
            public void onChanged(Change<? extends Visualization> arg0) {
//                vdraw.switchVisualization(arg0.getList().get(0));
            }
        });
//        visList.getSelectionModel().select(visualizations.get(0));
    }

}
