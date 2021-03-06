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
package bgu.dcr.az.vdev.vis.cpuc;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.vdev.timelines.CpuConsumptionChange;
import bgu.dcr.az.vdev.timelines.CurrentMessageHandlingChange;
import bgu.dcr.az.vdev.util.FXImaging;
import bgu.dcr.az.vdev.vis.misc.ComputerView;
import bgu.dcr.az.vdev.vis.misc.CpuView;
import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class CpuConsumptionVisualizationDrawer implements Initializable, VisualizationDrawer<Image, Pane> {

    public TilePane back;
    public VBox cpuPan;
    private ComputerView[] cvs;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void save() {
        Random r = new Random();
        for (ComputerView cv : cvs) {
            new FXImaging().nodeToImage(cv, back.getChildren(), new File("" + r.nextInt() + ".png"));
        }
    }

    public void setup(Execution ex) {
        cvs = new ComputerView[ex.getAgents().length];
        for (int i = 0; i < cvs.length; i++) {
            cvs[i] = new ComputerView();
            cvs[i].setAgentId("" + i);
            back.getChildren().add(cvs[i]);
        }

        final CpuView cpuView = new CpuView();
        cpuPan.getChildren().add(cpuView);
    }

    @Override
    public boolean draw(Pane canvas, Frame frame) {
        int[][] data = CpuConsumptionChange.extract(frame);
        for (int i = 0; i < cvs.length; i++) {
            cvs[i].setPrecentage((int) (data[0][i]), false);
            cvs[i].setTotalCC(data[1][i]);
            cvs[i].setHandling(CurrentMessageHandlingChange.extract(frame).get(i));
            cvs[i].draw();
        }
        return true;
    }
}
