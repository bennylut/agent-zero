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
package bgu.dcr.az.api.prob;

import bgu.dcr.az.api.tools.Assignment;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class ComposedKAryConstraint implements KAryConstraint {

    private static final int[] EMPTY_ARRAY = new int[0];
    private List<KAryConstraint> composition = new LinkedList<KAryConstraint>();

    @Override
    public void getCost(Assignment a, ConstraintCheckResult result) {
        if (composition.isEmpty()) {
            result.set(0, 1);
        } else {
            int cost = 0;
            int cc = 0;
            for (KAryConstraint c : composition) {
                c.getCost(a, result);
                cost += result.getCost();
                cc += result.getCheckCost();
            }

            result.set(cost, cc);
        }
    }

    public List<KAryConstraint> getComposition() {
        return composition;
    }

    @Override
    public int[] getParicipients() {
        if (composition.isEmpty()) {
            return EMPTY_ARRAY;
        } else {
            return composition.get(0).getParicipients();
        }
    }
}
