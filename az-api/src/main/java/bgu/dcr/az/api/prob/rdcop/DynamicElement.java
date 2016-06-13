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
package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.prob.Problem;

/**
 *
 * @author bennyl
 */
public abstract class DynamicElement implements Comparable<DynamicElement> {

    public abstract int getNumberOfPossibilities();

    public abstract double getProbabilityFor(int possibility);

    public abstract void patch(int possibility, PatchableAgentProblem p);

    @Override
    public int compareTo(DynamicElement o) {
        PatchOrder order = desidePatchOrder(o);
        switch (order) {
            case BEFORE:
                return -1;
            case AFTER:
                return 1;
            case INDIFFERENT:
                return 0;
            default:
                throw new AssertionError(order.name());

        }
    }

    public abstract PatchOrder desidePatchOrder(DynamicElement o);

    public enum PatchOrder {

        BEFORE, AFTER, INDIFFERENT;
    }

}
