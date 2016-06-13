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
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.tools.Assignment;
import java.util.Set;

/**
 * this class represent a data structure that hold and query about constraints
 *
 * @author bennyl
 */
public interface ConstraintsPackage {

    /**
     * K-Ary version
     *
     * @param a
     * @param cost
     */
    public void setConstraintCost(int owner, KAryConstraint constraint);

    public void addConstraintCost(int owner, KAryConstraint constraint);

    /**
     * Binary version
     *
     * @param x1
     * @param v1
     * @param x2
     * @param v2
     * @param cost
     */
    public void setConstraintCost(int owner, int x1, int v1, int x2, int v2, int cost);

    /**
     * Unary version
     *
     * @param x1
     * @param v1
     * @param cost
     */
    public void setConstraintCost(int owner, int x1, int v1, int cost);

    /**
     * Unary version
     *
     * @param x1
     * @param v1
     * @return
     */
    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result);

    /**
     * Binary version
     *
     * @param x1
     * @param v1
     * @param x2
     * @param v2
     * @return
     */
    public void getConstraintCost(int owner, int x1, int v1, int x2, int v2, ConstraintCheckResult result);

    /**
     * K-ary version
     *
     * @param k
     * @return
     */
    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result);

    /**
     * return the set of neighbor's belongs to the variable xi
     *
     * @param xi
     * @return
     */
    public Set<Integer> getNeighbores(int xi);

    /**
     * add neighbor to the set of neighborer's belongs to 'to'
     *
     * @param to
     * @param neighbor
     */
    public void addNeighbor(int to, int neighbor);

    /**
     * return the cost of the given assignment from the given owner point of view inside the parameter result
     * the result parameter is an array of 2: [cost, number of constraint checks required]
     *
     * @param owner
     * @param a
     * @return
     *
     */
    public void calculateCost(int owner, Assignment a, ConstraintCheckResult result);

    /**
     * calculate the cost relative to the system - means that this cost is calculated to be the sum of costs of all owners possible
     *
     * @param a
     * @return
     */
    public int calculateGlobalCost(Assignment a);
}
