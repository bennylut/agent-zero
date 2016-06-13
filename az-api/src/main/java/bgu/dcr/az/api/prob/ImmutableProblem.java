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

import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public interface ImmutableProblem {

    /**
     * @return the problem type.
     */
    ProblemType type();

    /**
     *
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1=val1 when var2=val2
     */
    int getConstraintCost(int var1, int val1, int var2, int val2);

    /**
     *
     * @param var1
     * @param val1
     * @return the cost of assigning var1=val1
     */
    default int getConstraintCost(int var1, int val1) {
        return getConstraintCost(var1, val1, var1, val1);
    }

    /**
     * return the cost of the k-ary constraint represented by the given
     * assignment
     *
     * @param ass
     * @return
     */
    int getConstraintCost(Assignment ass);

    /**
     * return the domain of the given variable
     *
     * @param var
     * @return
     */
    ImmutableSet<Integer> getDomainOf(int var);

    /**
     * return the domain size of the variable var
     *
     * @param var
     * @return
     */
    int getDomainSize(int var);

    /**
     * @return this problem metadata
     */
    HashMap<String, Object> getMetadata();

    /**
     * @param var
     * @return all the variables that costrainted with the given var
     */
    Set<Integer> getNeighbors(int var);

    /**
     * @return the number of variables defined in this problem
     */
    int getNumberOfVariables();

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    default boolean isConsistent(int var1, int val1, int var2, int val2) {
        return getConstraintCost(var1, val1, var2, val2) == 0;
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2 operation
     * cost: o(d^2)cc
     */
    boolean isConstrained(int var1, int var2);

    /**
     * return the cost of the given assignment (taking into consideration all
     * the constraints that apply to it)
     *
     * @param a
     * @return
     */
    int calculateCost(Assignment a);

}
