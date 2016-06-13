package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exp.UnRegisteredAgentException;

/**
 * This is a familiar concept, an Interface for designing mailers – the mailer
 * is attached to the Execution object – what means that you should be able to
 * attach different mailers to test the algorithm with (some mailers can help
 * with producing algorithm visualization and some can be used for debugging).
 *
 * @author bennyl
 */
public interface Mailer extends NonBlockingMailer {

    /**
     * @return true if all the registered mailboxed are empty
     */
    public boolean isAllMailBoxesAreEmpty(String groupKey);

    MessageQueue register(Agent agent, String groupKey);

    /**
     * release all blocking agents from a specific mail group
     *
     * @param mailGroup
     */
    public void releaseAllBlockingAgents(String mailGroup);

    /**
     * release all blocking agents from all mail groups this method only make
     * sense if you are trying to terminate the whole execution as it may harm
     * the integrity of its results
     */
    public void releaseAllBlockingAgents();
}
