package ext.sim.tools.bg.game;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public class BooleanGame {
	private List<Agent> agents;
		
	public BooleanGame() {
		agents = new ArrayList<Agent>();
	}
	
	public BooleanGame(BooleanGame game) {
		agents = new ArrayList<Agent>();
		
		for (Agent agent : game.getAgents()) {
			agents.add(agent.clone());
		}
	}
	
	public void sortAgents() {
		Collections.sort(agents, new Comparator<Agent>() {
			@Override
			public int compare(Agent o1, Agent o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}
		
	public List<Agent> getAgents() {
		return agents;
	}
	
	public Set<Variable> getVariables() {
		Set<Variable> variables = new HashSet<Variable>();
		
		for (Agent agent : agents) {
			for (Variable var : agent.getVariables()) {
				if (variables.contains(var)) {
					throw new RuntimeException("Multiple existance of variable: " + var.getName());
				}
				variables.add(var);
			}
		}
		
		return variables;
	}
	
	public boolean hasNashEquilibrium() {
		Set<Variable> variables = getVariables();
		
		int permutations = 1 << variables.size();

		for (int i = 0; i < permutations; i++) {
			Valuation valuation = Valuation.generateValuation(i, variables);
			
			if (isNashEquilibrium(valuation)) {
				System.out.println(valuation);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isNashEquilibrium(Valuation valuation) {
		for (Agent agent : agents) {
			Collection<Variable> variables = agent.getVariables();
			
			int permutations = 1 << variables.size();

			for (int i = 0; i < permutations; i++) {
				Valuation val = Valuation.generateValuation(i, variables);

				int u1 = agent.getUtility(valuation);
				int u2 = agent.getUtility(Valuation.merge(valuation, val)); 
				if (u1 < u2) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean isSpecialOutcome(Valuation v) {
		for (Agent agent : agents) {
			Collection<Variable> variables = agent.getVariables();
			boolean goal = agent.getGoal().evaluate(v);
			
			int permutations = 1 << variables.size();

			for (int i = 0; i < permutations; i++) {
				Valuation val = Valuation.generateValuation(i, variables);

				boolean updated = agent.getGoal().evaluate(Valuation.merge(v, val)); 
				if (!goal && updated) {
					return true;
				}
			}
		}
		
		return false;		
	}

	public boolean isGoalEquivalentOutcome(Valuation v) {
		for (Agent agent : agents) {
			Collection<Variable> variables = agent.getVariables();
			boolean goal = agent.getGoal().evaluate(v);
			
			int permutations = 1 << variables.size();

			for (int i = 0; i < permutations; i++) {
				Valuation val = Valuation.generateValuation(i, variables);

				boolean updated = agent.getGoal().evaluate(Valuation.merge(v, val)); 
				if (goal != updated) {
					return false;
				}
			}
		}
		
		return true;		
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BooleanGame) {
			BooleanGame game = (BooleanGame)obj;
			
			for (Agent agent1 : agents) {
				boolean exists = false;
				for (Agent agent2 : game.getAgents()) {
					if (agent1.equals(agent2)) {
						exists = true;
						break;
					}
				}
				if (!exists) { return false; }
			}
			
			for (Agent agent1 : game.getAgents()) {
				boolean exists = false;
				for (Agent agent2 : agents) {
					if (agent1.equals(agent2)) {
						exists = true;
						break;
					}
				}
				if (!exists) { return false; }
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		for (Agent agent : agents) {
			s.append(agent.toString() + "\n");
		}
		
		return s.toString();
	}
}