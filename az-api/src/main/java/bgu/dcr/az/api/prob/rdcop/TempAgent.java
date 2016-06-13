/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public class TempAgent extends Agent {

    RDCOP rdcop;

    @Override
    public void start() {
        rdcop = RDCOP.from(getProblem());
        PossibleProblem pp = rdcop.getPossibility(1);

        Assignment a = new Assignment(1, 1, 2, 2, 3, 3);
        
        utilityOf(a, pp);
        a.calcCost(pp);
    }

    protected int utilityOf(Assignment a, PossibleProblem p) {
        return -a.calcCost(p);
    }

}
