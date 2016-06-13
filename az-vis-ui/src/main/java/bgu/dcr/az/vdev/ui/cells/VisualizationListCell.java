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
package bgu.dcr.az.vdev.ui.cells;

import bgu.dcr.az.vdev.VisualizationMetadata;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 *
 * @author Administrator
 */
public class VisualizationListCell extends ListCell<VisualizationMetadata> {

    ImageView thumbnail = new ImageView();
    Label name = new Label();
    HBox layout = new HBox(3);

    public VisualizationListCell() {
        layout.getChildren().add(thumbnail);
        layout.getChildren().add(name);
        layout.setAlignment(Pos.CENTER_LEFT);
        
        thumbnail.setFitHeight(32);
        thumbnail.setFitWidth(32);
        thumbnail.getStyleClass().add("thumbnail");
    }

    @Override
    protected void updateItem(VisualizationMetadata item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null) {
            return;
        }

        name.setText(item.getName());
        thumbnail.setImage((Image) item.getVisualization().getThumbnail());
        setGraphic(layout);
    }

    public static Callback<ListView<VisualizationMetadata>, ListCell<VisualizationMetadata>> renderer() {
        return new Callback<ListView<VisualizationMetadata>, ListCell<VisualizationMetadata>>() {
            @Override
            public ListCell<VisualizationMetadata> call(ListView<VisualizationMetadata> param) {
                return new VisualizationListCell();
            }
        };
    }
}
