/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;

/**
 *
 * @author Administrator
 */
public interface Visualization {
    public VisualizationDrawer loadOrRetreiveView(Object canvas);
    public void install(Execution ex, VisualizationFrameBuffer buffer);
    public Object getThumbnail();
    public String getDescriptionURL();
}
