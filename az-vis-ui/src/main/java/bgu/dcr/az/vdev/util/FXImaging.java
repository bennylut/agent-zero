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
package bgu.dcr.az.vdev.util;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Narayan
 */
public class FXImaging{

    /**
     * Some of the important private variables;
     */
    private JFXPanel fxPanel;
    private  int TIME = 200;
    private File file ;
    private JFrame frame;
    private BoundingBox boundbox;
    private Timer timer;
    private Stage stage;
    private Scene scene;
    private ObservableList list;
    private Node node;

    /**
     *
     * @param scene the scene which is to be imaged
     * @param save place where the image to be saved
     * @param width width of image
     * @param height height of image
     */
    public void sceneToImage(final Scene scene,final File save,double width,double height){
        stage = (Stage)scene.getWindow();
        this.scene = scene;
        BoundingBox bound = null;
        if(width >0 && height >0){
            bound = new BoundingBox(0,0,width,height);
        }
        initAndShowGUI(scene,save,bound);
    }

    /**
     * Overload  function of sceneToImage
     *
     * @param scene
     * @param save
     */
    public void sceneToImage(final Scene scene,final File save){
        sceneToImage(scene,save,0,0);
    }

    /**
     * This function helps to save the Node to Image
     * and it's the only function which is public
     *
     * @param node node to be saved
     * @param list list of children where the node is kept
     * @param save place where the image to be saved
     */
    public void nodeToImage(final Node node,final ObservableList list ,final File save){
        nodeToImage(node,list,save,0,0);
    }

    /**
     * This function helps to save the Node to Image
     * and it's the only function which is public
     *
     * @param node the node to be saved
     * @param list the ObservableList of children where the node is kept
     * @param save place where the image to be saved
     * @param width width of image to be saved
     * @param height height of image to be saveed
     */
    public void nodeToImage(final Node node,final ObservableList list,final File save,final double width, final double height){
        stage = (Stage)node.getScene().getWindow();
        scene = node.getScene();
        this.node = node;
        this.list = list;
        BoundingBox bound = null;
        if(width > 0 && height > 0){
            bound = new BoundingBox(0,0,width,height);
        }
        initAndShowGUI(node,save,bound);
    }

    /**
     * This is the main function to generate the graphics of the Node
     * using the FXPanel inside the JFrame.
     * @param node
     * @param f
     * @param bound
     */
    private void initAndShowGUI(final Node node,File f, BoundingBox bound){
        Group root = new Group();
        Scene sc = new Scene(root);
        root.getChildren().add(node);
        initAndShowGUI(sc,f,bound);

    }

    /**
     * This is the main function to generate the graphics of the scene
     * using the FXPanel inside the JFrame.
     *
     * @param sc
     * @param f
     * @param bound
     */
    private void initAndShowGUI(final Scene sc,File f,BoundingBox bound) {
        file= f;
        if(bound == null)
            boundbox = new BoundingBox(0,0,stage.getWidth(),stage.getHeight()) ;
        else
            boundbox = bound ;

        frame = new JFrame();
        //Frame.setUndecorated(true);
        fxPanel = new JFXPanel();
        fxPanel.setScene(sc);
        fxPanel.addComponentListener(new ComponentListener(){
            public void componentResized(ComponentEvent e) {     

                ActionListener ac = new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        save(fxPanel,boundbox, file );
                        timer.stop();
                        fxPanel.removeAll();
                        restore();
                        frame.dispose();

                    }
               };
               //I've set timer for capturing image of the Node
               timer = new Timer(TIME, ac);
               timer.start();
            }
            public void componentMoved(ComponentEvent e) {
            }
            public void componentShown(ComponentEvent e) {
            }
            public void componentHidden(ComponentEvent e) {
            }
        });        

        frame.add(fxPanel);
        frame.setSize((int)boundbox.getWidth(),(int)boundbox.getHeight());
        if(stage !=null){
            frame.setLocation((int)stage.getX(), (int)stage.getY());
            Platform.runLater(new Runnable(){
                public void run(){
                    stage.hide();
                }

            });
        }
        frame.setVisible(true);

    }

    /**
     * This function saves the container as FXPanel
     * to the Image using the Java API
     * @param container
     * @param bounds
     * @param file
     */
    private void save(Container container, Bounds bounds, File file)  {
        try {
            String extension = "";
            String name = file.getName();
            if(name.contains(".")){
                int start= name.lastIndexOf(".");
                extension = file.getName().substring(start+1);

            }
            else{
                extension = "jpg";
            }
            ImageIO.write(toBufferedImage(container, bounds), extension, file);
            System.out.println("Node To Image saved");

        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "The image couldn't be saved","Error",JOptionPane.ERROR_MESSAGE);
            restore();
        }
    }

    /**
     * Restoring the scene or Node to it's original state
     */
    private void restore(){
        if(node!=null){
            restoreNode();
        }else{
            restoreScene();
        }
    }

    /**
     * Restores the Node
     */
    private void restoreNode(){
        Platform.runLater(new Runnable(){
            public void run(){
                list.add(node);
                stage.show();
            }

        });
    }

    /**
     * This function restores the main Scene to the original Stage
     * from where the event has been triggered
     */
    private void restoreScene(){
        Platform.runLater(new Runnable(){
            public void run(){
                stage.setScene(scene);
                stage.show();
            }

        });
    }

    /**
     * This function is used to get the BufferedImage of the
     * Container as JFXPanel
     * @param container
     * @param bounds
     * @return
     */
    private BufferedImage toBufferedImage(Container container, Bounds bounds) {
        BufferedImage bufferedImage = new BufferedImage(
                (int) bounds.getWidth(),
                (int) bounds.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = bufferedImage.getGraphics();
        graphics.translate((int) -bounds.getMinX(), (int) -bounds.getMinY()); // translating to upper-left corner
        container.paint(graphics);
        graphics.dispose();
        return bufferedImage;
    }

}