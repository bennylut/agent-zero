/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen;

import bgu.dcr.az.api.exen.Process;

/* TODO: remove event pipe implementation in favor of piped event bus implementation event bus */
/**
 * process is the basic unit that can run 
 * @author bennyl
 */
public abstract class AbstractProcess implements Process {

    private boolean finished;
    private Thread executingThread;

    public AbstractProcess() {
        finished = false;
    }

    @Override
    public final void stop() {
        if (!finished && executingThread != null) {
            finished = true;
            _stop();
        }
    }

    /**
     * this stop will cause the executing thread to be interrupted - you can change it 
     * if it is not your suitable logic
     */
    protected void _stop(){
        executingThread.interrupt();
    }
    
    @Override
    public final void run() {
        finished = false;
        executingThread = Thread.currentThread();
        _run();
        finished = true;
    }

    protected abstract void _run();

    @Override
    public boolean isFinished() {
        return finished;
    }

}
