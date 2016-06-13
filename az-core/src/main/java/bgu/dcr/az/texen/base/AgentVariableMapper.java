package bgu.dcr.az.texen.base;

public interface AgentVariableMapper {

    int getVar(int agentId);

    int getAgentId(int var);
    
    int getNumberOfAgents();
    
    int getNumberOfVariables();
}
