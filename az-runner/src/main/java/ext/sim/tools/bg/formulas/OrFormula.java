package ext.sim.tools.bg.formulas;

import java.util.Set;

import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class OrFormula extends BooleanFormula {
	private BooleanFormula leftOperand;
	private BooleanFormula rightOperand;
	
	public OrFormula(BooleanFormula leftOperand, BooleanFormula rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}
	
	@Override
	public boolean evaluate(Valuation valuation) {
		return leftOperand.evaluate(valuation) || rightOperand.evaluate(valuation);
	}
	
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> vars1 = leftOperand.getVariables();
		Set<Variable> vars2 = rightOperand.getVariables();
		
		vars1.addAll(vars2);
		
		return vars1;
	}
	
	@Override
	public String toString() {
		return "(" + leftOperand.toString() + " V " + rightOperand.toString() + ")";
	}
}
