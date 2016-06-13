package ext.sim.tools.bg.formulas;

import java.util.HashSet;
import java.util.Set;

import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class VariableFormula extends BooleanFormula implements Variable {
	private String name;
	
	public VariableFormula(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return valuation.getValue(this);
	}
	
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> vars = new HashSet<Variable>();
		
		vars.add(this);
		
		return vars;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Variable) && ((Variable)obj).getName().equals(name);
	}

	@Override
	public String toString() {
		return getName();
	}
}