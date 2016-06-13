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

@Register(name = "tester")
public class PNEExistenceStatisticsCollector extends AbstractStatisticCollector<PNEExistenceStatisticsCollector.PNEExistenceStatisticsCollectorRecord> {

    @Override
    public VisualModel analyze(Database db, Test r) {
    	String query = "select SUM(value) as sum, rVar, algorithm_instance from PNEExistenceStatistics where TEST = '" + r.getName() + "' group by algorithm_instance, rVar";

    	//THEN YOU SHOULD CREATE A GRAPH TO REPRESENT THE QUERY IN LIKE THIS:
    	LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "PNE Existence", "Games that have a PNE for any taxation scheme");
        
    	try {
            ResultSet rs = db.query(query);
            while (rs.next()) {
                line.setPoint(rs.getString("algorithm_instance"), rs.getFloat("rVar"), rs.getInt("sum"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(PNEExistenceStatisticsCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void hookIn(final Agent[] agents, final Execution ex) {

    	ex.hookIn(new Hooks.TerminationHook() {
    	
            @Override
            public void hook() {
            	Problem p = ex.getGlobalProblem();
            	
            	if (!(p.getMetadata().containsKey(BGCostIncentiveUtils.class.getCanonicalName()))) {
            		throw new RuntimeException("Percentage of Games with PNE can be collected for Boolean games only!!!");
            	}
            	
            	BGCostIncentiveUtils utils = (BGCostIncentiveUtils) p.getMetadata().get(BGCostIncentiveUtils.class.getCanonicalName());
            	
            	submit(new PNEExistenceStatisticsCollectorRecord(ex.getTest().getCurrentVarValue(), utils.getOverallIncentiveCost(ex.getResult().getAssignment()) == 0 ? 1 : 0));
            }
        });
    }

    @Override
    public String getName() {
        //YOU CAN CHANGE THE RETURNED VALUE SO THAT YOUR STATISTIC COLLECTOR NAME IN THE UI WILL BE MORE PLESENT
    	return "PNEExistenceStatisticsCollector";
    }
    
	//THIS IS YOUR RECORD IN THE DATABASE DEFINITION
    public static class PNEExistenceStatisticsCollectorRecord extends DBRecord {
    	
        //YOU CAN ADD "PRIMITIVE" FIELDS AND STRING FIELDS ONLY IN THE RECORD FOR EXAMPLE:
        //double cc;
        double rVar;
        int value;

        public PNEExistenceStatisticsCollectorRecord(double rVar, int value) {
            this.rVar = rVar;
            this.value = value;
        }

        @Override
        public String provideTableName() {
            return "PNEExistenceStatistics";
        }
    }
}
