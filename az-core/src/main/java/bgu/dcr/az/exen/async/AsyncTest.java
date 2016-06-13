/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.async;

import bgu.dcr.az.api.exen.escan.Configuration;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.mdef.MessageDelayer;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.tools.IdleDetector;
import bgu.dcr.az.exen.AbstractTest;

/**
 *
 * @author bennyl
 */
@Register(name = "async-test")
public class AsyncTest extends AbstractTest {

    MessageDelayer dman = null;

    public MessageDelayer getMessageDelayer() {
        return dman;
    }

    @Configuration(name = "Message Delayer", description = "message delayer to add message delays")
    public void setMessageDelayer(MessageDelayer dman) {
        this.dman = dman;
    }

    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        if (dman != null) {
            final AsyncDelayedMailer mailer = new AsyncDelayedMailer(dman, p.getNumberOfVariables());
            AsyncExecution ret = new AsyncExecution(p, alg, this, mailer, getExperiment());
//                        ret.setIdleDetector(idle.getSubDetector(ret.getAlgorithm().getAgentClass().getName()));
//            ret.setForceIdleDetection(true);
            dman.initialize(ret);
            return ret;
        } else {
            return new AsyncExecution(p, alg, this, getExperiment());
        }
    }
}
