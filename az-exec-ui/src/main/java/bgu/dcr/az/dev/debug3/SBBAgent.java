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
package bgu.dcr.az.dev.debug3;

import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.tools.Assignment;

/**
 * the synchronous-back-bounding algorithm
 * should be used with Binary DCOP's only.
 * @author bennyl
 */
@Algorithm(name = "__SBB")
public class SBBAgent extends SimpleAgent {
    Assignment cpa, best;
    List<Integer> currentDomain;

    @Override
    public void start() {
        if (isFirstAgent()) {
            cpa = new Assignment();
            //copying the domain as it is immutable
            currentDomain = new LinkedList<Integer>(getDomain()); 
            assignCpa();
        }
    }

    private void assignCpa() {
        if (currentDomain.isEmpty()) {
            backtrack();
            return;
        }

        Integer minimum = cpa.findMinimalCostValue(getId(), currentDomain, getProblem());
        cpa.assign(getId(), minimum);
        currentDomain.remove(minimum);

        if (costOf(cpa) < costOf(best)) {
            if (isLastAgent()) {
                best = cpa.deepCopy();
                backtrack();
            } else {
                send("CPA", cpa).toNextAgent(); 
                //you can also use:
                //send("CPA", cpa).to(getId() + 1);
            }
        } else {
            backtrack();
        }
    }

    private void backtrack() {
        if (isFirstAgent()) {
            finish(best);
        } else {
            cpa.unassign(getId());
            send("BACKTRACK", cpa, best).toPreviousAgent();
        }
    }

    @WhenReceived("CPA")
    public void handleCPA(Assignment cpa) {
        this.cpa = cpa;
        currentDomain = new LinkedList<Integer>(getDomain());
        assignCpa();
    }

    @WhenReceived("BACKTRACK")
    public void handleBACKTRACK(Assignment cpa, Assignment best) {
        this.cpa = cpa;
        this.best = best;
        assignCpa();
    }
}
