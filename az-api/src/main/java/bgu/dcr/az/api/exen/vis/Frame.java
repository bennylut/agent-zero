/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class Frame {
    private VisualizationFrameBuffer frameBuffer;
    private int number;
            
    public Frame(VisualizationFrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
        this.number = 0;
    }

    void setFrameNumber(int number) {
        this.number = number;
    }
    
    public Object getData(String delta){
        return frameBuffer.getData(number, delta);
    }
    
    public int getFrameNumber() {
        return number;
    }
}
