/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
