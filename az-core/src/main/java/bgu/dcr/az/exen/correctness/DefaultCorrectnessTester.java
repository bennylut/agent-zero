/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.correctness;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.exen.correctness.IterativeCSPSolver.Status;

/**
 *
 * @author bennyl
 */
@Register(name="default-tester")
public class DefaultCorrectnessTester extends AbstractCorrectnessTester {

    @Override
    public CorrectnessTestResult test(Execution exec, ExecutionResult result) {
        if (result.getState() != ExecutionResult.State.SUCCESS){
            return new CorrectnessTestResult(null, true); //cannot check that..
        }
        
        Assignment ass;
        final Problem globalProblem = exec.getGlobalProblem();
        Status stat;
        final MACSolver solver = new MACSolver();
        switch (exec.getGlobalProblem().type()) {
            case ADCOP:
                return new CorrectnessTestResult(null, true);
            case DCOP:
                ass = BranchAndBound.solve(globalProblem);
                if (ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                    return new CorrectnessTestResult(ass, true);
                } else {
                    return new CorrectnessTestResult(ass, false);
                }
            case DCSP:
                stat = solver.solve(globalProblem);
                switch (stat) {
                    case imposible:
                        if (result.hasSolution()) {
                            return new CorrectnessTestResult(null, false);
                        } else {
                            return new CorrectnessTestResult(null, true);
                        }
                    case solution:
                        try{
                        ass = solver.getAssignment();
                        
                        if (result.getAssignment() != null && ass.calcCost(globalProblem) == result.getAssignment().calcCost(globalProblem)) {
                            return new CorrectnessTestResult(ass, true);
                        } else {
                            return new CorrectnessTestResult(ass, false);
                        }
                        }catch(Exception e){
                            System.out.println("HERE!!!");
                        }
                    default:
                        return new CorrectnessTestResult(null, true);
                }
            default:
                return null;
        }
    }
}
