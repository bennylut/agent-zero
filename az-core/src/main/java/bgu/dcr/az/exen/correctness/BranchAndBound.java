/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.correctness;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import java.util.HashSet;

/**
 *
 * @author bennyl
 */
public class BranchAndBound {

    public static Assignment solve(Problem p) {
        return _solve(p, 0, new Assignment(), Integer.MAX_VALUE);
    }

    private static Assignment _solve(Problem p, int var, Assignment cpa, int ub) {
        
        if (p.getNumberOfVariables() == var) return cpa;
        HashSet<Integer> cd = new HashSet<>(p.getDomainOf(var));
        Assignment bcpa = null;
        
        while (! cd.isEmpty()){
            Integer best = cpa.findMinimalCostValue(var, cd, p);
            cpa.assign(var, best);
            cd.remove(best);
            
            if (cpa.calcCost(p) >= ub) {
                cpa.unassign(var);
                return bcpa;
            }
            
            Assignment temp = _solve(p, var+1, cpa, ub);
            
            
            if (temp != null){
                int cost = temp.calcCost(p);
                if (cost < ub){
                    ub = cost;
                    bcpa = temp.copy();
                } 
            }
            
            cpa.unassign(var);
        }
        
        return bcpa;
    }
}
