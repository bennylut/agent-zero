package ext.sim.modules.adcop.bg.pgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import ext.sim.modules.adcop.bg.convertor.CostDrivenSingleVariableBooleanGameToADCOP.SingleVariableBooleanGameGenerator;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.agent.AgentImpl;
import ext.sim.tools.bg.formulas.AndFormula;
import ext.sim.tools.bg.formulas.BooleanFormula;
import ext.sim.tools.bg.formulas.NotFormula;
import ext.sim.tools.bg.formulas.OrFormula;
import ext.sim.tools.bg.formulas.VariableFormula;
import ext.sim.tools.bg.game.BooleanGame;
import ext.sim.tools.graph.Graph;
import ext.sim.tools.graph.Vertex;
import ext.sim.tools.graph.gen.AverageNeighboursGraphGenerator;

@Register(name = "bgsf-adcop-pg")
public class NoPNEWithoutTaxProblemGenerator extends SingleVariableBooleanGameGenerator {
	@Variable(name = "n", description = "number of agents", defaultValue = "10")
	private int n = 10;
	@Variable(name = "aan", description = "average amount of neighbors", defaultValue = "3")
	private int aan = 3;
	@Variable(name = "max-cost", description = "maximal cost", defaultValue = "100")
	private int maxCost = 100;

	private VariableFormula[] vars;
	private BooleanFormula[] nvars;

	@Override
	protected BooleanGame generateBooleanGame(Random rand) {
//		Set<Integer>[] neighbors = new Set[n];
		Graph g = new AverageNeighboursGraphGenerator(aan).generate(n, rand);
		Map<Integer, Vertex> indexToVertex = new HashMap<>();
		Map<Vertex, Integer> vertexToIndex = new HashMap<>();
		vars = new VariableFormula[n];
		nvars = new BooleanFormula[n];

		int i = 0;
		for (Vertex v : g.getVertices()) {
			vars[i] = new VariableFormula(String.format("V[%d]", i));
			nvars[i] = new NotFormula(vars[i]);
			indexToVertex.put(i, v);
			vertexToIndex.put(v, i);
			i++;
		}

//		List<Integer> specialIDs = new LinkedList<>(agentIds);
//
//		Collections.shuffle(specialIDs, rand);
//
//		for (int i = 0; i < n; i++) {
//			List<Integer> ids = new LinkedList<>(agentIds);
//			Collections.shuffle(ids, rand);
//			ids.remove((Integer) i);
//			ids.removeAll(neighbors[i]);
//			while (neighbors[i].size() < aan && !ids.isEmpty()) {
//				Integer n = ids.remove(0);
//				neighbors[i].add(n);
//				neighbors[n].add(i);
//			}
//		}

		BooleanGame game = new BooleanGame();

		for (i = 0; i < indexToVertex.size(); i++) {
			Vertex u = indexToVertex.get(i);
			BooleanFormula pg = null;
			for (Vertex v : g.getNeighbours(u)) {
				int j = vertexToIndex.get(v);
				BooleanFormula f = createCoordination(i, j);
				pg = pg == null ? f : new OrFormula(pg, f);
			}
			Agent a = new AgentImpl("A_" + i, pg);
			if (i == 0) {
				a.setControlledVariable(vars[i], maxCost * n * 200, 0);
//				a.setControlledVariable(vars[i], maxCost + rand.nextInt(maxCost), rand.nextInt(maxCost));
			} else {
				a.setControlledVariable(vars[i], maxCost + rand.nextInt(maxCost), rand.nextInt(maxCost));
			}
			game.getAgents().add(a);
		}

		return game;
	}

	private BooleanFormula createCoordination(int i, int j) {
		BooleanFormula pgSF = null;

		if (i < j) {
			pgSF = new OrFormula(vars[i], vars[j]);
		} else {
			BooleanFormula c1 = new AndFormula(vars[i], vars[j]);
			BooleanFormula c2 = new AndFormula(nvars[i], nvars[j]);
			
			pgSF = new OrFormula(c1, c2);
		}

		return pgSF;
	}

}
