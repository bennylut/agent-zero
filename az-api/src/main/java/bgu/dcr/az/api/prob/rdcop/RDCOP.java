/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
