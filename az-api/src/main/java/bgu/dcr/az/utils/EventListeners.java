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
package bgu.sonar.util.evt;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.factory.cglib.CglibProxyFactory;

/**
 *
 * @author bennyl
 */
public class EventListeners<T> {

    private ConcurrentLinkedQueue<T> listeners = new ConcurrentLinkedQueue<>();
    private T shooter;
    private boolean notificationsEnabled = true;

    private EventListeners() {
    }

    public void add(T listener) {
        listeners.add(listener);
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * remove the listener from this list.
     * note that unlike most lists removing the listener from within the event handling code is permitted.
     *
     * @param listener
     */
    public void remove(T listener) {
        listeners.remove(listener);
    }

    public T fire() {
        return shooter;
    }

    public static <T> EventListeners<T> create(Class<T> type) {
        final EventListeners<T> elist = new EventListeners<T>();
        T result = (T) new CglibProxyFactory().createInvokerProxy(new Invoker() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                if (!elist.notificationsEnabled) {
                    return null;
                }
                for (T l : elist.listeners) {
                    method.invoke(l, arguments);
                }
                return null;
            }
        }, new Class[]{type});

        elist.shooter = result;
        return elist;
    }
}
