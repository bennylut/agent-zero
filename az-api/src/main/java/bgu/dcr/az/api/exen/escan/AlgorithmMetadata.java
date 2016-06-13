package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exp.InvalidValueException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * this is a metadata of an algorithm used to create and configure an agent, 
 * the metadata itself read the Algorithm Annotation from the agent and upon agent 
 * creation it configurs with its given configuration.
 * @author bennyl
 */
@Register(name = "algorithm")
public class AlgorithmMetadata implements ExternalConfigurationAware {

    //CONFIGURABLE FIELDS
    @Variable(name = "name", description = "the name of the algorithm", defaultValue = "unnamed")
     String name; //the algorithm name
    @Variable(name = "instance-name", description = "the name of the algorithm instance", defaultValue = "default")    
     String instanceName = "default";
    
    //NORMAL FIELDS
    Class<? extends Agent> agentClass; //the class of the agent that implements this algorithm
    ProblemType problemType;
    boolean useIdleDetector;
    private Map<String, Object> agentConfiguration = new HashMap<String, Object>();

    /**
     * constract an algorithm metadata from an agent class 
     * the agent class must be annotated by @Algorithm annotation.
     * @param agentClass
     */
    public AlgorithmMetadata(Class<? extends Agent> agentClass) {
        initialize(agentClass);
    }

    public AlgorithmMetadata() {
    }

    private void initialize(Class<? extends Agent> agentClass) throws InvalidValueException {
        bgu.dcr.az.api.ano.Algorithm a = agentClass.getAnnotation(bgu.dcr.az.api.ano.Algorithm.class);
        if (a == null) {
            throw new InvalidValueException("no algorithm annotation used on the given agent class");
        }

        this.name = a.name();
        this.agentClass = agentClass;
        this.problemType = a.problemType();
        this.useIdleDetector = a.useIdleDetector();
    }

    public String getInstanceName() {
        if (instanceName.equals("default")) return getName();
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * @return the algorithm name
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if the algorithm implementer request the platform to use idle detection to close its agents
     */
    public boolean isUseIdleDetector() {
        return useIdleDetector;
    }

    /**
     * @return the class of the agent implementation that can run this algorithm
     */
    public Class<? extends Agent> getAgentClass() {
        return agentClass;
    }

    /**
     * @return the problem type that the algorithm implementer declare its algorithm to solve
     */
    public ProblemType getProblemType() {
        return problemType;
    }

    @Configuration(name = "Agent Variable Assignment", description = "if the agent defined variables you can assign new values to them")
    public void addAgentVariableAssignment(VarAssign va) {
        agentConfiguration.put(va.varName, va.value);
    }

    public List<VarAssign> getAgentVariableAssignments() {
        List<VarAssign> ret = new LinkedList<VarAssign>();
        for (Entry<String, Object> va : agentConfiguration.entrySet()) {
            ret.add(new VarAssign(va.getKey(), va.getValue().toString()));
        }

        return ret;
    }

    public Agent generateAgent() throws InstantiationException, IllegalAccessException {
        Agent agent = agentClass.newInstance();
        Agent.PlatformOps pops = Agent.PlatformOperationsExtractor.extract(agent);
        pops.configure(agentConfiguration);
        return agent;
    }

    /**
     * the algorithm depends on calling this function before starting any execution
     */
    @Override
    public void afterExternalConfiguration() {
        Class<? extends Agent> a = Registery.UNIT.getAgentByAlgorithmName(name);
        if (a == null) {
            throw new InvalidValueException("cannot find agent with the given algorithm name: '" + name + "'");
        }
        initialize(a);

    }
}
