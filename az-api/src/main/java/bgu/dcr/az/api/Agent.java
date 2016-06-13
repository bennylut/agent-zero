package bgu.dcr.az.api;

import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.Hooks.AfterMessageProcessingHook;
import bgu.dcr.az.api.agt.ReportMediator;
import bgu.dcr.az.api.agt.SendMediator;
import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.Hooks.BeforeCallingFinishHook;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.WhenReceived;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.exp.RepeatedCallingException;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.escan.VariableMetadata;
import bgu.dcr.az.api.exp.UnsupportedMessageException;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.tools.Assignment;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Agent is the main building block for a CP algorithms, it includes the
 * algorithms main logic. This is The base class of SimpleAgent.
 *
 * The agents have only one entry point and one exit point Entry point: the
 * function start() Exit point: the exit point is not accessed directly instead
 * you can call one of the finish*(*) functions or call panic
 *
 */
public abstract class Agent extends Agt0DSL {

    /**
     * the name for the system termination message the system termination
     * message is getting sent only by the abstract agent
     */
    public static final String SYS_TERMINATION_MESSAGE = "__TERMINATE__";
    /**
     * the name for the system tick message the system tick message is getting
     * sent only by the local search mailer when the system clock performs a
     * 'tick' it wakes up the agent even if he doesn't have any messages - in
     * order for him to re-tick the clock
     */
    public static final String SYS_TICK_MESSAGE = "__TICK__";
    public static final String SYS_TIMEOUT_MESSAGE = "__TIMEOUT__";
    private int id; //The Agent ID
    private MessageQueue mailbox; //This Agent Mailbox
    private ImmutableProblem prob; // The Agent Local Problem
    private boolean finished = false; //The Status of the current Agent - TODO: TRANSFORM INTO A STATUS ENUM SO WE CAN BE ABLE TO QUERY THE AGENT ABOUT IT CURRENT STATUS
    private Message currentMessage = null; //The Current Message (The Last Message That was taken from the mailbox)
    private PlatformOps pops; //Hidden Platform Operation 
    /**
     * collection of hooks that will get called before message processing on
     * this agent
     */
    protected List<Hooks.BeforeMessageProcessingHook> beforeMessageProcessingHooks;
    protected List<Hooks.AfterMessageProcessingHook> afterMessageProcessingHooks;
    /**
     * collection of hooks that will be called before the agent calls finish
     */
    protected List<Hooks.BeforeCallingFinishHook> beforeCallingFinishHooks;
    /**
     * METADATA
     */
    private String algorithmName;
    private boolean usingIdleDetection;
    private HashMap<String, Method> msgToMethod;

    @Override
    public String toString() {
        final String prefix = "Agent " + (getId() < 10 ? "00" : getId() < 100 ? "0" : "") + getId();

        return prefix + "@" + getAlgorithmName();
    }

    /**
     * create a default agent - this agent will have id = -1 so you must
     * reassign it
     */
    public Agent() {
        this.id = -1;
        beforeMessageProcessingHooks = new ArrayList<>();
        afterMessageProcessingHooks = new ArrayList<>();
        beforeCallingFinishHooks = new ArrayList<>();
        this.pops = new PlatformOps();
        final Algorithm algAnnotation = getClass().getAnnotation(Algorithm.class);
        this.usingIdleDetection = false;

        if (algAnnotation != null) {
            this.algorithmName = algAnnotation.name();
            this.usingIdleDetection = algAnnotation.useIdleDetector();
        } else {
            String name = getClass().getSimpleName();
            if (name.endsWith("Agent")) {
                name = name.substring(0, name.length() - "Agent".length());
            }
            this.algorithmName = name;
        }

        msgToMethod = new HashMap<>();
        scanMethods();
    }

    /**
     * will scan methods that should handle messages
     */
    private void scanMethods() {
        for (Method m : getClass().getMethods()) {
            if (m.isAnnotationPresent(WhenReceived.class)) {
                m.setAccessible(true); // bypass the security manager rechecking - make reflected calls faster
                msgToMethod.put(m.getAnnotation(WhenReceived.class).value(), m);
            }
        }
    }

    public boolean isUsingIdleDetection() {
        return usingIdleDetection;
    }

