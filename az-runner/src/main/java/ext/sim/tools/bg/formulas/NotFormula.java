package ext.sim.tools.bg.formulas;

import java.util.Set;

import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class NotFormula extends BooleanFormula {
	private BooleanFormula formula;

	public NotFormula(BooleanFormula formula) {
		this.formula = formula;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return !formula.evaluate(valuation);
	}
	
	@Override
	public Set<Variable> getVariables() {
		return formula.getVariables();
	}

	@Override
	public String toString() {
		return "~" + formula.toString();
	}
}
