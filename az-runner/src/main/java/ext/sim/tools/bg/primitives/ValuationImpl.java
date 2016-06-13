package ext.sim.tools.bg.primitives;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class ValuationImpl extends Valuation {
	
	@Override
	public Valuation updateValue(Variable var, boolean value) {
		ValuationImpl valuation = new ValuationImpl();
		
		valuation.valuations.putAll(valuations);
		
		valuation.valuations.put(var, value);
		
		return valuation;
	}

	@Override
	public boolean getValue(Variable var) {
		return valuations.get(var);
	}
	
	@Override
	public boolean hasAssignment(Variable var) {
		return valuations.keySet().contains(var);
	}

	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		LinkedList<Variable> list = new LinkedList<>(valuations.keySet());
		
		Collections.<Variable>sort(list, new Comparator<Variable>() {
			@Override
			public int compare(Variable o1, Variable o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		
		for (Variable var : list) {
			sb.append(", " + var.getName() + "=" + (valuations.get(var) ? "T" : "F"));
		}
		
		return sb.length() == 0 ? "" : sb.substring(2).toString();
	}

}
