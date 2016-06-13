/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.ui.cells;

import bgu.dcr.az.api.exen.Test;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Administrator
 */
public class TestComboboxCell extends ListCell<Test> {

    Label label = new Label();

    public TestComboboxCell() {
        label.setStyle("-fx-text-fill: black;");
    }

    @Override
    protected void updateItem(Test item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            label.setText(item.getName());
            setGraphic(label);
        }
    }

    public static Callback<ListView<Test>, ListCell<Test>> renderer() {
        return new Callback<ListView<Test>, ListCell<Test>>() {
            @Override
            public ListCell<Test> call(ListView<Test> param) {
                return new TestComboboxCell();
            }
        };
    }
}
