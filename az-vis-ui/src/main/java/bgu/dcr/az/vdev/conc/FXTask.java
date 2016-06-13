/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.conc;

import bgu.dcr.az.vdev.AZSystem;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 *
 * @author Administrator
 */
public abstract class FXTask extends Task {

    @Override
    protected final void done() {
        super.done();
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                doWhenBackToUI();
            }
        });
    }

    @Override
    protected final Object call() throws Exception {
        doOutsideOfUI();
        return null;
    }
    
    public void submitToSystemPool(){
        AZSystem.thread(this);
    }
    
    protected abstract void doOutsideOfUI();
    protected abstract void doWhenBackToUI();
}
