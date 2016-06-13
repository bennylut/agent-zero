/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob;

import bgu.dcr.az.api.tools.Assignment;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public class RandomKAryConstraint implements KAryConstraint {

    private static final long BIG_PRIME = 10123457689L;
    private static final long MEDIUM_PRIME = 101111L;
    private int base;
    private long seed;
    private int maxCostPlus;
    private long jump;
    private int[] participients;

    public RandomKAryConstraint(int numVars, int maxDomainSize, int maxCost, int seed, int[] participients) {
        this.base = Math.max(numVars, maxDomainSize);
        this.seed = seed * BIG_PRIME;
        this.maxCostPlus = maxCost + 1;
        this.jump = 101111 * Math.abs(new Random(seed).nextInt((int) MEDIUM_PRIME));
        this.participients = participients;
    }

    @Override
    public void getCost(Assignment a, ConstraintCheckResult result) {
        Random r = new Random();
        int cost = 0;
        for (int p : participients) {
            r.setSeed(seed + jump * (base * p + a.getAssignment(p)));
            cost += r.nextInt();
        }

        result.set(Math.abs(cost) % maxCostPlus, 1);
    }

    @Override
    public int[] getParicipients() {
        return participients;
    }

    @Override
    public String toString() {
        return "KAryConstraint{" + "participients=" + Arrays.toString(participients) + '}';
    }
}
