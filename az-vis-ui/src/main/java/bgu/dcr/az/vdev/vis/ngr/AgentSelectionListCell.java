/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.ngr;

import bgu.dcr.az.vdev.vis.misc.AgentIcon;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Administrator
 */
public class AgentSelectionListCell extends ListCell<Integer> {

    AgentIcon ai;
    
    public AgentSelectionListCell() {
        ai = new AgentIcon();
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null) {
            return;
        }
        ai.setAgentId(item);
        setGraphic(ai);
    }

    public static Callback<ListView<Integer>, ListCell<Integer>> renderer() {
        return new Callback<ListView<Integer>, ListCell<Integer>>() {
            @Override
            public ListCell<Integer> call(ListView<Integer> param) {
                return new AgentSelectionListCell();
            }
        };
    }
}
