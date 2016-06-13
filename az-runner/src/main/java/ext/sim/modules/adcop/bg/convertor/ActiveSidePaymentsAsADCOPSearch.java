package ext.sim.modules.adcop.bg.convertor;

import bgu.dcr.az.api.prob.Problem;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class ActiveSidePaymentsAsADCOPSearch extends CostDrivenSingleVariableBooleanGameToADCOP {

	public ActiveSidePaymentsAsADCOPSearch(SingleVariableBooleanGameGenerator gameGenerator) {
		super(gameGenerator);
	}

	@Override
	protected int getMinimalCostIncentives(Problem p, Valuation valuation, Agent agent) {
		BGCostIncentiveUtils utils = getBGUtils(p);
		if (utils.hasAllAssignments(valuation, utils.getBooleanGame().getAgents()) && gain(p, valuation) < loss(p, valuation)) {
			return utils.getMinMaxCostValue();
		}

		// if (gain(valuation) >= loss(valuation)) {
		return super.getMinimalCostIncentives(p, valuation, agent);
		// }

		// return getBGUtils().getMinMaxCostValue();
	}

	private int gain(Problem p, Valuation valuation) {
		BGCostIncentiveUtils utils = getBGUtils(p);
		if (!utils.hasAllAssignments(valuation, utils.getBooleanGame().getAgents())) {
			return 0;
		}

		int gain = 0;
		for (Agent a : utils.getBooleanGame().getAgents()) {
			Variable var = utils.agentToVariable(a);
			boolean curVal = valuation.getValue(var);
			if (a.getCost(var, curVal) < a.getCost(var, !curVal)) {
				gain += (a.getCost(var, !curVal) - a.getCost(var, curVal));
			}
		}

		return gain;
	}

	private int loss(Problem p, Valuation valuation) {
		BGCostIncentiveUtils utils = getBGUtils(p);
		if (!utils.hasAllAssignments(valuation, utils.getBooleanGame().getAgents())) {
			return 0;
		}

		int loss = 0;
		for (Agent a : utils.getBooleanGame().getAgents()) {
			Variable var = utils.agentToVariable(a);
			boolean curVal = valuation.getValue(var);
			if (a.getCost(var, curVal) > a.getCost(var, !curVal)) {
				loss += (a.getCost(var, curVal) - a.getCost(var, !curVal));
			}
		}

		return loss;
	}

	// public final int getNeededTax(Assignment assignment, int aId) {
	// Valuation valuation = assignmentToValuation(assignment);
	//
	// Agent agent = null;
	//
	// for (Entry<Agent, Integer> e : agentsToIndex.entrySet()) {
	// if (e.getValue() == aId) {
	// agent = e.getKey();
	// }
	// }
	//
	// return getNeededTax(valuation, agent);
	// }
	//
	// public final int getNeededTax(Valuation valuation, Agent agent) {
	// return getCostIncentives(valuation, agent);
	// }
}
