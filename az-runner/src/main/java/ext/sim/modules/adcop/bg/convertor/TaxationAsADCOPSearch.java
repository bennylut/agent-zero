package ext.sim.modules.adcop.bg.convertor;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.primitives.Valuation;

public class TaxationAsADCOPSearch extends CostDrivenSingleVariableBooleanGameToADCOP {
	
	public TaxationAsADCOPSearch(SingleVariableBooleanGameGenerator gameGenerator) {
		super(gameGenerator);
	}

	@Override
	protected int getMinimalCostIncentives(Problem p, Valuation valuation, Agent agent) {
		return super.getMinimalCostIncentives(p, valuation, agent);
	}

	public final int getNeededTax(Problem p, Assignment assignment, int aId) {
		Valuation valuation = getBGUtils(p).assignmentToValuation(assignment);

		Agent agent = getBGUtils(p).indexToAgent(aId);		

		return getNeededTax(p, valuation, agent);
	}

	public final int getNeededTax(Problem p, Valuation valuation, Agent agent) {
		return getMinimalCostIncentives(p, valuation, agent);
	}
}
