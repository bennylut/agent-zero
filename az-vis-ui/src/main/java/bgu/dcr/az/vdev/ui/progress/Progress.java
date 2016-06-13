/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.ui.progress;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class Progress implements Initializable {

    public VBox hidingBoxLeft;
    public VBox hidingBoxRight;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void toggle(){
        if (hidingBoxLeft.getStyleClass().contains("dark")){
            hidingBoxLeft.getStyleClass().remove("dark");
            hidingBoxLeft.getStyleClass().add("light");
            hidingBoxRight.getStyleClass().add("dark");
            hidingBoxRight.getStyleClass().remove("light");
        }else {
            hidingBoxRight.getStyleClass().remove("dark");
            hidingBoxRight.getStyleClass().add("light");
            hidingBoxLeft.getStyleClass().add("dark");
            hidingBoxLeft.getStyleClass().remove("light");
        }
    }
}
