/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * expirement is executeable collection of predefined tests 
 * the expirement responsible to configure its sub executions 
 * based on its loaded tests and analyzed their statistics via the statistics analayzers defined in the test
 * @author bennyl
 */
public interface Experiment extends Process {

    void addTest(Test test);

    /**
     * remove statistic collectors and correctness testers from all the tests in the given experiment
     */
    void removeInspectorsFromAllTests();
    
    /**
     * @return the number of executions in this experiment
     */
    int getTotalNumberOfExecutions();
    
    /**
     * return list of all the tests that are loaded to this experiment object
     */
    List<Test> getTests();
    
    /**
     * @return the experiment result - this is valid only if the experiment already done
     */
    ExperimentResult getResult();

    /**
     * add experiment listener
     */
    void addListener(ExperimentListener l);
    
    /**
     * remove experiment listeners
     * @param l 
     */
    void removeListener(ExperimentListener l);
    
    /**
     * return the global thread pool used by this experiment
     */
    ExecutorService getThreadPool();
    
    public static class ExperimentResult {

        public final boolean succeded;
        public final Test problematicTest;
        public final Test.TestResult badTestResult;
        public final boolean interupted;

        /**
         * constract successfull/interupted result
         */
        public ExperimentResult(boolean interupted) {
            this.succeded = !interupted;
            this.problematicTest = null;
            this.badTestResult = null;
            this.interupted = interupted;
        }

        /**
         * constract faild result
         * @param problematicTest
         * @param badTestResult 
         */
        public ExperimentResult(Test problematicTest, Test.TestResult badTestResult) {
            this.succeded = false;
            this.problematicTest = problematicTest;
            this.badTestResult = badTestResult;
            this.interupted = false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Expirement");
            if (succeded) {
                sb.append(" succeded");
            } else if (!interupted) {
                sb.append(" failed: ");
                sb.append(badTestResult.toString());
            } else {
                sb.append("interrupted");
            }
            return sb.toString();
        }
    }

    public static interface ExperimentListener {

        void onExpirementStarted(Experiment source);

        void onExpirementEnded(Experiment source);

        void onNewTestStarted(Experiment source, Test test);

        void onNewExecutionStarted(Experiment source, Test test, Execution exec);

        void onExecutionEnded(Experiment source, Test test, Execution exec);
    }
}
