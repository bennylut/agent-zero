package ext.sim.modules.adcop.bg.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ext.sim.modules.adcop.bg.convertor.BGCostIncentiveUtils;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.exen.stat.AbstractStatisticCollector;

@Register(name = "poc-sc")
public class PercentageOfNeededCostStatisticCollector extends
		AbstractStatisticCollector<PercentageOfNeededCostStatisticCollector.PercentageOfOverallTaxStatisticCollectorRecord> {

	@Override
	public VisualModel analyze(Database db, Test r) {
		String query = "select AVG(value) as avg, rVar, algorithm_instance from PoTStatistic where TEST = '" + r.getName()
				+ "' group by algorithm_instance, rVar";

		LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(Percentage of Overall Tax)", "Percentage of Overall Tax");

		try {
			ResultSet rs = db.query(query);
			while (rs.next()) {
				line.setPoint(rs.getString("algorithm_instance"), rs.getFloat("rVar"), rs.getFloat("avg"));
			}
			return line;
		} catch (SQLException ex) {
			Logger.getLogger(PercentageOfNeededCostStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
		}

		return line;
	}

	@Override
	public void hookIn(final Agent[] agents, final Execution ex) {
		ex.hookIn(new Hooks.TerminationHook() {
			@Override
			public void hook() {
		    	Problem p = ex.getGlobalProblem();
		    	
		    	if (!(p.getMetadata().containsKey(BGCostIncentiveUtils.class.getCanonicalName()))) {
		    		throw new RuntimeException("Percentage Of Overall Tax can be collected for Boolean games only!!!");
		    	}
		    	
		    	BGCostIncentiveUtils utils = (BGCostIncentiveUtils) p.getMetadata().get(BGCostIncentiveUtils.class.getCanonicalName());
				int totalTax = utils.getOverallIncentiveCost(ex.getResult().getAssignment());
				int totalCost = utils.getOverallInitialCost();

//				if (totalTax != 0) {
//					 panic("Finally not zero tax!!!!");
//				}

				double percentageOfOverallTax = (((double) totalTax) / ((double) totalCost)) * 100.0;
				
				if (totalTax < utils.getMinMaxCostValue()) {
					submit(new PercentageOfOverallTaxStatisticCollectorRecord(ex.getTest().getCurrentVarValue(), percentageOfOverallTax));
				}
			}
		});
	}

	@Override
	public String getName() {
		return "Percentage Of Overall Tax";
	}

	public static class PercentageOfOverallTaxStatisticCollectorRecord extends DBRecord {

		double rVar;
		double value;

		public PercentageOfOverallTaxStatisticCollectorRecord(double rVar, double value) {
			this.rVar = rVar;
			this.value = value;
		}

		@Override
		public String provideTableName() {
			return "PoTStatistic";
		}
	}
}
