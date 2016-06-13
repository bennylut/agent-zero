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
