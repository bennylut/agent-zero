/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;

/**
 *
 * @author Administrator
 */
public interface VisualExecutionRunner extends bgu.dcr.az.api.exen.Process{
    
    /**
     * @return the execution that this runner is about to / already running
     */
    Execution getRunningExecution();
    /**
     * @return the visualizations that will get buffered while running the {@code getRunningExecution} execution
     */
    Visualization getLoadedVisualization();
    
    /**
     * @return the buffer that is collected by running the relevant execution
     */
    VisualizationFrameBuffer getLoadedVisualizationBuffer();
}
