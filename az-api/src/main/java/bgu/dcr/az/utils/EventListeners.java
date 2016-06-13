/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