    /**
     * @return the number of constraint checks this agent performed
     */
    public long getNumberOfConstraintChecks() {
        return ((AgentProblem) prob).cc;
    }

    /**
     * creates a message object from the given parameters and attach some
     * metadata to it.. you can override this method to add some more metadata
     * of your own on each message that your agent sends or even modify the
     * message being sent just use super.createMessage(...) to retrieve a new
     * message and then modify it as you please
     *
     * @param name
     * @param args
     * @return
     */
    protected Message createMessage(String name, Object[] args) {
        Message ret = new Message(name, getId(), args);
        beforeMessageSending(ret);
        return ret;
    }

    /**
     * report to statistic collector
     *
     * @param args
     * @return
     */
    protected ReportMediator report(Object... args) {
        return new ReportMediator(args, this);
    }

    /**
     * override this function in case you want to make some action every time
     * before sending a message this is a great place to write logs, attach
     * timestamps to the message etc.
     *
     * @param m
     */
    protected void beforeMessageSending(Message m) {
        //do nothing - derived classes can implement this if they want
    }

    /**
     * the agent is an message driven creature - he works only if he received
     * any message - this is the message that the agent currently processing =
     * the last message taken from the mailbox
     *
     * @return
     */
    protected Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * hook-in to this agent class in the given hook point. hooks are mostly
     * used for "automatic services/tools" like timestamp etc.
     *
     * @param hook
     */
    public void hookIn(Hooks.BeforeMessageProcessingHook hook) {
        beforeMessageProcessingHooks.add(hook);
    }

    /**
     * hook-in to this agent class in the given hook point. hooks are mostly
     * used for "automatic services/tools/statistics" like timestamp etc.
     *
     * @param hook
     */
    public void hookIn(Hooks.AfterMessageProcessingHook hook) {
        afterMessageProcessingHooks.add(hook);
    }

    /**
     * hook to be callback before agent calls finish
     *
     * @param hook
     */
    public void hookIn(Hooks.BeforeCallingFinishHook hook) {
        beforeCallingFinishHooks.add(hook);
    }

    /**
     * @return the problem currently being worked on each agent has its own
     * unique instance of the problem (based on the global problem that was
     * given to the simulator) and he manages it differently - so if you are
     * building tools that have to be sent with the mailer to other agents don't
     * include the agent's problem in them as a field.
     */
    protected ImmutableProblem getProblem() {
        return prob;
    }

    /**
     * @return set of variables that are constrainted with this agent variable
     */
    protected Set<Integer> getNeighbors() {
        return prob.getNeighbors(getId());
    }

    /**
     * @return the id of this agent. In simple algorithms this ID is the
     * variable that the agent is "handling" starting from 0 to number of
     * variables - 1
     */
    public int getId() {
        return id;
    }

