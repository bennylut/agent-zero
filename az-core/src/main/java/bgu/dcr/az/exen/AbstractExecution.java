package bgu.dcr.az.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.exen.AgentRunner;
import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.exen.Mailer;
import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.api.tools.IdleDetector;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.exen.async.AsyncExecution;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the object that represents the run time environment for a single
 * algorithm execution, can be reused with the use of the reset function There
 * can be several of those for example: one especially designed for testing, one
 * for a distributed environment and one for using in UI’s etc. this way the
 * same algorithm can run without changes in every environment that we choose –
 * with this objects you control the running of the experiment / project /
 * algorithm, get notifications about interesting thing that happened there, let
 * you shut down an execution etc.
 *
 * @author bennyl
 */
public abstract class AbstractExecution extends AbstractProcess implements Execution {

    private Experiment experiment; //the executing experiment
    private Problem problem;//the *global* problem
    private Mailer mailer; //the mailer object used by this execution
    private boolean shuttingdown; //this variable is used to check that the execution is doing the process of shuting down only once.
    private ExecutionResult result = new ExecutionResult(this); //the final execution result
    private AlgorithmMetadata algorithmMetadata; //the executed algorithm metadata
    private IdleDetector idleDetector; //if this execution need an idle detector then this field will hold it
    private ExecutorService executorService; // this is the thread pool that this execution use
    private AgentRunner[] agentRunners; //the agent runners of this execution
    private Agent[] agents; //the constracted agents
    private LinkedList<LogListener> logListeners = new LinkedList<LogListener>(); //list of listeners that receive events about log usage
    private SystemClock clock; //if this execution uses a system clock - this field will hold it
    private List<StatisticCollector> statisticCollectors = new LinkedList<StatisticCollector>(); //list of activated statistic collectors
    private final Test test; //the test that this execution is running in
    private Multimap<String, ReportHook> reportHooks = HashMultimap.create();//list of report hook listeners
    private List<TerminationHook> terminationHooks = new LinkedList<TerminationHook>();
    private Limiter limiter = null;
    private boolean initialized = false;
    private boolean forceIdleDetection = false;

    /**
     *
     */
    public AbstractExecution(Problem p, Mailer m, AlgorithmMetadata a, Test test, Experiment exp) {
        this.shuttingdown = false;
        this.executorService = exp.getThreadPool();
        this.mailer = m;
        this.problem = p;
        this.algorithmMetadata = a;
        this.test = test;
        this.experiment = exp;
    }

    public void setForceIdleDetection(boolean forceIdleDetection) {
        this.forceIdleDetection = forceIdleDetection;
    }

    public boolean isForceIdleDetection() {
        return forceIdleDetection;
    }

    @Override
    public void setLimiter(Limiter timer) {
        this.limiter = timer;
    }

    @Override
    public void terminateDueToLimiter() {
        if (!shuttingdown) {
            result.toEndedByLimiterState();
            shuttingdown = true; //TODO - check if can combine those state markers - there are 3 different markers for termination.

            getMailer().releaseAllBlockingAgents();
        }
    }

    @Override
    public void hookIn(TerminationHook hook) {
        terminationHooks.add(hook);
    }

    @Override
    public void hookIn(ReportHook hook) {
        reportHooks.put(hook.getReportName(), hook);
    }

    @Override
    public void report(String to, Agent a, Object[] args) {
        if (reportHooks.containsKey(to)) {
            for (ReportHook t : reportHooks.get(to)) {
                t.hook(a, args);
            }
        }
    }

    /**
     * will stop the current execution and set the result to no solution TODO:
     * maybe keep track of the execution status via enum (working, done,
     * crushed, etc.)
     *
     * @param ex
     * @param error
     */
    @Override
    public void terminateDueToCrush(Exception ex, String error) {
        if (!shuttingdown) {
            result.toCrushState(ex);
            shuttingdown = true;
            stop();
        }
    }

    @Override
    public Test getTest() {
        return test;
    }

    @Override
    public SystemClock getSystemClock() {
        return this.clock;
    }

    @Override
    public int getNumberOfAgentRunners() {
        return agentRunners.length;
    }

    public void setSystemClock(SystemClock clock) {
        this.clock = clock;
    }

    public void addLogListener(LogListener ll) {
        this.logListeners.add(ll);
    }

    public void removeLogListener(LogListener ll) {
        this.logListeners.remove(ll);
    }

    @Override
    public AgentRunner getAgentRunnerFor(Agent a) {
        return agentRunners[a.getId()];
    }

