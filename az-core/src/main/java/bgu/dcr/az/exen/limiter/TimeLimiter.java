/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.limiter;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Limiter;

/**
 *
 * @author bennyl
 */
@Register(name="time-limiter")
public class TimeLimiter implements Limiter{

    @Variable(name="seconds", defaultValue="120", description="number of seconds before terminating the execution")
    long seconds;
    long endMilis = -1;
    

    @Override
    public boolean canContinue(Execution ex) {
        return System.currentTimeMillis() < endMilis;
    }

    @Override
    public void start(Execution ex) {
        endMilis = System.currentTimeMillis() + seconds * 1000;
    }
    
}