    /**
     * request the agent to process the next message (waiting if the agents
     * message queue is empty)
     *
     * @throws InterruptedException
     */
    public final void processNextMessage() throws InterruptedException {
        Message msg = nextMessage(); //will block until there will be messages in the q
        if (msg == null) {
            return;
        }

        for (Hooks.BeforeMessageProcessingHook hook : beforeMessageProcessingHooks) {
            hook.hook(this, msg);
        }

        msg = beforeMessageProcessing(msg);
        if (msg == null) {
            for (AfterMessageProcessingHook hook : afterMessageProcessingHooks) {
                hook.hook(this, msg);
            }
            return; //DUMPING MESSAGE..
        }

        Method mtd = msgToMethod.get(msg.getName());
        if (mtd == null) {
            throw new UnsupportedMessageException("no method to handle message: '" + msg.getName() + "' was found (use @WhenReceived on PUBLIC functions only)");
        }
        try {
            mtd.invoke(this, msg.getArgs());
            for (AfterMessageProcessingHook hook : afterMessageProcessingHooks) {
                hook.hook(this, msg);
            }
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            throw new UnsupportedMessageException("wrong parameters passed with the message " + msg.getName());
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + msg.getName() + "' in agent " + getId(), e);
        } catch (InvocationTargetException e) {
            //e.printStackTrace();
            throw new InternalErrorException("internal error while processing message: '" + msg.getName() + "' in agent " + getId() + ": " + e.getCause().getMessage() + " (see cause)", e.getCause());
        }
    }

    /**
     * @return true if this agent have messages in its mailbox
     */
    public boolean hasPendingMessages() {
        return mailbox.availableMessages() > 0;
    }

    /**
     * @return the next message from the mailbox - waiting if necessary for a
     * new message to arrive
     * @throws InterruptedException
     */
    protected Message nextMessage() throws InterruptedException {
        currentMessage = mailbox.take();
        return currentMessage;
    }

    /**
     * waits for new messages to arrive
     *
     * @throws InterruptedException
     */
    public void waitForNewMessages() throws InterruptedException {
        mailbox.waitForNewMessages();

    }

    /**
     * log something inside this agent log
     *
     * @param what
     */
    protected void log(String what) {
        pops.exec.log(id, pops.mailGroupKey, what);
    }

    protected void logIf(boolean predicate, String what) {
        if (predicate) {
            log(what);
        }
    }

    /**
     * stop execution - returning the given assignment, will cause a TERMINATION
     * message to be sent to all other agents, if ans is null there is no
     * solution
     *
     * @param ans
     */
    protected void finish(Assignment ans) {
        //log("Calling Finish with assignment: " + ans + ", Starting shutdown sequence.");
        pops.exec.reportFinalAssignment(ans);
        terminate();
    }

    /**
     * stop the execution (send TERMINATION to all agents) without solution -
     * this method should be used in csp problem as it make sense there. This is
     * the same as calling finish(null)
     */
    protected void finishWithNoSolution() {
        finish(null);
    }

    /**
     * @deprecated when all the agents call finish, their submitted partial
     * assignments get collected and returned as the result of the execution -
     * unless you called finish(fullAssignment) which will replace all the
     * partial assignments
     */
    @Deprecated
    protected void finishWithAccumulationOfSubmitedPartialAssignments() {
        finish(pops.getExecution().getResult().getAssignment());
    }

    /**
     * stop execution of <b> current </b> agent - will not affect other agents
     * most of the times the desired function to call is
     * finish(current-assignment) or finish(full-assignment) this function is
     * here so that you can implement your own shutdown mechanism
     */
    protected void finish() {
        if (!finished) {
            hookBeforeCallingFinish();
            finished = true;
            mailbox.onAgentFinish();
        }
    }

    /**
     * call this function when an agent is done and want to report its
     * assignment upon finishing
     *
     * @param currentAssignment
     */
    protected void finish(int currentAssignment) {
        submitCurrentAssignment(currentAssignment);
        finish();
    }

    /**
     * the agent can submit its assignment so that when the algorithm is finish
     * running (happened when all agents call finish) this will be the
     * assignment to be accumulated - if you want to re-assign a new value you
     * don't have to call unSubmitCurrentAssignment, you can just call this
     * function again with the new value
     *
     * @param currentAssignment the assignment to submit
     */
    protected void submitCurrentAssignment(int currentAssignment) {
        pops.exec.submitPartialAssignment(getId(), currentAssignment);
    }

    /**
     * remove the submitted current assignment
     */
    protected void unSubmitCurrentAssignment() {
        final Assignment partialAssignment = pops.exec.getResult().getAssignment();
        if (partialAssignment != null) {
            partialAssignment.unassign(this);
        }
    }

    /**
     * @return the last submitted assignment will throw InvalideValueException
     * if no assignment was submitted
     */
    protected Integer getSubmitedCurrentAssignment() {
        final Assignment finalAssignment = pops.exec.getResult().getAssignment();
        if (finalAssignment != null) {
            return finalAssignment.getAssignment(getId());
        }

        throw new InvalidValueException("Agent called 'getSubmitedCurrentAssignment' before he ever called 'submitCurrentAssignment'");
    }

    /**
     * this function called once on each agent when the algorithm is started
     */
    public abstract void start();

    /**
     * @return true if this is the first agent current implementation only
     * checks if this agent's id is 0 but later implementations can use variable
     * arranger that can change the first agent's id
     */
    public boolean isFirstAgent() {
        return this.getId() == 0;
    }

    /**
     * @return true if this is the last agent current implementation just checks
     * if this agent's id +1 is num_of_vars but later implementations can use
     * variable arranger that can change the last agent's id
     */
    protected boolean isLastAgent() {
        return this.getId() + 1 == getNumberOfVariables();
    }

    /**
     * same as calling a.calcCost(getProblem()); accept - if a is null returns
     * infinity (= Integer.MAX_VALUE)
     *
     * @param a
     * @return
     */
    protected int costOf(Assignment a) {
        return (a == null ? Integer.MAX_VALUE : a.calcCost(prob));
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1<-val1 while var2=val2 in the current
     * problem
     */
    public int getConstraintCost(int var1, int val1, int var2, int val2) {
        return getProblem().getConstraintCost(var1, val1, var2, val2);
    }

    /**
     * @param var1
     * @param val1
     * @return the unary cost of assigning var1<-val1 in the current problem
     */
    public int getConstraintCost(int var1, int val1) {
        return getProblem().getConstraintCost(var1, val1);
    }

    /**
     * @return the number of variable in the current problem
     */
    public int getNumberOfVariables() {
        return getProblem().getNumberOfVariables();
    }

    /**
     * @param var
     * @return the domain of some variable in the current problem
     */
    protected ImmutableSet<Integer> getDomainOf(int var) {
        return getProblem().getDomainOf(var);
    }

    /**
     * @return this agents full domain - as immutable set - if you need to
     * change your domain- copy this set and then change your copy :
     * HashSet<Integer> currentDomain = new HashSet<Integer>(getDomain());
     */
    public ImmutableSet<Integer> getDomain() {
        return getProblem().getDomainOf(getId());
    }

    /**
     * same as getDomain().size()
     *
     * @return
     */
    protected int getDomainSize() {
        return getDomainOf(getId()).size();
    }

    /**
     * @param var1
     * @param var2
     * @return true if var1 is constrained with var2 which means : there is val1
     * in domainOf[var1] and val2 in domainOf[var2] where
     * getConstraintCost(val1, var1, var2, val2) != 0
     */
    public boolean isConstrained(int var1, int var2) {
        return getProblem().isConstrained(var1, var2);
    }

    /**
     * @return true if this agent called one of the finish methods (or panic.. )
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * send the given message+arguments to all other agents except the sender
     * (read the method send javadoc for more details about sending the message)
     *
     * @param msg
     * @param args
     */
    protected void broadcast(String msg, Object... args) {
        broadcast(createMessage(msg, args));
    }

    /**
     * broadcast a new message - prefer using broadcast(String msg, Object...
     * args)
     *
     * @param msg
     */
    protected void broadcast(Message msg) {
        pops.getExecution().getMailer().broadcast(msg, pops.mailGroupKey);
    }

    /**
     * sends a new message the message should have a name and any number of
     * arguments the message which will be sent here will be received by an
     * agent in the method that defines
     *
     * @WhenReceived with the name of the message (case sensitive!) and the
     * arguments will be inserted to the parameters of that method
     *
     * usage: send("MESSAGE_NAME", ARG1, ARG2, ..., ARGn).to(OTHER_AGENT_ID)
     *
     * @param msg the message name
     * @param args the list (variadic) of arguments that belongs to this message
     * @return continuation class
     */
    protected SendMediator send(String msg, Object... args) {
        return send(createMessage(msg, args));
    }

    /**
     * send a new message - prefer using send(String msg, Object... args)
     *
     * @param msg
     * @return
     */
    protected SendMediator send(Message msg) {
        final Execution execution = pops.getExecution();
        return new SendMediator(msg, execution.getMailer(), execution.getGlobalProblem(), pops.mailGroupKey);
    }

    /**
     * a callback which is called when idle detected - this is the place to
     * finish the algorithm or revive from idle
     */
    public void onIdleDetected() {
        throw new UnsupportedOperationException("if you are using IdleDetected feature you must implements Agent.onIdleDetected method");
    }

    /**
     * a callback which is called (only when running in synchronized mode) just
     * before the next tick (when the agent finish handling all its messages)
     */
    public void onMailBoxEmpty() {
//        throw new UnsupportedOperationException("if you are running a Synchronized Search you must implements Agent.onMailBoxEmpty method");
    }

    /**
     * this function is called when a SYS_TERMINATION Message Arrived -> it just
     * calls finish on the agent, you can override it to make your own
     * termination handling but dont forgot to reassign it to the termination
     * message. you should override it as follows:
     * <pre>
     * {@code
     *
     * @WhenReceived(Agent.SYS_TERMINATION_MESSAGE) protected void
     * handleTermination() { //your code here... }
     * }
     * </pre>
     *
     */
    @WhenReceived(Agent.SYS_TERMINATION_MESSAGE)
    public void handleTermination() {
        finish();
    }

    @WhenReceived(Agent.SYS_TIMEOUT_MESSAGE)
    protected void handleTimeout() {
        log("Got Timeout indication message");
        finish();
    }

    /**
     * Note: the concept 'system time' only exists in synchronized execution
     *
     * @return the number of ticks passed since the algorithm start (first tick
     * is 0), you can read about the definition of tick in agent zero manual
     */
    public long getSystemTimeInTicks() {
        return pops.getExecution().getSystemClock().time();
    }

    private void hookBeforeCallingFinish() {
        for (BeforeCallingFinishHook l : beforeCallingFinishHooks) {
            l.hook(this);
        }
    }

    /**
     * @return the registered algorithm name for this agent
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * send termination to all agents
     */
    private void terminate() {
        send(SYS_TERMINATION_MESSAGE).toAll(range(0, getNumberOfVariables() - 1));
    }

    /**
     * you can override this method to perform preprocessing before messages
     * arrive to their functions you can change the message or even return
     * completly other one - if you will return null the message is rejected and
     * dumped.
     *
     * @param msg
     * @return
     */
    protected Message beforeMessageProcessing(Message msg) {
        return msg;
    }

    /**
     * this class contains all the "hidden but public" methods, because the user
     * should extend the agent class all the "platform" operations can be called
     * mistakenly by him, instead of making those operations private and then
     * access them via reflection - which will create a decrease in the
     * performance - we just hide them in this inner class. One private object
     * of this class are held by each agent. In order for the platform to obtain
     * this instance it uses another inner class 'PlatformOperationsExtractor'
     * this class contains a static method that extracts the private field -
     * because it's also defined inside the agent it doesn't have to use
     * reflection to do so.
     */
    public class PlatformOps {

        private int numberOfSetIdCalls = 0;
        private Map metadata = new HashMap();
        private String mailGroupKey = Agent.this.getClass().getName(); // The Mail Group Key  - when sending mail it will be recieved only by the relevant group
        private Execution exec; //The Execution That This Agent Is Currently Running Within

        /**
         * @return all the messages names that the algorithm that is represented
         * by this agent can send can send
         */
        public Set<String> algorithmMessages() {
            return msgToMethod.keySet();
        }

        /**
         * used especially in nested agents - where you want to copy all the
         * hooks into the nested agent.
         *
         * @param b the agent to copy this agent hooks into
         */
        public void copyHooksTo(Agent b) {
            b.beforeCallingFinishHooks = beforeCallingFinishHooks;
            b.beforeMessageProcessingHooks = beforeMessageProcessingHooks;
            b.afterMessageProcessingHooks = afterMessageProcessingHooks;
        }

        /**
         * attach an execution to this agent - this execution needs to already
         * contains global problem
         *
         * @param exec
         */
        public void setExecution(Execution exec) {
            this.exec = exec;
            prob = new AgentProblem();
        }

        /**
         * intended for calling from nested agent that actually sharing the same
         * problem with the outer agent
         *
         * @param exec
         * @param prob
         */
        public void setExecution(Execution exec, ImmutableProblem prob) {
            this.exec = exec;
            Agent.this.prob = prob;
        }

        public ImmutableProblem getProblem() {
            return prob;
        }

        public void setMailGroupKey(String mailGroupKey) {
            if (numberOfSetIdCalls != 0) {
                throw new InternalErrorException("cannot call to setMailGroupKey after calling setId - you must first change the mail group key and then the id");
            }

            this.mailGroupKey = mailGroupKey;
        }

        public void clearQueue() {
            while (mailbox.isNotEmpty()) {
                try {
                    mailbox.take();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public Map getMetadata() {
            return metadata;
        }

        /**
         * set the agent id - this method is called by the execution environment
         * to set the agent id and should not be called by hand / by an
         * algorithm implementer this function should only be called once and
         * will throw Repeated Calling Exception upon repeated calls.
         *
         * ** if you need to change the mail group do it before setting the id
         * as setting the id will register the agent to the mailer with it known
         * mail group key
         *
         * @param id
         */
        public void setId(int id) {
            numberOfSetIdCalls++;
            if (numberOfSetIdCalls != 1) {
                throw new RepeatedCallingException("you can only call setId once.");
            }

            Agent.this.id = id;
            mailbox = getExecution().getMailer().register(Agent.this, mailGroupKey);
        }

        /**
         * @return the Execution object - this is the object that connects the
         * agent to the execution environment most of the time the algorithm
         * implementer will not have to deal with this object it is mostly here
         * for advance users / tool implementers
         */
        public Execution getExecution() {
            return exec;
        }

        /**
         * @return the current agent mail group key (current implementation will
         * just return the class name but it should get changed in later
         * implementations)
         */
        public String getMailGroupKey() {
            return mailGroupKey;
        }

        public void configure(Map<String, Object> vars) {
            VariableMetadata.assign(Agent.this, vars);
        }
    }

    /**
     * See documentation of PlatformOps.
     */
    public static class PlatformOperationsExtractor {

        /**
         * extracting the hidden Platform Operations object from the given
         * agent.
         *
         * @param a
         * @return
         */
        public static PlatformOps extract(Agent a) {
            return a.pops;
        }
    }

    /**
     * this is a wrap on the given problem - each agent posess a wrap like this
     * instead of the actual problem
     */
    public class AgentProblem implements ImmutableProblem {

        long cc = 0;
        ConstraintCheckResult queryTemp = new ConstraintCheckResult();

        public int getAgentId() {
            return Agent.this.getId();
        }

        public ConstraintCheckResult getQueryTemp() {
            return queryTemp;
        }

        @Override
        public int getNumberOfVariables() {
            return pops.exec.getGlobalProblem().getNumberOfVariables();
        }

        @Override
        public ImmutableSet<Integer> getDomainOf(int var) {
            return pops.exec.getGlobalProblem().getDomainOf(var);
        }

        @Override
        public int getConstraintCost(int var1, int val1) {
            pops.exec.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, queryTemp);

            cc += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public int getConstraintCost(int var1, int val1, int var2, int val2) {
            pops.exec.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);

            cc += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public String toString() {
            return pops.exec.getGlobalProblem().toString();
        }

        @Override
        public int getDomainSize(int var) {
            return pops.exec.getGlobalProblem().getDomainSize(var);
        }

        @Override
        public HashMap<String, Object> getMetadata() {
            return pops.exec.getGlobalProblem().getMetadata();
        }

        @Override
        public Set<Integer> getNeighbors(int var) {
            return pops.exec.getGlobalProblem().getNeighbors(var);
        }

        @Override
        public boolean isConsistent(int var1, int val1, int var2, int val2) {
            pops.exec.getGlobalProblem().getConstraintCost(getAgentId(), var1, val1, var2, val2, queryTemp);

            cc += queryTemp.getCheckCost();
            return queryTemp.getCost() == 0;
        }

        @Override
        public boolean isConstrained(int var1, int var2) {
            return pops.exec.getGlobalProblem().isConstrained(var1, var2);
        }

        /**
         * @return the type of the problem
         */
        @Override
        public ProblemType type() {
            return pops.exec.getGlobalProblem().type();
        }

        @Override
        public int getConstraintCost(Assignment ass) {
            pops.exec.getGlobalProblem().getConstraintCost(getAgentId(), ass, queryTemp);

            cc += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        @Override
        public int calculateCost(Assignment a) {
            pops.exec.getGlobalProblem().calculateCost(getAgentId(), a, queryTemp);

            cc += queryTemp.getCheckCost();
            return queryTemp.getCost();
        }

        public void increaseCC(int amount) {
            cc += amount;
        }
    }
}
