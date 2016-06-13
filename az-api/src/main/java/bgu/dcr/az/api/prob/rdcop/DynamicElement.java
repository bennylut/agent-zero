/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
