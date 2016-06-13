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
