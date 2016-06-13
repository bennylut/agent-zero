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

import bgu.dcr.az.exen.ExperimentImpl;
import bgu.dcr.az.exen.escan.ExperimentReader;
import com.sun.javafx.perf.PerformanceTracker;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Administrator
 */
public class AZDevJFX extends Application {

    File experimentFile;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Map<String, String> args = this.getParameters().getNamed();
        if (args.containsKey("efile")){
            experimentFile = new File(args.get("efile"));
        }else {
            experimentFile = new File("exp.xml");
        }
        
        //final ExperimentImpl exp = loadExperiment();
        //VisualizationsBufferManager vbuffer = VisualizationsBufferManager.create(exp);
        //VisualizationDrawerManager vdrawer = new VisualizationDrawerManager(exp, vbuffer);
        
        primaryStage.setTitle("Agent Zero Visualization Viewer");
        JFXs.maximizeWindow(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScreen.fxml"));

        Pane root = (Pane) loader.load();
        final Scene scene = new Scene(root);

        scene.getStylesheets().add(AZDevJFX.class.getResource("azVis.css").toExternalForm());

        primaryStage.setScene(scene);

        MainScreenController controller = (MainScreenController) loader.getController();
        //controller.setup(exp, vdrawer);
        primaryStage.show();

//        final PerformanceTracker trk = PerformanceTracker.getSceneTracker(scene);
//        Timer t = new Timer("", true);
//        t.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                System.out.println("AVG FPS: " + trk.getAverageFPS());
//            }
//        }, 0, 1000);
//        
//        new Thread(new Runnable(){
//
//            @Override
//            public void run() {
//                //exp.run();
//            }
//            
//        }).start();
    }

    private ExperimentImpl loadExperiment() {
        try {
            return (ExperimentImpl) ExperimentReader.read(experimentFile);
        } catch (IOException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(AZDevJFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.exit(0);
        return null;
    }
}
