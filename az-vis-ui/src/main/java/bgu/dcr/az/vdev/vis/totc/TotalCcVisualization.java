/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
