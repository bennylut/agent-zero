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

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.Registery;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.exen.AbstractTest;
import bgu.dcr.az.vdev.ExecutionMetadata;
import bgu.dcr.az.vdev.ExecutionMetadata.InnerMetadata;
import bgu.dcr.az.vdev.VisualizationMetadata;
import bgu.dcr.az.vdev.ui.cells.TestComboboxCell;
import bgu.dcr.az.vdev.ui.cells.VisualizationListCell;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 *
 * @author Administrator
 */
public class VisualizationChooseScreen implements Initializable {

    ////////////////////////////////////////////////////////////////////////////
    ///                             UI FIELDS                                ///
    ////////////////////////////////////////////////////////////////////////////
    public VBox root;
    public ImageView selectedVisualizationImage;
    public GridPane currentExecutionInformation;
    public ComboBox<Test> availableTests;
    public Slider executionSelectionSlider;
    public TextField executionSelectionText;
    public ListView<VisualizationMetadata> vmetaList;
    public WebView currentVisualizationDescription;
    public Label visualizationNameLabel;
    public Button playButton;
    ////////////////////////////////////////////////////////////////////////////
    ///                           NORMAL FIELDS                              ///
    ////////////////////////////////////////////////////////////////////////////
    private Experiment exp;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        availableTests.setCellFactory(TestComboboxCell.renderer());

        executionSelectionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                onExecutionSelectionSliderChanged(newValue, oldValue);
            }
        });

        executionSelectionText.textProperty().set("" + 0);
        executionSelectionText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                onExecutionSelectionTextChanged(newValue, oldValue);
            }
        });

        availableTests.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Test>() {
            @Override
            public void changed(ObservableValue<? extends Test> observable, Test oldValue, Test newValue) {
                onSelectedTestChanged(newValue);
            }
        });

        //visualization meta list
        vmetaList.setCellFactory(VisualizationListCell.renderer());
        Set<String> foundVis = Registery.UNIT.getExtendingEntities(Visualization.class);
        vmetaList.getItems().addAll(VisualizationMetadata.load(foundVis));
        vmetaList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<VisualizationMetadata>() {
            @Override
            public void onChanged(Change<? extends VisualizationMetadata> c) {
                final VisualizationMetadata meta = c.getList().get(0);
                selectedVisualizationImage.setImage((Image) meta.getVisualization().getThumbnail());
                currentVisualizationDescription.getEngine().load(meta.getVisualization().getDescriptionURL());
                visualizationNameLabel.setText(meta.getName());
            }
        });
        vmetaList.getSelectionModel().selectFirst();

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    onPlayPressed();
                } catch (IOException ex) {
                    Logger.getLogger(VisualizationChooseScreen.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////
    ///                          EVENT HANDLERS                              ///
    ////////////////////////////////////////////////////////////////////////////
    private void onPlayPressed() throws IOException {
        AbstractTest at = (AbstractTest) availableTests.getSelectionModel().getSelectedItem();
        Execution ex = at.buildExecution((int) Math.round(executionSelectionSlider.getValue()));
        VisualizationMetadata vm = vmetaList.getSelectionModel().getSelectedItem();
        TestUI.loadPlayScreen(ex, vm.getNewVisualization());
    }

    private void onExecutionSelectionSliderChanged(Number newValue, Number oldValue) {
        System.out.println("executionSelectionSlider > " + newValue + " >> " + cint(newValue));
        if (cint(oldValue) != cint(newValue)) {
            executionSelectionText.textProperty().set("" + cint(newValue));
        }
    }

    private void onExecutionSelectionTextChanged(String newValue, String oldValue) {
        System.out.println("executionSelectionText");
        try {
            int value = (newValue.isEmpty() ? 0 : Integer.valueOf(newValue));
            if (value > executionSelectionSlider.getMax()) {
                executionSelectionText.textProperty().set("" + (int) executionSelectionSlider.getMax());
            } else {
                if (cint(executionSelectionSlider.valueProperty().getValue()) != value) {
                    executionSelectionSlider.setValue(value);
                }

                updateSelectedExecution(value);
            }
        } catch (NumberFormatException ex) {
            executionSelectionText.textProperty().set(oldValue);
        }
    }

    private void onSelectedTestChanged(Test newValue) {
        executionSelectionSlider.setMax(newValue.getTotalNumberOfExecutions());
        executionSelectionSlider.setValue(0);
        updateSelectedExecution(0);
    }

    public void setExperiment(Experiment exp) {
        this.exp = exp;
        ObservableList<Test> availableTestsItems = availableTests.getItems();
        availableTestsItems.clear();
        availableTestsItems.setAll(exp.getTests());
        availableTests.getSelectionModel().select(exp.getTests().get(0));

        updateSelectedExecution(0);
    }

    private void updateSelectedExecution(final int executionNumber) {
        ExecutionMetadata metadata;
        LinkedList<Node> guiPrebuild;
        int rowsAdded = 0;

        metadata = new ExecutionMetadata(availableTests.getSelectionModel().getSelectedItem(), executionNumber);
        guiPrebuild = new LinkedList<>();

        for (InnerMetadata im : metadata.getInnerMeta()) {

            //module type 
            final Label moduleNameLabel = new Label(im.module + ": ");
            guiPrebuild.add(moduleNameLabel);
            GridPane.setConstraints(moduleNameLabel, 0, rowsAdded);
            moduleNameLabel.getStyleClass().add("metadata-type");

            //module name 
            final Label NameLabel = new Label(im.name);
            guiPrebuild.add(NameLabel);
            GridPane.setConstraints(NameLabel, 1, rowsAdded++);
            NameLabel.getStyleClass().add("metadata-name");


            //todo: after we will se how it like add the variables.. probably in flow panel
            FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 5, 5);
            for (Entry<String, String> e : im.variables.entrySet()) {
                Label lbl = new Label(e.getKey());
                lbl.getStyleClass().add("metadata-var-name");
                flowPane.getChildren().add(lbl);

                lbl = new Label("=");
                lbl.getStyleClass().add("metadata-var-eqs");
                flowPane.getChildren().add(lbl);

                lbl = new Label(e.getValue() + " |");
                lbl.getStyleClass().add("metadata-var-value");
                flowPane.getChildren().add(lbl);
            }
            guiPrebuild.add(flowPane);
            GridPane.setConstraints(flowPane, 1, rowsAdded++);
        }

        ObservableList<Node> children = currentExecutionInformation.getChildren();
        children.clear();
        children.addAll(guiPrebuild);
        currentExecutionInformation.getRowConstraints().clear();
        for (int i = 0; i < rowsAdded; i++) {
            currentExecutionInformation.getRowConstraints().add(new RowConstraints(0, 20, 20));
        }

    }

    ////////////////////////////////////////////////////////////////////////////
    ///                               HELPERS                                ///
    ////////////////////////////////////////////////////////////////////////////
    private int cint(Number newValue) {
        return (int) Math.round(newValue.doubleValue());
    }
}
