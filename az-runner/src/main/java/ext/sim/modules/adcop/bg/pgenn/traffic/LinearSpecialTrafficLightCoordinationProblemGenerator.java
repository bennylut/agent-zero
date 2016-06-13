package ext.sim.modules.adcop.bg.pgenn.traffic;

import java.util.Random;

import ext.sim.modules.adcop.bg.convertor.CostDrivenSingleVariableBooleanGameToADCOP.SingleVariableBooleanGameGenerator;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.agent.AgentImpl;
import ext.sim.tools.bg.formulas.AndFormula;
import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.formulas.ConstantFormula;
import ext.sim.tools.bg.formulas.NotFormula;
import ext.sim.tools.bg.formulas.OrFormula;
import ext.sim.tools.bg.formulas.VariableFormula;
import ext.sim.tools.bg.game.BooleanGame;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

@Register(name = "lstlc-adcop-pg")
public class LinearSpecialTrafficLightCoordinationProblemGenerator extends SingleVariableBooleanGameGenerator {

	@Variable(name = "index", description = "costs seed", defaultValue = "3")
    private int index = 3;
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
		
		int t = index;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				BooleanFormula personalGoal = new ConstantFormula(false);

				if (i - 1 >= 0 && i + 1 < n) {
					BooleanFormula f = new AndFormula(vars[i][j], vars[i - 1][j]);
					f = new AndFormula(f, vars[i + 1][j]);
					
					personalGoal = new OrFormula(personalGoal, f);					
				}
				
				if (j - 1 >= 0 && j + 1 < n) {
					BooleanFormula f = new AndFormula(new NotFormula(vars[i][j]), new NotFormula(vars[i][j - 1]));
					f = new AndFormula(f, new NotFormula(vars[i][j + 1]));
					
					personalGoal = new OrFormula(personalGoal, f);
				}

				Agent agent = new AgentImpl(String.format("A[%d,%d]", i, j), personalGoal);
				
				int tmp = t % 3;

				agent.setControlledVariable(vars[i][j], tmp * (tmp % 2), tmp * ((tmp + 1) % 2));
				
				t = t / 3;
								
				game.getAgents().add(agent);
			}					
		}
		
		return game;
	}

}
