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
package bgu.dcr.az.vdev.vis.totc;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.timelines.TotalCcChange;
import bgu.dcr.az.vdev.vis.misc.AgentIcon;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import resources.img.ResourcesImgAnchor;

/**
 *
 * @author Administrator
 */
@Register(name = "totc-vis")
public class TotalCcVisualization implements Visualization {

    Pane view;
    TotalCCVisualizationDrawer drawer = null;
    private int nagents;
    private String[] messages;

    @Override
    public VisualizationDrawer loadOrRetreiveView(Object canvas) {
        if (drawer == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(TotalCCVisualizationDrawer.class.getSimpleName() + ".fxml"));
                view = (Pane) loader.load();
                drawer = (TotalCCVisualizationDrawer) loader.getController();
                drawer.setup(nagents, messages);
                final Pane c = (Pane) canvas;
                c.getChildren().add(view);
                c.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        view.setPrefSize(c.getWidth(), c.getHeight());
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(TotalCcVisualization.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return drawer;
    }

    @Override
    public void install(Execution ex, VisualizationFrameBuffer buffer) {
        this.nagents = ex.getAgents().length;

        this.messages = Agent.PlatformOperationsExtractor.extract(ex.getAgents()[0]).algorithmMessages().toArray(new String[0]);
        if (ex.getSystemClock() != null) {
            String[] temp = messages;
            messages = new String[messages.length + 1];
            System.arraycopy(temp, 0, messages, 0, temp.length);
            messages[temp.length] = "__SYNC__";
        }
        TotalCcChange.install(ex, buffer);
    }

    @Override
    public Object getThumbnail() {
        return new Image(ResourcesImgAnchor.class.getResourceAsStream("camera.png"));
    }

    @Override
    public String getDescriptionURL() {
        return getClass().getResource("___desc.html").toExternalForm();
    }
}
