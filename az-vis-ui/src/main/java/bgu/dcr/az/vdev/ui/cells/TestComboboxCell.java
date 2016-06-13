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
