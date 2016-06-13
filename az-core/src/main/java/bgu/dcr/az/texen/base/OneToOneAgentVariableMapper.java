package bgu.dcr.az.texen.base;

/**
 *
 * @author User
 */
public class OneToOneAgentVariableMapper implements AgentVariableMapper {

    int numberOfVariables;

    public OneToOneAgentVariableMapper(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }
    
    @Override
    public int getVar(int agentId) {
        return agentId;
    }

    @Override
    public int getAgentId(int var) {
        return var;
    }

    @Override
    public int getNumberOfAgents() {
        return numberOfVariables;
    }

    @Override
    public int getNumberOfVariables() {
        return numberOfVariables;
    }

}
