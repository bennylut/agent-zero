/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.misc;

import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Administrator
 */
public class AgentIcon extends Region {

    public static Image agentImage = new Image(AgentIcon.class.getResourceAsStream("_agent.png"));
    ImageView iv = new ImageView(agentImage);
    Text idText = new Text("0");
    int id = 0;

    public AgentIcon() {
        initialize();
    }

    public void setAgentId(int id) {
        this.id = id;
        idText.setText("" + id);
        _layout();
    }

    public void glow(Color color) {
        iv.setEffect(null);
        iv.setEffect(DropShadowBuilder.create().radius(20).color(color).build());
    }

    public void removeGlow() {
        iv.setEffect(null);
    }

    public void initialize() {
        //Image
        iv = new ImageView(AgentIcon.agentImage);
        iv.setFitHeight(48);
        iv.setFitWidth(48);
        getChildren().add(iv);

        idText.setFill(Color.CORAL);

        idText.setFont(Font.font("Verdana", 17));
        getChildren().add(idText);
        idText.setMouseTransparent(true);
        _layout();

    }

    private void _layout() {
        //Id test
        if (id >= 10) {
            idText.setX(iv.getX() + 13);
        } else {
            idText.setX(iv.getX() + 20);
        }

        idText.setY(iv.getY() + 40);
    }
}
