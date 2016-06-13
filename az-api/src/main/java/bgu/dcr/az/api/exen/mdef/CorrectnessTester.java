/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.tools.Assignment;

/**
 *
 * @author bennyl
 */
public interface CorrectnessTester{
    
    CorrectnessTestResult test(Execution exec, ExecutionResult result);
    
    public static class CorrectnessTestResult{
        public final Assignment rightAnswer;
        public final boolean passed;

        public CorrectnessTestResult(Assignment rightAnswer, boolean passed) {
            this.rightAnswer = rightAnswer;
            this.passed = passed;
        }
    }
}
