package ext.sim.tools.bg.agent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class AgentImpl implements Agent {
	private String name;
	private BooleanFormula goal;
    private Map<Variable, Integer> trueCosts;
    private Map<Variable, Integer> falseCosts;
    
    public AgentImpl(String name, BooleanFormula goal) {
    	this.name = name;
    	this.goal = goal;
    	
    	trueCosts = new HashMap<Variable, Integer>();
    	falseCosts = new HashMap<Variable, Integer>();
    }
    
    @Override
    public String getName() {
    	return name;
    }
	
	@Override
	public void setControlledVariable(Variable var, int trueCost, int falseCost) {
		trueCosts.put(var, trueCost);
		falseCosts.put(var, falseCost);
		
		assert Arrays.equals(trueCosts.keySet().toArray(), falseCosts.keySet().toArray());
	}

	@Override
	public Collection<Variable> getVariables() {
		return trueCosts.keySet();
	}

	@Override
	public BooleanFormula getGoal() {
		return goal;
	}
	
	@Override
	public int getCost(Variable variable, boolean value) {
		return value ? trueCosts.get(variable) : falseCosts.get(variable);
	}

	private int getMostExpensiveCost(Valuation valuation) {
		int cost = 0;
		
		for(Variable var : getVariables()) {
			int tC = trueCosts.get(var);
			int fC = falseCosts.get(var);
			
			cost += Math.max(tC, fC);
		}
		
		return cost;
	}
	
	private int getCost(Valuation valuation) {
		int cost = 0;

		for(Variable var : getVariables()) {			
			cost += valuation.getValue(var) ? trueCosts.get(var) : falseCosts.get(var);
		}

		return cost;
	}
	
	@Override
	public int getUtility(Valuation valuation) {
		return goal.evaluate(valuation) ? 
					1 + getMostExpensiveCost(valuation) - getCost(valuation) : 
								-getCost(valuation); 
	}
	
	@Override
	public Agent clone() {
		AgentImpl agent = new AgentImpl(name, goal);
		
		for (Variable var : trueCosts.keySet()) {
			agent.trueCosts.put(var, trueCosts.get(var));
		}
		
		for (Variable var : falseCosts.keySet()) {
			agent.falseCosts.put(var, falseCosts.get(var));
		}
		
		return agent;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Agent) {
			Agent agent = (Agent)obj;
			
			return agent.getName().equals(getName());
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Variable var : getVariables()) {
			s.append(String.format(", C(%s=T)=%d  C(%s=F)=%d", var.getName(), trueCosts.get(var), var.getName(), falseCosts.get(var)));
		}
//		return "Name: " + getName() + "\n" +
//			   "Goal: " + getGoal() + "\n" +
//			   "Costs: " + (s.length() == 0 ? "" : s.substring(2)) + "\n";
		return getName();
	}
}
