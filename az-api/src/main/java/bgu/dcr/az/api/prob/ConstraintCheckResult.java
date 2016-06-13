/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob;

/**
 * this class represents a constraint/s check result after one check a
 * constraint he expect to find two values - the first is the actual cost and
 * the second is the check cost for example if one checking the cost of
 * assignment a, and this check required 10 constraint checks after which the
 * cost of the assignment - c can be calculated then the result's cost is c and
 * the result's check cost is 10
 *
 * @author bennyl
 */
public class ConstraintCheckResult {

    int[] result = new int[2];

    /**
     * set the constraint result
     *
     * @param cost the cost of the constraint
     * @param checkCost how many constraint checks performed in order to
     * retrieve the given cost (usually 1) used for statistics
     */
    public void set(int cost, int checkCost) {
        result[0] = cost;
        result[1] = checkCost;
    }

    public int getCost() {
        return result[0];
    }

    public int getCheckCost() {
        return result[1];
    }
}
