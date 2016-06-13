/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.cpuc;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.timelines.CpuConsumptionChange;
import bgu.dcr.az.vdev.timelines.CurrentMessageHandlingChange;
import bgu.dcr.az.vdev.vis.misc.AgentIcon;
import bgu.dcr.az.vdev.vis.ngr.NetworkGraphVisualization;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;
import resources.img.ResourcesImgAnchor;

/**
 *
 * @author Administrator
 */
@Register(name = "cpu-cons-vis")
public class CpuConsumptionVisualization implements Visualization {

    CpuConsumptionVisualizationDrawer drawer = null;
    Pane view;
    private Execution ex;

    @Override
    public VisualizationDrawer loadOrRetreiveView(Object canvas) {
        if (drawer == null) {
            try {
                final String fxmlFile = getClass().getSimpleName() + "Drawer.fxml";
                final URL resource = getClass().getResource(fxmlFile);
                FXMLLoader loader = new FXMLLoader(resource);
                view = (Pane) loader.load();
                final Pane c = (Pane) canvas;
                c.getChildren().add(view);
                c.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        view.setPrefSize(c.getWidth(), c.getHeight());
                    }
                });
                drawer = (CpuConsumptionVisualizationDrawer) loader.getController();
                drawer.setup(ex);
            } catch (IOException ex) {
                Logger.getLogger(NetworkGraphVisualization.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return drawer;
    }

    @Override
    public void install(Execution ex, VisualizationFrameBuffer buffer) {
        CpuConsumptionChange.install(ex, buffer);
        CurrentMessageHandlingChange.install(ex, buffer, 1);
        this.ex = ex;
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
