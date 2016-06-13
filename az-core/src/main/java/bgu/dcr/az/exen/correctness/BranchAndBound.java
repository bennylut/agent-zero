/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
