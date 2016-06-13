/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.vis;

import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.vis.VisualExecutionRunner;
import bgu.dcr.az.exen.AbstractExecution;

/**
 *
 * @author Administrator
 */
public class VisualExecutionRunnerImpl implements VisualExecutionRunner {

    private Execution execution;
    private Visualization visualization = null;
    private VisualizationFrameBuffer vfb;

    public VisualExecutionRunnerImpl(Execution execution) {
        this.execution = execution;
    }

    @Override
    public Execution getRunningExecution() {
        return execution;
    }

    public void setVisualization(Visualization vis) {
        visualization = vis;
    }

    @Override
    public boolean isFinished() {
        return execution.isFinished();
    }

    @Override
    public void stop() {
        execution.stop();
    }

    @Override
    public void run() {
        try {
            ((AbstractExecution) execution).initialize();
            vfb = new VisualizationFrameBuffer(execution);
            visualization.install(execution, vfb);
            execution.run();
//            ((AbstractExecution) execution).getThreadPool().shutdownNow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Visualization getLoadedVisualization() {
        return this.visualization;
    }

    @Override
    public VisualizationFrameBuffer getLoadedVisualizationBuffer() {
        return vfb;
    }
    
    public void startRunning(final Continuation andWhenDone){
        new Thread(new Runnable() {

            @Override
            public void run() {
                VisualExecutionRunnerImpl.this.run();
                andWhenDone.doContinue();
            }
        }).start();
    }
    
    public void startAnalyzing(final Continuation andWhenDone){
        new Thread(new Runnable() {

            @Override
            public void run() {
                vfb.prepereTimeLines();
                andWhenDone.doContinue();
            }
        }).start();
    }
}
