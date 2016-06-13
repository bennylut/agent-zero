/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev;

import bgu.dcr.az.exen.escan.ExperimentReader;
import bc.dsl.SwingDSL;
import bgu.dcr.az.api.exp.ConnectionFaildException;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Experiment.ExperimentListener;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.dev.ui.MainWindow;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import bgu.dcr.az.exen.AbstractExecution;
import bgu.dcr.az.exen.AbstractTest;
import bgu.dcr.az.exen.LogListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public enum ExperimentExecutionController implements Experiment.ExperimentListener {

    UNIT;
    List<ExperimentListener> experimentListeners = new LinkedList<ExperimentListener>();
    LinkedBlockingQueue<Runnable> jobs = new LinkedBlockingQueue<Runnable>();
    WorkerThread worker = new WorkerThread();
    Experiment runningExperiment;
    Test currentTest;
    LogListener logListener = null;
    boolean running;
    File badProblemStorage = new File("fail-problems");
    boolean inDebugMode = false;

    public void run(File xml, boolean withGui, boolean debug) throws InterruptedException {
        try {
            this.inDebugMode = debug;
            worker.start();
            runningExperiment = ExperimentReader.read(xml);
            SwingDSL.configureUI();
            MainWindow mainW = new MainWindow();

            
            if (debug) {
                runningExperiment = mainW.startDebugging(runningExperiment, badProblemStorage);
            }

            runningExperiment.addListener(this);
            
            DatabaseUnit.UNIT.start();
            mainW.startRunning(runningExperiment);
            running = true;
            runningExperiment.run();
            running = false;
            stop();
        } catch (ConnectionFaildException ex) {
            Logger.getLogger(ExperimentExecutionController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExperimentExecutionController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ExperimentExecutionController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ExperimentExecutionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFailProblemStorage(File path) {
        System.out.println("Failed Problem Dir Set To: " + (path == null ? "none!!" : path.getAbsolutePath()));
        badProblemStorage = path;
        badProblemStorage.mkdirs();
    }

    public void stop() {
        if (!running) {
            return;
        }
        System.out.println("Execution unit stop was invoked!");
        
        running = false;
        System.out.println("attempting to close experiment");
        runningExperiment.stop();

//        runningExperiment.removeListener(this);
        //System.out.println("attempting to close statistic collector thread");
        //DatabaseUnit.UNIT.stopCollectorThread();
        //System.out.println("attempting to diconnect from database");
        //DatabaseUnit.UNIT.disconnect();
    }

    public Experiment getRunningExperiment() {
        return runningExperiment;
    }

    public void addExperimentListener(ExperimentListener l) {
        experimentListeners.add(l);
    }

    public void removeExperimentListener(ExperimentListener l) {
        experimentListeners.remove(l);
    }

    public static void main(String[] args) throws InterruptedException {
        UNIT.run(new File("exp.xml"), true, false);
    }

    public AlgorithmMetadata getRunningAlgorithm() {
        if (currentTest == null) {
            return null;
        }
        return ((AbstractTest) currentTest).getAlgorithms().get(0);
    }

    @Override
    public void onExpirementStarted(final Experiment source) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExpirementStarted(source);
                }
            }
        });

    }

    @Override
    public void onExpirementEnded(final Experiment source) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExpirementEnded(source);
                }
            }
        });

        if (source.getResult()!=null && !source.getResult().succeded && !inDebugMode) {
            PrintWriter pw;
            try {
                String fname = newFileName();
                badProblemStorage.mkdirs();
                pw = new PrintWriter(new File(badProblemStorage.getAbsolutePath() + "/" + fname));
                ExperimentReader.write(source, pw);
                pw.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExperimentExecutionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    public Test getCurrentTest() {
        return currentTest;
    }

    private String newFileName() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy'-'DD'-'HH'-'mm'-'ss'.xml'");
        return df.format(new Date());
    }

    @Override
    public void onNewTestStarted(final Experiment source, final Test test) {
        currentTest = test;
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onNewTestStarted(source, test);
                }
            }
        });
    }

    @Override
    public void onNewExecutionStarted(final Experiment source, final Test test, final Execution exec) {
        if (logListener != null) {
            ((AbstractExecution) exec).addLogListener(logListener);
        }

        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onNewExecutionStarted(source, test, exec);
                }
            }
        });
    }

    @Override
    public void onExecutionEnded(final Experiment source, final Test test, final Execution exec) {
        jobs.add(new Runnable() {

            @Override
            public void run() {
                for (ExperimentListener l : experimentListeners) {
                    l.onExecutionEnded(source, test, exec);
                }
            }
        });

    }

    public List<Test> getAllTests() {
        return runningExperiment.getTests();
    }

    public void setLogListener(LogListener l) {
        logListener = l;
    }

    public class WorkerThread extends Thread {

        public WorkerThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable job = jobs.take();
                    try {
                        job.run();
                    } catch (Exception ex) {
                        if (ex instanceof InterruptedException) {
                            this.interrupt();
                        } else {
                            ex.printStackTrace();
                        }
                    }
                } catch (InterruptedException ex) {
                    this.interrupt();
                }
            }
        }
    }
}
