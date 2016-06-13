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
package bgu.dcr.az.vdev.ui;

import azdevjfx.*;
import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.vis.VisualExecutionRunnerImpl;
import bgu.dcr.az.vdev.ui.progress.Progress;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Administrator
 */
public class TestUI extends Application {

    public static Class testClass = VisualizationChooseScreen.class;
    private static Stage primaryStage;
    private static Experiment exp;
    private static Pane vchooseScreen;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TestUI.primaryStage = primaryStage;
        primaryStage.setTitle("Agent Zero Algorithm Visualization Player");
        JFXs.maximizeWindow(primaryStage);
        final Scene scene = new Scene(new Pane());
        scene.getStylesheets().add(JFXs.class.getResource("azVis.css").toExternalForm());
        primaryStage.setScene(scene);
        loadSelectionScreen();
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                System.exit(42);
            }
        });
    }

    //executing algorithm -> analyzing data -> show
    public static void loadPlayScreen(Execution ex, Visualization vis) throws IOException {

        FXMLLoader loader = new FXMLLoader(Progress.class.getResource("Progress.fxml"));
        Pane ppane = (Pane) loader.load();
        primaryStage.getScene().setRoot(ppane);
        final Progress pcon = (Progress) loader.getController();

        final VisualExecutionRunnerImpl vr = new VisualExecutionRunnerImpl(ex);
        vr.setVisualization(vis);
        vr.startRunning(new Continuation() {
            @Override
            public void doContinue() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pcon.toggle();
                    }
                });

                vr.startAnalyzing(new Continuation() {
                    @Override
                    public void doContinue() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FXMLLoader loader = new FXMLLoader(PlayScreen.class.getResource("PlayScreen.fxml"));
                                    Pane pane = (Pane) loader.load();
                                    PlayScreen controller = (PlayScreen) loader.getController();
                                    controller.setup(primaryStage, vr);
                                    primaryStage.getScene().setRoot(pane);
                                } catch (IOException ex1) {
                                    Logger.getLogger(TestUI.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public static void loadSelectionScreen() throws Exception {
        if (vchooseScreen == null) {
            FXMLLoader loader = new FXMLLoader(VisualizationChooseScreen.class.getResource("VisualizationChooseScreen.fxml"));
            vchooseScreen = (Pane) loader.load();
            VisualizationChooseScreen controller = (VisualizationChooseScreen) loader.getController();
            exp = ExperimentReader.read(new File("test.xml"));
            controller.setExperiment(exp);
        }
        primaryStage.getScene().setRoot(vchooseScreen);
    }
}
