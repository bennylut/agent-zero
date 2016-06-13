/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;

/**
 * this module represent a limiter for the execution 
 * this module can force the execution to stop if a condition was reached - for example
 * a time limit or a cc limit etc.
 * 
 * @author bennyl
 */
public interface Limiter {
    /**
     * this method will get called by the agent runners after each message processed 
     * so keep it implementation fast and small.
     * @param ex
     * @return 
     */
    boolean canContinue(Execution ex);
    
    /**
     * this method will get called before each execution start so you can initialize 
     * the module accordingly.
     * @param ex 
     */
    void start(Execution ex);
}
