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
