package ext.sim.tools.bg.agent;

import java.util.Collection;

import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public interface Agent {
	String getName();
	
	void setControlledVariable(Variable var, int trueCost, int falseCost);
	
	Collection<Variable> getVariables();
	
	BooleanFormula getGoal();
	
	int getCost(Variable variable, boolean value);
	
	int getUtility(Valuation valuation);
	
	Agent clone();
}
