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
package bgu.dcr.az.exen;

import bgu.dcr.az.api.exen.Process;

/* TODO: remove event pipe implementation in favor of piped event bus implementation event bus */
/**
 * process is the basic unit that can run 
 * @author bennyl
 */
public abstract class AbstractProcess implements Process {

    private boolean finished;
    private Thread executingThread;

    public AbstractProcess() {
        finished = false;
    }

    @Override
    public final void stop() {
        if (!finished && executingThread != null) {
            finished = true;
            _stop();
        }
    }

    /**
     * this stop will cause the executing thread to be interrupted - you can change it 
     * if it is not your suitable logic
     */
    protected void _stop(){
        executingThread.interrupt();
    }
    
    @Override
    public final void run() {
        finished = false;
        executingThread = Thread.currentThread();
        _run();
        finished = true;
    }

    protected abstract void _run();

    @Override
    public boolean isFinished() {
        return finished;
    }

}