    protected boolean tryGenerateAgents() {
        try {
            agents = new Agent[getGlobalProblem().getNumberOfVariables()];
            for (int i = 0; i < agents.length; i++) {
                getAgents()[i] = getAlgorithm().generateAgent();
                PlatformOps apops = Agent.PlatformOperationsExtractor.extract(getAgents()[i]);
                apops.setExecution(this);
                apops.setId(i);

            }
            return true;
        } catch (InstantiationException ex) {
            Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, "every agent must have empty constractor", ex);
            terminateDueToCrush(ex, "execution failed on initial state - every agent must have empty constractor");
            return false;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, "agent cannot be abstract/ cannot have a private constractor", ex);
            terminateDueToCrush(ex, "execution failed on initial state - agent cannot be abstract/ cannot have a private constractor");
            return false;
        }
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

    protected void setAgentRunners(AgentRunner[] runners) {
        this.agentRunners = runners;
    }

    @Override
    public Agent[] getAgents() {
        return agents;
    }

    protected AgentRunner[] getRunners() {
        return agentRunners;
    }

    @Override
    public void reportFinalAssignment(Assignment answer) {
        result.toSucceefulState(answer);
    }

    public IdleDetector getIdleDetector() {
        return idleDetector;
    }

    @Override
    public void setStatisticCollectors(List<StatisticCollector> statisticCollectors) {
        this.statisticCollectors = statisticCollectors;
    }

    /**
     * @return the global problem - each agent have its own "version" of problem
     * that is based on the global problem using the global problem is reserved
     * to the execution environment or to the execution tools - do not use it
     * inside your algorithms - use Agents getProblem() instead.
     */
    @Override
    public Problem getGlobalProblem() {
        return this.problem;
    }

    /**
     * @return the mailer attached to this execution.
     */
    @Override
    public Mailer getMailer() {
        return this.mailer;
    }

    /**
     * cause the executed environment to log the given data this implementation
     * only print the data into the screen
     *
     * @param agent
     * @param data
     */
    @Override
    public void log(int agent, String mailGroupKey, String data) {
        for (LogListener ll : logListeners) {
            ll.onLog(agent, mailGroupKey, data);
        }
    }

    @Override
    public ExecutionResult getResult() {
        return result;
    }

    /**
     * @return the metadata for the current executed algorithm
     */
    public AlgorithmMetadata getAlgorithm() {
        return algorithmMetadata;
    }

    /**
     * ugly synchronization - replace with semaphore..
     *
     * @param var
     * @param val
     */
    @Override
    public synchronized void submitPartialAssignment(int var, int val) {
        /*
         * if (partialResult.getAssignment() == null) { partialResult = new
         * ExecutionResult(new Assignment()); }
         * partialResult.getAssignment().assign(var, val);
         */
        if (result.getAssignment() == null) {
            result.toSucceefulState(new Assignment());
        }

        result.getAssignment().assign(var, val);
    }

    @Override
    protected void _run() {
        try {
            initialize();
            startExecution();
        } finally {
            finish();
            try {
                for (TerminationHook hook : terminationHooks) {
                    hook.hook();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            log(-1, "SYSTEM", "Execution Ended With Result = " + getResult());
            System.out.println("Execution Ended.");
        }
    }

    @Override
    public Limiter getLimiter() {
        return limiter;
    }

    protected void startExecution() {
        System.out.println("Starting new execution");
        while (true) {
            for (int i = 0; i < agentRunners.length; i++) {
//                System.out.println("Executing Agent: " + getAgents()[i].getId());
                getExecutorService().execute(getRunners()[i]);
            }
            break;
        }

        for (AgentRunner runner : getRunners()) {
            try {
                runner.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); //reflag
                //Logger.getLogger(AsyncExecution.class.getName()).log(Level.SEVERE, null, ex);
                terminateDueToCrush(ex, "interupted while waiting for all agents to finish");
            }
        }
    }

    protected Experiment getExperiment() {
        return experiment;
    }

    protected abstract void configure();

    protected abstract void finish();

    //TODO - CHECK WHY THIS METHOD IS NEEDED.
    public void setAgentRunnerFor(int id, AgentRunner aThis) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isInterrupted() {
        return result != null && result.getState() == ExecutionResult.State.LIMITED;
    }

    public void setIdleDetector(IdleDetector idleDetector) {
        this.idleDetector = idleDetector;
    }

    /**
     * this is called automatically when the execution is run but you can call
     * it yourself before the actual run to get parameters from the execution
     * that is only available after the initialization
     */
    public void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        //setup mailer
        mailer.setExecution(this);

        //setup idle detector
        if (this.idleDetector == null) {
            this.idleDetector = new IdleDetector(getGlobalProblem().getNumberOfVariables(), getMailer(), getAlgorithm().getAgentClass().getName());
        }

        // do any other configuration that maight be implemented on the deriving classes
        //TODO: check if needed and if so check if can be splited into smaller functions with miningful names
        configure();

        //setup statistic collectors
        for (StatisticCollector sc : statisticCollectors) {
            sc.hookIn(agents, this);
        }

        //setup limiter
        if (limiter != null) {
            limiter.start(this);
            for (AgentRunner ar : agentRunners) {
                ar.setLimiter(limiter);
            }
        }
    }

    public ExecutorService getThreadPool() {
        return executorService;
    }
}
