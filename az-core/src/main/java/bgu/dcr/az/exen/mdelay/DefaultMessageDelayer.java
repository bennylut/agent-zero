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
package bgu.dcr.az.exen.mdelay;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.MessageDelayer;
import bgu.dcr.az.api.exen.stat.NCSCToken;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name = "default-message-delayer")
public class DefaultMessageDelayer implements MessageDelayer {

    @Variable(name = "type",
              description = "the type of the delay: NCCC/NCSC, each depends on the corresponding statistic collectors",
              defaultValue = "NCSC")
    String type = "NCSC";
    @Variable(name = "seed",
              description = "seed for the randomization of the delay",
              defaultValue = "42")
    int seed = 42;
    @Variable(name = "maximum-delay",
              description = "the maximum delay that the delayer can produce for a message",
              defaultValue = "100")
    int maximumDelay = 100;
    @Variable(name = "minimum-delay",
              description = "the minimum delay that the delayer can produce for a message",
              defaultValue = "0")
    int minimumDelay = 0;
    
    long[][] previousTime;
    Random rnd = null;

    @Override
    public long getInitialTime() {
        return 0;
    }

    @Override
    public long extractTime(Message m) {
        if (type.equals("NCCC")) {
            return ((Long) m.getMetadata().get("nccc")).intValue();
        } else {
            return NCSCToken.extract(m).getValue();//((Long) m.getMetadata().get("ncsc")).intValue();
        }
    }

    @Override
    public void addDelay(Message m, int from, int to) {
        if (maximumDelay < minimumDelay) return; //nothinng to add..
        int delay = rnd.nextInt(maximumDelay - minimumDelay) + minimumDelay;
        long otime = extractTime(m);
        long ntime = Math.max(delay + previousTime[from][to], otime + delay);
        previousTime[from][to] = ntime;
        if (type.equals("NCCC")) {
            m.getMetadata().put("nccc", (long)ntime);
        } else {
            NCSCToken.extract(m).setValue((long)ntime);
//            m.getMetadata().put("ncsc", (long)ntime);
        }
    }

    @Override
    public void initialize(Execution ex) {
        System.out.println("initializing message delayer... delaying on " + type );
        int n = ex.getGlobalProblem().getNumberOfVariables();
        previousTime = new long[n][n];
        rnd = new Random(seed);
    }
}
