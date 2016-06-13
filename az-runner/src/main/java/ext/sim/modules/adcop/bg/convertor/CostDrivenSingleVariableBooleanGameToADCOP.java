package ext.sim.modules.adcop.bg.convertor;

import java.util.Random;
import java.util.Set;

import bgu.dcr.az.api.exp.UnsupportedOperationException;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.exen.pgen.AbstractProblemGenerator;
import ext.sim.tools.bg.agent.Agent;
import ext.sim.tools.bg.game.BooleanGame;
import ext.sim.tools.bg.primitives.Valuation;
import ext.sim.tools.bg.primitives.Variable;

public abstract class CostDrivenSingleVariableBooleanGameToADCOP {

	private SingleVariableBooleanGameGenerator gameGenerator;

	public BGCostIncentiveUtils getBGUtils(Problem p) {
		return (BGCostIncentiveUtils) p.getMetadata().get(BGCostIncentiveUtils.class.getCanonicalName());
	}

	public CostDrivenSingleVariableBooleanGameToADCOP(SingleVariableBooleanGameGenerator gameGenerator) {
		this.gameGenerator = gameGenerator;
	}

	public void generate(Problem p, Random rand) {
		// System.out.println("Generating Boolean game...");		
		p.getMetadata().put(BGCostIncentiveUtils.class.getCanonicalName(), new BGCostIncentiveUtils(p, this, gameGenerator.generateBooleanGame(rand)));
		// System.out.println("Boolean game generated.");

		// System.out.println("Generating ADCOP...");
		generateADCOP(p);
		// System.out.println("ADCOP generated.");		
	}

	private void generateADCOP(Problem p) {
		BGCostIncentiveUtils utils = getBGUtils(p);
		p.initialize(ProblemType.K_ARY_ADCOP, utils.getBooleanGame().getAgents().size(), 2);

		for (Agent agent : utils.getBooleanGame().getAgents()) {
			final Agent fAgent = agent;
			int aid = utils.agentToIndex(fAgent);
			p.setConstraint(aid, new KAryConstraint() {
				private int[] participients;

				@Override
				public int[] getParicipients() {
					if (participients == null) {
						Set<Agent> neighbours = utils.neighbourAgents(fAgent);
						participients = new int[neighbours.size()];
						int i = 0;
						for (Agent a : neighbours) {
							participients[i++] = getBGUtils(p).agentToIndex(a);
						}
					}
					return participients;
				}

				@Override
				public void getCost(Assignment cpa, ConstraintCheckResult result) {
					result.set(utils.getMinimalCostIncentivesSecured(cpa, fAgent), 1);
				}

			});
		}
	}

	protected int getMinimalCostIncentives(Problem p, Valuation valuation, Agent agent) {
		Variable var = getBGUtils(p).agentToVariable(agent);
		boolean curVal = valuation.getValue(var);

		return agent.getCost(var, curVal) > agent.getCost(var, !curVal) ? agent.getCost(var, curVal) - agent.getCost(var, !curVal) : 0;
	}

	public static abstract class SingleVariableBooleanGameGenerator extends AbstractProblemGenerator {

		@bgu.dcr.az.api.ano.Variable(name = "im", description = "Incentive mechanizm", defaultValue = "test")
		private String im = "test";

		protected CostDrivenSingleVariableBooleanGameToADCOP bgToAdcop;

		private void updateIncentiveMechanizm() {
			System.out.println("Incentive mechanizm: " + im);
			switch (im) {
			case "taxation":
				bgToAdcop = new TaxationAsADCOPSearch(this);
				break;
			case "side-payments":
				bgToAdcop = new ActiveSidePaymentsAsADCOPSearch(this);
				break;
			default:
				throw new UnsupportedOperationException("Unknown incentive mechanizm: " + im);
			}
		}

		protected abstract BooleanGame generateBooleanGame(Random rand);

		public final CostDrivenSingleVariableBooleanGameToADCOP getADCOPProblemGenerator() {
			return bgToAdcop;
		}

		@Override
		public void generate(Problem p, Random rand) {
			updateIncentiveMechanizm();
			bgToAdcop.generate(p, rand);
		}
	}
}
