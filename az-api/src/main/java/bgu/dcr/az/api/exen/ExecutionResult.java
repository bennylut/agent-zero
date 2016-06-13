/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.tools.Assignment;

/**
 * TODO: hide all the to* so that correctness testers will not have the power to affect the result directly
 * @author bennyl
 */
public class ExecutionResult implements DeepCopyable {

    private Assignment finalAssignment = null;
    private Assignment correctAssignment = null;
    private Exception crushReason = null;
    private State state = State.UNKNOWN;
    private Execution resultingExecution;

    public ExecutionResult(Execution resultingExecution) {
        this.resultingExecution = resultingExecution;
    }

    @Override
    public String toString() {
        return state.toString(this);
    }

    public Execution getResultingExecution() {
        return resultingExecution;
    }

    public State getState() {
        return state;
    }

    public Assignment getCorrectAssignment() {
        return correctAssignment;
    }
    
    public ExecutionResult toSucceefulState(Assignment finalAssignment) {
        this.finalAssignment = finalAssignment;
        this.state = State.SUCCESS;
        return this;
    }

    /**
     * indicate that the execution was ended with timeout
     */
    public ExecutionResult toEndedByLimiterState() {
        this.state = State.LIMITED;
        return this;
    }

    
    public ExecutionResult toCrushState(Exception reason) {
        crushReason = reason;
        this.state = State.CRUSHED;
        return this;
    }
    
    public ExecutionResult toWrongState(Assignment currectAssignment){
        this.correctAssignment = currectAssignment;
        this.state = State.WRONG;
        return this;
    }

    public boolean hasSolution() {
        return this.finalAssignment != null;
    }

    public Assignment getAssignment() {
        return finalAssignment;
    }

    public Exception getCrushReason() {
        return crushReason;
    }

    @Override
    public ExecutionResult deepCopy() {
        ExecutionResult ret = new ExecutionResult(getResultingExecution());
        ret.state = this.state;
        ret.crushReason = this.crushReason;
        ret.finalAssignment = (this.finalAssignment == null ? null : this.finalAssignment.copy());
        return ret;
    }

    public static enum State {

        UNKNOWN {

            @Override
            public String toString(ExecutionResult er) {
                return "This Execution didnt ended yet so it result is unknown.";
            }
        },
        WRONG {

            @Override
            public String toString(ExecutionResult er) {
                return "This Execution ended with wrong results: it result was " + er.finalAssignment + " while example of a correct result is: " + er.correctAssignment;
            }
        },
        CRUSHED {

            @Override
            public String toString(ExecutionResult er) {
                return "The Execution crushed with the exception: " + (er.crushReason != null ? er.crushReason.getMessage() : "no-exception");
            }
        },
        LIMITED {

            @Override
            public String toString(ExecutionResult er) {
                return "The Execution was limited by the attached limiter " + (er.crushReason != null ? er.crushReason.getMessage() : "no-exception");
            }
        },
        SUCCESS {

            @Override
            public String toString(ExecutionResult er) {
                return "The Execution was ended successfully with the final assignment: " + er.finalAssignment;
            }
        };

        public abstract String toString(ExecutionResult er);
    }
}
