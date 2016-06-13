/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.ui;

import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualExecutionRunner;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Administrator
 */
public class PlayScreen implements Initializable {
    ////////////////////////////////////////////////////////////////////////////
    ///                             UI FIELDS                                ///
    ////////////////////////////////////////////////////////////////////////////

    public Button fullScreenButton;
    public Button playButton;
    public Pane canvas;
    public Label currentFrameLabel;
    public Slider frameSlider;
    public Button ejectButton;
    ////////////////////////////////////////////////////////////////////////////
    ///                           NORMAL FIELDS                              ///
    ////////////////////////////////////////////////////////////////////////////
    private Stage stage;
    private VisualExecutionRunner vrunner;
    private VisualizationDrawer drawer;
    private BooleanProperty pause = new SimpleBooleanProperty(false);
    ////////////////////////////////////////////////////////////////////////////
    ///                        INITIALIZATION CODE                           ///
    ////////////////////////////////////////////////////////////////////////////
    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeMediaControllers();
        ejectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    timeline.stop();
                    TestUI.loadSelectionScreen();
                } catch (Exception ex) {
                    Logger.getLogger(PlayScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void setup(Stage stage, final VisualExecutionRunner ver) {
        frameSlider.setMin(0);
        frameSlider.setMax(ver.getLoadedVisualizationBuffer().numberOfFrames());

        this.stage = stage;
        this.vrunner = ver;
        this.drawer = ver.getLoadedVisualization().loadOrRetreiveView(canvas);

        timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 40), new EventHandler<ActionEvent>() {
            private VisualizationFrameBuffer buf = ver.getLoadedVisualizationBuffer();
            private Frame next = null;

            @Override
            public void handle(ActionEvent arg0) {
                currentFrameLabel.setText("current frame: " + buf.getCurrentFrame());
                if (buf.isFrameAutoIncreaseEnabled()) {
                    frameSlider.valueProperty().set(buf.getCurrentFrame());
                }

                if (next == null) {
                    next = buf.nextFrame();
                }

                if (next != null) {
                    if (drawer.draw(canvas, next)) {
                        next = null;
                    }
                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setDelay(Duration.seconds(1));
        timeline.play();
    }

    private void initializeMediaControllers() {
        fullScreenButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onFullScreenButtonClicked();
            }
        });

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pause.set(!pause.get());
                vrunner.getLoadedVisualizationBuffer().setFrameAutoIncreaseEnabled(!pause.get());
            }
        });

        frameSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                vrunner.getLoadedVisualizationBuffer().setFrameAutoIncreaseEnabled(false);
            }
        });

        frameSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                vrunner.getLoadedVisualizationBuffer().setFrameAutoIncreaseEnabled(!pause.get());
            }
        });

        frameSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int frame = (int) Math.round(newValue.doubleValue());
                if (frame != vrunner.getLoadedVisualizationBuffer().getCurrentFrame()) {
                    vrunner.getLoadedVisualizationBuffer().gotoFrame(frame);
                }
            }
        });

        frameSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                vrunner.getLoadedVisualizationBuffer().gotoFrame((int) Math.round(frameSlider.getValue()));
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////
    ///                        EVENT HANDLERS                                ///
    ////////////////////////////////////////////////////////////////////////////
    private void onFullScreenButtonClicked() {
        stage.setFullScreen(true);
    }
}
