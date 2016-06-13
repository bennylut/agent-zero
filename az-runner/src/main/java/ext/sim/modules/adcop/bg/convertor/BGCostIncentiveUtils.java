package ext.sim.modules.adcop.bg.convertor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.game.BooleanGame;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.ValuationImpl;
import ext.sim.tools.bg.primitives.Variable;

public class BGCostIncentiveUtils {

	private final Problem problem;	
	private final CostDrivenSingleVariableBooleanGameToADCOP costCalculator;
	private final BooleanGame game;
	private Map<Agent, Integer> agentsToIndex;
	private Map<Integer, Agent> indexToAgent;
	private Map<Agent, Set<Agent>> neighbourAgents;
	private Map<Variable, Agent> variablesToAgents;
	private Map<Agent, Variable> agentsToVariables;
	private int minMaxCostValue;

	public BGCostIncentiveUtils(Problem p, CostDrivenSingleVariableBooleanGameToADCOP costCalculator, BooleanGame game) {		
		this.problem = p;
		this.costCalculator = costCalculator;
		this.game = game;

		createMetadata();
	}

	public BooleanGame getBooleanGame() {
		return game;
	}

	public Integer agentToIndex(Agent a) {
		return agentsToIndex.get(a);
	}

	public Agent indexToAgent(int aId) {
		return indexToAgent.get(aId);
	}
	
	public Variable agentToVariable(Agent a) {
		return agentsToVariables.get(a);
	}

	public Set<Agent> neighbourAgents(Agent a) {
		return neighbourAgents.get(a);
	}

	public int getMinMaxCostValue() {
		return minMaxCostValue;
	}

	private void createMetadata() {
		agentsToIndex = new HashMap<>();
		indexToAgent = new HashMap<>();
		variablesToAgents = new IdentityHashMap<>();
		agentsToVariables = new IdentityHashMap<>();

		minMaxCostValue = 1;
		int i = 0;
		for (Agent a : game.getAgents()) {
			indexToAgent.put(i, a);
			agentsToIndex.put(a, i);
			i++;

			if (a.getVariables().size() != 1) {
				throw new RuntimeException("Only one variable per agent supported.");
			} else {
				Variable v = a.getVariables().iterator().next();
				variablesToAgents.put(v, a);
				agentsToVariables.put(a, v);
				minMaxCostValue += Math.abs(a.getCost(v, true) - a.getCost(v, false)) + 1;
			}
		}

		neighbourAgents = new IdentityHashMap<>();

		for (Agent a : game.getAgents()) {
			Set<Agent> neighbours = new HashSet<>();
			for (Variable v : a.getGoal().getVariables()) {
				neighbours.add(variablesToAgents.get(v));
			}
			neighbours.add(a);
			neighbourAgents.put(a, neighbours);
		}
	}

	public final Valuation assignmentToValuation(Assignment cpa) {
		Valuation valuation = new ValuationImpl();

		for (Agent a : game.getAgents()) {
			int var = agentsToIndex.get(a);

			if (cpa.isAssigned(var)) {
				valuation = valuation.updateValue(agentsToVariables.get(a), cpa.getAssignment(var) == 1);
			}
		}

		return valuation;
	}

	public final boolean hasAllAssignments(Valuation valuation, Iterable<Agent> agents) {
		for (Agent a : agents) {
			if (!valuation.hasAssignment(agentsToVariables.get(a))) {
				return false;
			}
		}
		return true;
	}

	public final int getSummOfUtilities(Assignment cpa) {
		Valuation valuation = assignmentToValuation(cpa);
		int sum = 0;
		
		for (Agent a : game.getAgents()) {
			sum += a.getUtility(valuation);
		}

		return sum;
	}
	
	public final int getMinimalCostIncentivesSecured(Assignment cpa, Agent agent) {
		Valuation valuation = assignmentToValuation(cpa);

		if (!hasAllAssignments(valuation, neighbourAgents.get(agent))) {
			return 0;
		}

		Variable var = agentsToVariables.get(agent);

		boolean curVal = valuation.getValue(var);

		boolean cg = agent.getGoal().evaluate(valuation);
		boolean fg = agent.getGoal().evaluate(valuation.updateValue(var, !curVal));

		if (!cg && fg) { // Special Outcome
			return minMaxCostValue;
		}

		if (cg == fg && agent.getCost(var, curVal) > agent.getCost(var, !curVal)) {
			return costCalculator.getMinimalCostIncentives(problem, valuation, agent);
		}

		return 0;
	}

	public int getOverallIncentiveCost(Assignment cpa) {
		int totalCost = 0;
		
		for (Agent a : game.getAgents()) {
			totalCost += getMinimalCostIncentivesSecured(cpa, a);//getMinimalCostIncentivesSecured(cpa, a);
		}

		return totalCost;
	}

	public final int getOverallInitialCost() {
		int totalCost = 0;

		for (Agent a : game.getAgents()) {
			Variable v = agentsToVariables.get(a);
			totalCost += a.getCost(v, true) + a.getCost(v, false);
		}

		return totalCost;
	}

	public final boolean isPNE(Assignment cpa) {
		Valuation val = assignmentToValuation(cpa);

		return hasAllAssignments(val, game.getAgents()) ? game.isNashEquilibrium(val) : false;
	}

	public final boolean hasPNE(Problem p) {
		return game.hasNashEquilibrium();
	}

}
