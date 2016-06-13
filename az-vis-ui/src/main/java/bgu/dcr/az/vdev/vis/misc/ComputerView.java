/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.misc;

import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Administrator
 */
public class ComputerView extends Region {

    private static Random r = new Random(42);
    private static final Image[] code;
    private static final int MAX_POWER_HEIGHT = 100;
    public static final int DOWN_SPEED = 3;
    private int screenReplaceAnimationSpeed = 0;
    private int aspeed;
    private int precentage = 0;
    private int showingPrecentage = 0;
    private ImageView iv = new ImageView();
    private Rectangle power;
    private Rectangle powerFrame;
    private Text idText;
    private Text handlingText;
    private Text totalCCText;

    static {
        code = new Image[]{
            new Image(ComputerView.class.getResourceAsStream("_cpu1.png")),
            new Image(ComputerView.class.getResourceAsStream("_cpu2.png")),
            new Image(ComputerView.class.getResourceAsStream("_cpu3.png")),
            new Image(ComputerView.class.getResourceAsStream("_cpu4.png")),
            new Image(ComputerView.class.getResourceAsStream("_cpu5.png"))
        };
    }
    private int totalCC = 0;

    public ComputerView() {

        getStyleClass().add("computer");
        aspeed = r.nextInt(10) + 10;
        iv.setImage(code[Math.abs(r.nextInt()) % code.length]);
        power = new Rectangle(64, 1, Color.YELLOWGREEN);
        getChildren().add(iv);
        getChildren().add(power);
        setPrefSize(192, 192);
        power.setTranslateX((getPrefWidth() - power.getWidth()) / 2);
        power.setTranslateY(getPrefHeight() - power.getHeight() - 10);
        
        powerFrame = new Rectangle();
        powerFrame.setTranslateX(power.getTranslateX()-1);
        powerFrame.setTranslateY(getPrefHeight() - MAX_POWER_HEIGHT - 10 - 1);
        powerFrame.setHeight(MAX_POWER_HEIGHT+1);
        powerFrame.setWidth(power.getWidth()+1);
//        powerFrame.setStroke(Color.DARKCYAN);
        powerFrame.setFill(Color.DARKCYAN.deriveColor(0, 0, 0, 0.6));
        getChildren().add(powerFrame);

        idText = new Text("0");
        idText.getStyleClass().add("idtext");
        idText.setTranslateX(20);
        idText.setTranslateY(25);
        getChildren().add(idText);


        totalCCText = new Text("Total CC: 0");
        totalCCText.getStyleClass().add("percentage-text");
        totalCCText.setTranslateX(idText.getTranslateX());
        totalCCText.setTranslateY(idText.getTranslateY() + 20);
        getChildren().add(totalCCText);

        handlingText = new Text("Idle");
        handlingText.getStyleClass().add("m-handling-text");
        handlingText.setTranslateX(totalCCText.getTranslateX());
        handlingText.setTranslateY(totalCCText.getTranslateY() + 20);
        getChildren().add(handlingText);
    }

    public void setAgentId(String id) {
        this.idText.setText(id);
    }

    public void draw() {
        screenReplaceAnimationSpeed = (screenReplaceAnimationSpeed + 1) % aspeed;
        if (screenReplaceAnimationSpeed == 0) {
            iv.setImage(code[Math.abs(r.nextInt()) % code.length]);
        }

        if (showingPrecentage > precentage) {
            showingPrecentage = Math.max(precentage, showingPrecentage - DOWN_SPEED);
        }else if (showingPrecentage < precentage){
            showingPrecentage = precentage;
        }
        
        power.setHeight(Math.max(1, MAX_POWER_HEIGHT * showingPrecentage / 100));
        power.setTranslateY(getPrefHeight() - power.getHeight() - 10);
//        percentageText.setText("" + showingPrecentage + "%");
//        percentageText.setTranslateY(power.getTranslateY() - 20);
//            setPrecentage(Math.max(0, precentage - 2), true);

    }

    public void setPrecentage(int precentage, boolean force) {
        this.precentage = precentage;
    }

    public void setTotalCC(int totalCC) {
        if (totalCC != this.totalCC) {
            this.totalCC = totalCC;
            totalCCText.setText("Total CC: " + totalCC);
        }
    }
    
    public void setHandling(String what){
        if (what == null || what.isEmpty()){
//            handlingText.setText("Idle");
        }else {
            handlingText.setText("Last Handle: " + what);
        }
    }
}
