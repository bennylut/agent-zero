package ext.sim.modules.adcop.bg.pgenn.traffic;

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

@Register(name = "atlc-adcop-pg")
public class AsymmetricTrafficLightCoordinationProblemGenerator extends SingleVariableBooleanGameGenerator {

	@Variable(name = "n", description = "grid dimension is [n * n]", defaultValue = "3")
    private int n = 3;
	@Variable(name = "max-cost", description = "maximal amount of vehicles", defaultValue = "10")
	private int maxCost = 10;

	private VariableFormula[][] vars;
	private boolean[][] paths;

	@Override
	protected BooleanGame generateBooleanGame(Random rand) {
		vars = new VariableFormula[n][n];
		paths = new boolean[n+1][n+1];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				vars[i][j] = new VariableFormula(String.format("V[%d,%d]", i, j));				
			}
		}

		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= n; j++) {
				paths[i][j] = rand.nextBoolean();
			}
		}
		
		BooleanGame game = new BooleanGame();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				BooleanFormula bf1 = vars[i][j];

				if (i - 1 >= 0 && paths[i - 1][j]) {
					bf1 = new AndFormula(bf1, vars[i - 1][j]);
				}
				if (i + 1 < n && paths[i + 1][j]) {
					bf1 = new AndFormula(bf1, vars[i + 1][j]);
				}

				BooleanFormula bf2 = new NotFormula(vars[i][j]);
				if (j - 1 >= 0 && paths[i][j - 1]) {
					bf2 = new AndFormula(bf2, new NotFormula(vars[i][j - 1]));
				}
				if (j + 1 < n && paths[i][j + 1]) {
					bf2 = new AndFormula(bf2, new NotFormula(vars[i][j + 1]));
				}

				Agent agent = new AgentImpl(String.format("A[%d,%d]", i, j), new OrFormula(bf1, bf2));

				agent.setControlledVariable(vars[i][j], rand.nextInt(maxCost + 1),rand.nextInt(maxCost + 1));
				
				game.getAgents().add(agent);
			}					
		}
		
		return game;
	}

}