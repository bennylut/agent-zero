/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.misc;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 *
 * @author Administrator
 */
public class CpuView extends Region{

    public static Image cpuImage = new Image(AgentIcon.class.getResourceAsStream("_cpu.png"));
    
    private ImageView iv = new ImageView(cpuImage);
    private Text t = new Text("CPU Model:\nAZ2000\nFREQ:\n100cc/f.");
    
    
    public CpuView() {
        iv.setFitHeight(128);
        iv.setFitWidth(128);
        getChildren().add(iv);
        t.setTranslateX(30);
        t.setTranslateY(34);
        getChildren().add(t);
        t.getStyleClass().add("cpu-text");
    }
    
    
}
