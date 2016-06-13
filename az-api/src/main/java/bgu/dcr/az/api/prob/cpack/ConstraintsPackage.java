/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
