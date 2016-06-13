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
package bgu.dcr.az.api.prob.cpack;

import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.tools.Assignment;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class AsymmetricBinaryMapConstraintPackage extends BinaryMapConstraintPackage {

    public AsymmetricBinaryMapConstraintPackage(int numvar, int maxDomainSize) {
        super(numvar, maxDomainSize);
    }

    @Override
    public void calculateCost(int owner, Assignment k, ConstraintCheckResult result) {
        if (!k.isAssigned(owner)) { //if owner is not assigned then its cost is 0 because it is not conflicting with any value.
            result.set(0, 0);
            return;
        }
        int value = k.getAssignment(owner);
        int c = 0;
        int cc = 0;

        for (Map.Entry<Integer, Integer> e : k.getAssignments()) {
            int var = e.getKey();
            int val = e.getValue();
            getConstraintCost(owner, owner, value, var, val, result);
            c += result.getCost();
            cc += result.getCheckCost();
        }

        result.set(c, cc);
    }

    @Override
    public int calculateGlobalCost(Assignment assignment) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        int c = 0;
        LinkedList<Map.Entry<Integer, Integer>> past = new LinkedList<Map.Entry<Integer, Integer>>();

        for (Map.Entry<Integer, Integer> e : assignment.getAssignments()) {
            int var = e.getKey();
            int val = e.getValue();
            getConstraintCost(var, var, val, result);
            c += result.getCost();

            for (Map.Entry<Integer, Integer> pe : past) {
                int pvar = pe.getKey();
                int pval = pe.getValue();

                getConstraintCost(pvar, pvar, pval, var, val, result);
                c += result.getCost();
                getConstraintCost(var, var, val, pvar, pval, result);
                c += result.getCost();
            }

            past.add(e);
        }

        return c;
    }
}
