/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
