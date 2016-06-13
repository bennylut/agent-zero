/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;

/**
 *
 * @author Administrator
 */
public interface VisualizationDrawer<IMAGE_IMPL, CANVAS_IMPL> {
    public boolean draw(CANVAS_IMPL canvas, Frame frame);
}
