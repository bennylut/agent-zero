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
