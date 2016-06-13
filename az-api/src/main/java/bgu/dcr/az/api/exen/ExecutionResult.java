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
