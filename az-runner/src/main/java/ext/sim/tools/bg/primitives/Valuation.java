package ext.sim.tools.bg.primitives;

import java.util.HashMap;
import java.util.Map;

public abstract class Valuation {
    protected Map<Variable, Boolean> valuations;
    
    public Valuation() {
    	valuations = new HashMap<Variable, Boolean>();
    }

	public abstract Valuation updateValue(Variable var, boolean value);
	
	public abstract boolean getValue(Variable var);
	
	public abstract boolean hasAssignment(Variable var);
	
	public static Valuation generateValuation(int seed, Iterable<Variable> variables) {
		Valuation valuation = new ValuationImpl();
		
		int j = 0;
		for (Variable var : variables) {
			boolean value = (seed >> j) % 2 == 1;
			valuation.valuations.put(var, value);
			j++;
		}

		return valuation;
	}
	
	public static Valuation merge(Valuation valuation1, Valuation valuation2) {
		Valuation valuation = new ValuationImpl();
		
		valuation.valuations.putAll(valuation1.valuations);
		valuation.valuations.putAll(valuation2.valuations);
		
		return valuation;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Valuation)) {
			return false;
		}
		
		Valuation v = (Valuation)obj;
		
		if (valuations.keySet().size() != v.valuations.keySet().size()) {
			return false;
		}
		
		for (Variable var : valuations.keySet()) {
			if (valuations.get(var) != v.valuations.get(var)) {
				return false;
			}
		}
		
		return true;
	}
}
