package ext.sim.agents;

import java.util.LinkedList;
import java.util.List;

import bgu.dcr.az.api.agt.*;
import bgu.dcr.az.api.ano.*;
import bgu.dcr.az.api.tools.Assignment;
import bgu.dcr.az.api.ano.WhenReceived;

@Algorithm(name="K-ary SyncABB", useIdleDetector=false)
public class KarySyncABB extends SimpleAgent {
	private List<Integer> currentDomain;
	private int lb;
	private Assignment bestSolution;

    @Override
    public void start() {
        if (isFirstAgent()) {
            handleFORWARDCPA(new Assignment(), 0, Integer.MAX_VALUE);
        }
    }
    
	private Integer selectConsistentValue(Assignment cpa, int ub) {
		while (!currentDomain.isEmpty()) {
			Integer value = currentDomain.remove(0);
			
			cpa.assign(getId(), value);
			
			int additionalCost = getProblem().calculateCost(cpa);//getConstraintCost(cpa);
			
			if (lb + additionalCost < ub) {
				return additionalCost;
			}
		}
		
		cpa.unassign(getId());
		
		return null;
	}
	
	private void performValueSelection(Assignment cpa, int ub) {
		Integer additionalCost = selectConsistentValue(cpa, ub);
		
		if (additionalCost != null) {
			if (isFirstAgent()) {
				send("FORWARD_CPA", cpa, lb + additionalCost, ub).toNextAgent();
			} else {
				send("BACK_CPA", cpa, getId(), lb + additionalCost, ub).toPreviousAgent();
			}
		} else {
			if (isFirstAgent()) {
				finish(bestSolution);
			} else {
				send("BACKTRACK", cpa, ub).toPreviousAgent();
			}
		}
	}

	@WhenReceived("FORWARD_CPA")
	public void handleFORWARDCPA(Assignment cpa, int lb, int ub) {
		this.lb = lb;
		
		currentDomain = new LinkedList<>(getDomain());

		performValueSelection(cpa, ub);
	}

	@WhenReceived("BACKTRACK")
	public void handleBACKTRACK(Assignment cpa, int ub) {
		performValueSelection(cpa, ub);
	}
	
	private boolean canCheckConstraint(Assignment cpa) {
		for (Integer n : getNeighbors()) {
			if (!cpa.isAssigned(n)) {
				return false;
			}
		}
		
		return true;		
	}

	@WhenReceived("BACK_CPA")
	public void handleBACKCPA(Assignment cpa, int lastAssigned, int lb, int ub) {
		int f = 0;
		
		if (getNeighbors().contains(lastAssigned) && canCheckConstraint(cpa)) {
			f = getProblem().calculateCost(cpa);
		}
		
		if (lb + f < ub) {
			if (isFirstAgent()) {
				if (lastAssigned == getNumberOfVariables() - 1) {
					bestSolution = cpa;
					send("BACKTRACK", cpa, lb + f).toLastAgent();
				} else {
					send("FORWARD_CPA", cpa, lb + f, ub).to(lastAssigned + 1);
				}
			} else {
				send("BACK_CPA", cpa, lastAssigned, lb + f, ub).toPreviousAgent();
			}
		} else {
			send("BACKTRACK", cpa, ub).to(lastAssigned);
		}
	}
	
}
