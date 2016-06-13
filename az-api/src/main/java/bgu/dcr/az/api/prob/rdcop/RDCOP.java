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
package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.prob.ImmutableProblem;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author bennyl
 */
public class RDCOP {

    private Agent.AgentProblem underlineProblem;
    private Collection<DynamicElement> dynamicElements;

    private RDCOP(Agent.AgentProblem underlineProblem, Collection<DynamicElement> dynamicElements) {
        this.underlineProblem = underlineProblem;
        this.dynamicElements = dynamicElements;
    }

    public static RDCOP from(ImmutableProblem p) {
        Object delements = p.getMetadata().get(RDCOP.class.getCanonicalName());

        if (p instanceof Agent.AgentProblem && delements != null && delements instanceof Collection) {
            return new RDCOP((Agent.AgentProblem) p, ((Collection) delements));
        } else {
            throw new UnsupportedOperationException("does not support this type of problem.");
        }
    }

    public static void enhance(ImmutableProblem p, ArrayList<DynamicElement> delements) {
        delements.sort(null);
        p.getMetadata().put(RDCOP.class.getCanonicalName(), delements);
    }

    public long numberOfPossibilities(){
        long num = 1;
        for (DynamicElement d : dynamicElements){
            num *= d.getNumberOfPossibilities();
        }
        
        return num;
    }
    
    public PossibleProblem getPossibility(long possibility) {
        PatchableAgentProblem problem = new PatchableAgentProblem(underlineProblem);
        double probability = 1;
        for (DynamicElement d : dynamicElements){
            int p = (int) (possibility % d.getNumberOfPossibilities());
            possibility = possibility / d.getNumberOfPossibilities();
            probability *= d.getProbabilityFor(p);
            d.patch(p, problem);
        }
        
        problem.setProbability(probability);
        return problem;
    }
    
    public double getProbability(long possibility){
        double probability = 1;
        for (DynamicElement d : dynamicElements){
            int p = (int) (possibility % d.getNumberOfPossibilities());
            possibility = possibility / d.getNumberOfPossibilities();
            probability *= d.getProbabilityFor(p);
        }
        
        return probability;
    }
}
