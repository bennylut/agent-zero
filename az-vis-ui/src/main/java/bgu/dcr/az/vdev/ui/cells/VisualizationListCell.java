/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
