/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.agt;

import bgu.dcr.az.api.Agent;

/**
 *
 * @author Inna
 */
public class ReportMediator {
    Object[] args;
    Agent a;

    public ReportMediator(Object[] args, Agent a) {
        this.args = args;
        this.a = a;
    }
    
    /**
     * send report to the given module name
     * @param who
     */
    public void to(String who){
        Agent.PlatformOperationsExtractor.extract(a).getExecution().report(who, a, args);
    }
}
