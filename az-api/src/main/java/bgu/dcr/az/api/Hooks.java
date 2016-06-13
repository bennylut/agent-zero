/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.api.exen.Execution;

/**
 * this collection of interfaces that the simple agent supports hooking via
 * @author bennyl
 */
public class Hooks {

    /**
     * describe hook definition with automatically hooking functinality
     */
    public static interface HookDefinition {

        void hookInto(Execution ex);
    }
    
    /**
     * callback that will get called before message sent - can be attached to simple agent.
     */
    public static abstract class BeforeMessageSentHook implements HookDefinition {

        
        public abstract void hook(int senderId, int recepientId, Message msg);

        @Override
        public void hookInto(Execution ex) {
            ex.getMailer().hookIn(this);
        }
    }

    /**
     * callback that will get called before message processed by the attached agent - can be attachd to simple agent.
     */
    public static abstract class BeforeMessageProcessingHook implements HookDefinition {

        /**
         * callback implementation
         * @param msg
         */
        public abstract void hook(Agent a, Message msg);

        @Override
        public void hookInto(Execution ex) {
            for (Agent a : ex.getAgents()) {
                a.hookIn(this);
            }
        }
    }
   
     public static abstract class AfterMessageProcessingHook implements HookDefinition {

        /**
         * callback implementation
         * @param msg
         */
        public abstract void hook(Agent a, Message msg);

        @Override
        public void hookInto(Execution ex) {
            for (Agent a : ex.getAgents()) {
                a.hookIn(this);
            }
        }
    }
   

    public static abstract class BeforeCallingFinishHook implements HookDefinition {

        public abstract void hook(Agent a);

        @Override
        public void hookInto(Execution ex) {
            for (Agent a : ex.getAgents()) {
                a.hookIn(this);
            }
        }
    }

    public static abstract class ReportHook implements HookDefinition {

        String reportName;

        public ReportHook(String reportName) {
            this.reportName = reportName;
        }

        public String getReportName() {
            return reportName;
        }

        public abstract void hook(Agent a, Object[] args);

        @Override
        public void hookInto(Execution ex) {
            ex.hookIn(this);
        }
    }

    public static abstract class TickHook implements HookDefinition {

        public abstract void hook(SystemClock clock);

        @Override
        public void hookInto(Execution ex) {
            if (ex.getSystemClock() != null) {
                ex.getSystemClock().hookIn(this);
            } else {
                Agt0DSL.panic("cannot hook into the system clock, are you running synchronus execution?");
            }
        }
    }

    public static abstract class TerminationHook implements HookDefinition {

        public abstract void hook();

        @Override
        public void hookInto(Execution ex) {
            ex.hookIn(this);
        }
    }
}
