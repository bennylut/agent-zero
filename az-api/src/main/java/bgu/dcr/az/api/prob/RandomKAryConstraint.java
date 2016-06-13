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
