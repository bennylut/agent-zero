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

import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.exen.mdef.ProblemGenerator;
import bgu.dcr.az.api.tools.Assignment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * test is a configurable execution - it is part of the expirement and it define 
 * the execution of the collection of problems + algorithms it contains
 * the test also contains the means to analyze the statistics that gathered during it execution
 * @author bennyl
 * 
 * TODO: MISSING FUNCTIONS FOR ADD AND REMOVE ALGORITHM METADATA (NEDD TO API THE ALGORITHM METADATA)
 * 
 */
public interface Test extends Process {
    
    /**
     * add test listener
     * @param l 
     */
    void addListener(TestListener l);

    /**
     * remove test listener
     * @param l 
     */
    void removeListener(TestListener l);

    /**
     * @return number of executions * number of algorithms in this test
     */
    int getTotalNumberOfExecutions();

    /**
     * @return
     * @deprecated use - get total number of executions instead as it is much more accurate
     */
    @Deprecated
    int getNumberOfExecutions();

    List<String> getIncludedAlgorithmsInstanceNames();

    /**
     * @return the name of this test
     */
    String getName();

    /**
     * the test seed - if there are any random elements on the test 
     * then this seed will give the ability to recreate the same test - if the seed is -1 
     * or not supplied (-1 is the default) then the seed will be the current time in miliseconds
     * @return the test seed 
     */
    long getSeed();

    /**
     * @return the variable name that this test is executing
     */
    String getRunningVarName();

    /**
     * @return the starting value of var
     */
    float getVarStart();

    /**
     * @return the ending value of var
     */
    float getVarEnd();

    /**
     * @return the amount to add to the run var after the repeat count
     */
    float getTickSize();

    /**
     * @return how many executions will be under each run var value
     */
    int getRepeatCount();

    /**
     * @return the current run var value
     */
    double getCurrentVarValue();

    /**
     * @return the test progress - when the test is running this function 
     * will return the current execution number between 0 and Test.getLength()
     */
    int getCurrentExecutionNumber();

    /**
     * @return this test problem generator
     */
    ProblemGenerator getProblemGenerator();

    /**
     * register statistic analayzer to this test
     * @param analyzer 
     */
    void addStatisticCollector(StatisticCollector analyzer);

    /**
     * @return list of all the registered statistics analayzers
     */
    List<StatisticCollector> getStatisticCollectors();

    /**
     * return statistic collector for the given type
     * @param type the class of the statistic collector
     * @return the statistic collector if it is loaded or null if it not.
     */
     <T extends StatisticCollector> T getStatisticCollector(Class<T> type);

    /**
     * return the test result after execution
     */
    TestResult getResult();

    CorrectnessTester getCorrectnessTester();

    int getCurrentProblemNumber();

    /**
     * you can set the correctness tester to null in order to remove it.
     * @param ctester 
     */
    void setCorrectnessTester(CorrectnessTester ctester);

    public String getCurrentExecutedAlgorithmInstanceName();

    /**
     * remove all the statistic collectors and the correction testers from the test 
     */
    public void removeInspectors();
    
    /**
     * if selector != null then on the next call to run 
     * only the execution that is selected using the given selector will get execute.
     * after the next call to run the selector will be dismissed and unless 
     * other one is specified using this method - all the executions will get execute.
     * @param selector 
     */
    public void select(ExecutionSelector selector);
    
    public static enum State {

        SUCCESS,
        WRONG_RESULT,
        CRUSH;
    }

    public static class TestResult {

        private State state;
        private Exception crushReason;
        private Assignment goodAssignment;
        private Execution badExecution;
        private Class failedClass;

        /**
         * constract successfull result
         */
        public TestResult() {
            this.state = State.SUCCESS;
            this.crushReason = null;
            this.goodAssignment = null;
            this.badExecution = null;
        }

        public State getState() {
            return state;
        }

        public TestResult toSuccessState(){
            this.state = State.SUCCESS;
            return this;
        }
        
        public TestResult toCrushState(Exception ex, Class failedAlgorithmClass){
            this.state = State.CRUSH;
            this.crushReason = ex;
            this.failedClass = failedAlgorithmClass;
            return this;
        }
        
        public TestResult toWrongResultState(Assignment good, Execution bad, Class failedAlgorithmClass){
            this.state = State.WRONG_RESULT;
            this.goodAssignment = good;
            this.badExecution = bad;
            this.failedClass = failedAlgorithmClass;
            return this;
        }

        public Class getFailedClass() {
            return failedClass;
        }

        public Exception getCrushReason() {
            return crushReason;
        }

        public Execution getBadExecution() {
            return badExecution;
        }

        public Assignment getGoodAssignment() {
            return goodAssignment;
        }
        
        
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("test result:\n").append("status= ").append(state);
            switch (state) {
                case CRUSH:
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    crushReason.printStackTrace(pw);
                    sb.append("crushReason: ").append(sw.toString());
                    break;
                case WRONG_RESULT:
                    sb.append("\nwrong assignment: ").append("").append(badExecution.getResult().getAssignment()).append(" with cost of = ").append(badExecution.getResult().getAssignment().calcCost(badExecution.getGlobalProblem())).append(" while good assignment is: ").append(goodAssignment.toString()).append(" with cost of = ").append(goodAssignment.calcCost(badExecution.getGlobalProblem()));
                    break;
            }
            return sb.toString();


        }
    }

    public static interface TestListener {

        void onTestStarted(Test source);

        void onExecutionStarted(Test source, Execution exec);

        void onExecutionEnded(Test source, Execution exec);
    }
}
