package ext.sim.modules.adcop.bg.pgenn.traffic;

import java.util.Random;

import ext.sim.modules.adcop.bg.convertor.CostDrivenSingleVariableBooleanGameToADCOP.SingleVariableBooleanGameGenerator;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.agent.AgentImpl;
import ext.sim.tools.bg.formulas.AndFormula;
import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.formulas.OrFormula;
import ext.sim.tools.bg.formulas.VariableFormula;
import ext.sim.tools.bg.game.BooleanGame;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

@Register(name = "rtlc-adcop-pg")
public class RandomCoordinationProblemGenerator extends SingleVariableBooleanGameGenerator {

	@Variable(name = "n", description = "grid dimension is [n * n]", defaultValue = "3")
    private int n = 3;
	@Variable(name = "max-cost", description = "maximal amount of vehicles", defaultValue = "10")
	private int maxCost = 10;

	private VariableFormula[][] vars;

	@Override
	protected BooleanGame generateBooleanGame(Random rand) {
		vars = new VariableFormula[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				vars[i][j] = new VariableFormula(String.format("V[%d,%d]", i, j));
			}
		}
		
		BooleanGame game = new BooleanGame();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				BooleanFormula bf1 = vars[i][j];

				for (int i1 = 0; i1 < n; i1++) {
					for (int j1 = 0; j1 < n; j1++) {
						if (rand.nextBoolean()) {
							if (rand.nextBoolean()) {
								bf1 = new AndFormula(bf1, vars[i1][j1]);
							} else {
								bf1 = new OrFormula(bf1, vars[i1][j1]);
							}
						}
					}
				}

				Agent agent = new AgentImpl(String.format("A[%d,%d]", i, j), bf1);

				agent.setControlledVariable(vars[i][j], rand.nextInt(maxCost + 1),rand.nextInt(maxCost + 1));
				
				game.getAgents().add(agent);
			}					
		}
		
		return game;
	}

}
