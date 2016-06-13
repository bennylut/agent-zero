/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.sync;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.exen.AbstractTest;

/**
 *
 * @author bennyl
 */
@Register(name="sync-test")
public class SyncTest extends AbstractTest {


    @Override
    protected Execution provideExecution(Problem p, AlgorithmMetadata alg) {
        return new SyncExecution(p, alg, this, getExperiment());
    }
}
