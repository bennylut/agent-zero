/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.pgen;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name="adcop-unstructured")
public class UnstructuredADCOPGen extends UnstructuredDCOPGen {

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.ADCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, false, rand);
                }
            }
        }
    }

}
