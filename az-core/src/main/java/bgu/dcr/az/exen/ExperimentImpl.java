/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen;

import bgu.dcr.az.api.exen.ExecutionSelector;
import bgu.dcr.az.api.exen.escan.Configuration;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test.TestResult;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.ExternalConfigurationAware;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author bennyl
 */
@Register(name = "experiment")
public class ExperimentImpl extends AbstractProcess implements Experiment, Test.TestListener, ExternalConfigurationAware {

    private List<Test> tests = new ArrayList<Test>();
    private ExperimentResult result;
    private LinkedList<Experiment.ExperimentListener> listeners = new LinkedList<ExperimentListener>();
    private List<Visualization> visualizations = new ArrayList<Visualization>();
    private ExecutionSelector eSelector;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private boolean allowReuse = false;

    @Override
    public void _run() {
        try {

            if (!DatabaseUnit.UNIT.isStarted() || (DatabaseUnit.UNIT.isStarted() && allowReuse)) {
                DatabaseUnit.UNIT.start();
            }
            
            fireExperimentStarted();

            List<Test> testsToRun = new LinkedList<Test>();
            if (eSelector == null) { //there is no selector so execute everything!
                testsToRun.addAll(tests);
            } else {
                for (Test r : tests) {
                    if (r.getName().equals(eSelector.getSelectedTest())) {
                        testsToRun.add(r);
                        break;
                    }
                }
            }

            for (Test currentTest : testsToRun) {
                if (Thread.currentThread().isInterrupted()) {
                    result = new ExperimentResult(true);
                }

                currentTest.addListener(this);

                currentTest.select(eSelector); //if is null will have no effect
                currentTest.run();

                DatabaseUnit.UNIT.signal(currentTest); // SIGNALING - TELLING THAT STATISTICS COLLECTION TO THE CURRENT TEST IS OVER
                currentTest.removeListener(this);

                if (currentTest != null && currentTest.getResult() != null) {
                    TestResult res = currentTest.getResult();
                    switch (res.getState()) {
                        case CRUSH:
                        case WRONG_RESULT:
                            result = new ExperimentResult(currentTest, res);
                            //for debug porpuse
                            setExecutionSelector(((AbstractTest) currentTest).getFaildExecutionSelector());
                            return;
                    }
                }
            }

            result = new ExperimentResult(Thread.currentThread().isInterrupted());
        } catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                System.out.println("Experiment Interrupted... closing...");
            } else {
                ex.printStackTrace();
            }

        } finally {
            fireExperimentEnded();
            if (!allowReuse) {
                doneReuse();
            }
        }
    }

    @Override
    public void removeInspectorsFromAllTests() {
        for (Test t : tests) {
            t.removeInspectors();
        }
    }
    
    /**
     * allow this experiment to be reused - for example with different execution
     * selectors after you finish reusing the experiment call the method
     * doneReuse to free all the experiment resources
     *
     * @param allowReuse
     */
    public void setAllowReuse(boolean allowReuse) {
        this.allowReuse = allowReuse;
    }

    /**
     * if the experiment is being reused, there are resources that cannot be
     * freed calling this function will tell the execution that it is not going
     * to be reused anymore thus allows it to free all its resources.
     */
    public void doneReuse() {
        pool.shutdownNow();
    }

    @Configuration(name = "Test", description = "Add new test for the experiment")
    @Override
    public void addTest(Test test) {
        tests.add(test);
    }

    @Override
    public List<Test> getTests() {
        if (eSelector == null) {
            return Collections.unmodifiableList(tests);
        } else {
            List<Test> ret = new LinkedList<Test>();
            for (Test r : tests) {
                if (r.getName().equals(eSelector.getSelectedTest())) {
                    ret.add(r);
                    return ret;
                }
            }

            return ret;
        }
    }

    @Override
    public ExperimentResult getResult() {
        return result;
    }

    @Configuration(name = "Execution Selector", description = "Required if the test is to be run in debug mode")
    public void setExecutionSelector(ExecutionSelector di) {
        this.eSelector = di;
    }

    public ExecutionSelector getExecutionSelector() {
        return eSelector;
    }

    public ExecutionSelector getDebugInfo() {
        return this.eSelector;
    }

    @Override
    public void addListener(ExperimentListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(ExperimentListener l) {
        listeners.remove(l);
    }

    @Override
    public void onTestStarted(Test source) {
        for (ExperimentListener l : listeners) {
            l.onNewTestStarted(this, source);
        }
    }

    @Override
    public void onExecutionStarted(Test source, Execution exec) {
        for (ExperimentListener l : listeners) {
            l.onNewExecutionStarted(this, source, exec);
        }

    }

    private void fireExperimentStarted() {
        for (ExperimentListener l : listeners) {
            l.onExpirementStarted(this);
        }
    }

    private void fireExperimentEnded() {
        for (ExperimentListener l : listeners) {
            l.onExpirementEnded(this);
        }
    }

    @Override
    public void onExecutionEnded(Test source, Execution exec) {
        for (ExperimentListener l : listeners) {
            l.onExecutionEnded(this, source, exec);
        }
    }

    @Override
    public int getTotalNumberOfExecutions() {
        if (eSelector != null) {
            return 1;
        } else {
            int sum = 0;
            for (Test r : tests) {
                sum += r.getTotalNumberOfExecutions();
            }
            return sum;
        }

    }

    @Override
    public ExecutorService getThreadPool() {
        return pool;
    }

    @Override
    public void afterExternalConfiguration() {
        for (Test t : tests) {
            if (t instanceof AbstractTest) {
                ((AbstractTest) t).setExperiment(this);

            }
        }
    }
}
