package ext.sim.tools.bg.formulas;

import java.util.HashSet;
import java.util.Set;

import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class ConstantFormula extends BooleanFormula {
	private boolean value;
	
	public ConstantFormula(boolean value) {
		this.value = value;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return value;
	}

	@Override
	public Set<Variable> getVariables() {
		Set<Variable> vars1 = new HashSet<Variable>();
		
		return vars1;
	}

	@Override
	public String toString() {
		return value + "";
	}
}
