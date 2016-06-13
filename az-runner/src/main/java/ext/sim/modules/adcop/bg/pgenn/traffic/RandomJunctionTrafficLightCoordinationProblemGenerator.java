package ext.sim.modules.adcop.bg.pgenn.traffic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ext.sim.modules.adcop.bg.convertor.CostDrivenSingleVariableBooleanGameToADCOP.SingleVariableBooleanGameGenerator;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.agent.AgentImpl;
import ext.sim.tools.bg.formulas.AndFormula;
import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.formulas.NotFormula;
import ext.sim.tools.bg.formulas.OrFormula;
import ext.sim.tools.bg.formulas.VariableFormula;
import ext.sim.tools.bg.game.BooleanGame;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

@Register(name = "rj-tlc-adcop-pg")
public class RandomJunctionTrafficLightCoordinationProblemGenerator extends SingleVariableBooleanGameGenerator {

	@Variable(name = "n", description = "grid dimension is [n * n]", defaultValue = "3")
    private int n = 3;
	@Variable(name = "max-cost", description = "maximal amount of vehicles", defaultValue = "10")
	private int maxCost = 10;
	@Variable(name = "rj", description = "amount of random junctions", defaultValue = "0")
	private int randomJunctions = 0;

	private VariableFormula[][] vars;
	private List<Integer> specials;

	@Override
	protected BooleanGame generateBooleanGame(Random rand) {
		vars = new VariableFormula[n][n];
		specials = new LinkedList<>();
		
		for (int i = 0; i < n * n; i++) {
			specials.add(i);
		}
		
		Collections.shuffle(specials, rand);
		
		while (specials.size() > randomJunctions) {
			specials.remove(0);
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				vars[i][j] = new VariableFormula(String.format("V[%d,%d]", i, j));
			}
		}
		
		BooleanGame game = new BooleanGame();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				Agent agent = generateAgent(rand, i, j);
				
				game.getAgents().add(agent);
			}					
		}
		
		return game;
	}

	private Agent generateAgent(Random rand, int i, int j) {
		//BooleanFormula personalGoal = specials.contains(i * n + j) ? generateRandomizedPersonalGoal(rand, i, j) : generateStandartPersonalGoal(rand, i, j);
		BooleanFormula personalGoal = generateRandomizedPersonalGoal(rand, i, j);
		
		Agent agent = new AgentImpl(String.format("A[%d,%d]", i, j), personalGoal);

		agent.setControlledVariable(vars[i][j], rand.nextInt(maxCost + 1),rand.nextInt(maxCost + 1));
		return agent;
	}

//	private BooleanFormula generateStandartPersonalGoal(Random rand, int i, int j) {
//		BooleanFormula bf1 = vars[i][j];
//
//		if (i - 1 >= 0) {
//			bf1 = new AndFormula(bf1, vars[i - 1][j]);
//		}
//		if (i + 1 < n) {
//			bf1 = new AndFormula(bf1, vars[i + 1][j]);
//		}
//
//		BooleanFormula bf2 = new NotFormula(vars[i][j]);
//		if (j - 1 >= 0) {
//			bf2 = new AndFormula(bf2, new NotFormula(vars[i][j - 1]));
//		}
//		if (j + 1 < n) {
//			bf2 = new AndFormula(bf2, new NotFormula(vars[i][j + 1]));
//		}
//
//		return new OrFormula(bf1, bf2);
//	}

	private BooleanFormula generateRandomizedPersonalGoal(Random rand, int i, int j) {
//		int sn = rand.nextInt(10);
//		int ew = rand.nextInt(10);
//		int dd = rand.nextInt(2);		
//		BooleanFormula bf1 = vars[i][j];
//
//		if (i - 1 >= 0 && (sn < 5)) {
//			bf1 = new AndFormula(bf1, vars[i - 1][j]);
//		}
//		if (i + 1 < n && (sn > 5)) {
//			bf1 = new AndFormula(bf1, vars[i + 1][j]);
//		}
//
//		BooleanFormula bf2 = new NotFormula(vars[i][j]);
//		if (j - 1 >= 0  && (ew < 5)) {
//			bf2 = new AndFormula(bf2, new NotFormula(vars[i][j - 1]));
//		}
//		if (j + 1 < n && (ew > 5)) {
//			bf2 = new AndFormula(bf2, new NotFormula(vars[i][j + 1]));
//		}
//
//		switch (dd) {
//			case 0:
//				return bf1;
//			default:
//				return bf2;
//		}
		
//		return new OrFormula(bf1, bf2);		
		
		BooleanFormula randomGoal = vars[i][j];
		
		List<VariableFormula> neighbors = new LinkedList<>();
		neighbors.add(vars[i][j]);

		if (i - 1 >= 0) {
			neighbors.add(vars[i - 1][j]);
		}
		if (i + 1 < n) {
			neighbors.add(vars[i + 1][j]);
		}

		if (j - 1 >= 0) {
			neighbors.add(vars[i][j - 1]);
		}
		if (j + 1 < n) {
			neighbors.add(vars[i][j + 1]);
		}
		
		for (int t = 0; t < 5; t++) {
			VariableFormula v = neighbors.get(rand.nextInt(neighbors.size()));
			BooleanFormula f = rand.nextBoolean() ? v : new NotFormula(v);
			
			randomGoal = rand.nextBoolean() ? new AndFormula(randomGoal, f) : new OrFormula(randomGoal, f);
		}

		return randomGoal;
	}

}
