package ext.sim.modules.adcop.bg.ct;

import ext.sim.modules.adcop.bg.convertor.BGCostIncentiveUtils;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.exen.correctness.AbstractCorrectnessTester;

@Register(name="is-pne-ct")
public class BgIsPNECorrectmessTester extends AbstractCorrectnessTester {

    @Override
	public CorrectnessTestResult test(Execution exec, ExecutionResult result) {
    	Problem p = exec.getGlobalProblem();
    	
    	if (!(p.getMetadata().containsKey(BGCostIncentiveUtils.class.getCanonicalName()))) {
    		throw new RuntimeException("Percentage of Games with PNE can be collected for Boolean games only!!!");
    	}
    	
    	BGCostIncentiveUtils utils = (BGCostIncentiveUtils) p.getMetadata().get(BGCostIncentiveUtils.class.getCanonicalName());
        
        return new CorrectnessTestResult(null, utils.isPNE(result.getAssignment())); //<-- PASS
  	}
}
