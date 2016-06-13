package ext.sim.tools.bg.formulas;

import java.util.Set;

import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public abstract class BooleanFormula {
	public abstract boolean evaluate(Valuation valuation);
	
	public abstract Set<Variable> getVariables();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BooleanFormula) {
			BooleanFormula f = (BooleanFormula)obj;
			
			Set<Variable> vars = getVariables();
			vars.addAll(f.getVariables());
						
			int permutations = 1 << vars.size();
			
			for (int i = 0; i < permutations; i++) {
				Valuation valuation = Valuation.generateValuation(i, vars);
				boolean e1 = evaluate(valuation);
				boolean e2 = f.evaluate(valuation); 
				
				if (e1 != e2) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
