/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.proxies;

import bc.dsl.JavaDSL;
import bc.proxies.Wrapper.OperationMediator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public class Wrapper<T> implements InvocationHandler {

    private static Map<Class, Object> primitiveDefaults = JavaDSL.<Class, Object>cassoc(
            int.class, 0,
            float.class, 0f,
            double.class, 0.0,
            char.class, '1',
            boolean.class, false);
    
    private static Method PERFORM_METHOD;
    private static Method BEFORE_METHOD;
    private static Method AFTER_METHOD;
    private static Method APPLY_METHOD;

    static {
        try {
            PERFORM_METHOD = WrapConfigurator.class.getMethod("perform", Operation.class);
            BEFORE_METHOD = OperationMediator.class.getMethod("before");
            APPLY_METHOD = WrapConfigurator.class.getMethod("applyOn", Object.class);
            AFTER_METHOD = OperationMediator.class.getMethod("after");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
    
    private T target;
    private HashMap<Method, Operation> beforeMap = new HashMap<Method, Operation>();
    private HashMap<Method, Operation> afterMap = new HashMap<Method, Operation>();
    private Operation lastOp;
    private Method lastMethod;

    private Wrapper() {
        //NEVER INSTANTIATE DIRECTLY!
    }

    public static <T> WrapConfigurator<T> configure(Class<T> interfaze) {

        return (WrapConfigurator<T>) java.lang.reflect.Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{interfaze, OperationMediator.class, WrapConfigurator.class},
                new Wrapper<T>());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.equals(PERFORM_METHOD)) {
            lastOp = (Operation) args[0];
            return proxy;
        } else if (method.equals(BEFORE_METHOD) || method.equals(AFTER_METHOD)) {
            lastMethod = method;
            return proxy;
        } else if (method.equals(APPLY_METHOD)) {
            target = (T) args[0];
            return proxy;
        } else if (lastMethod != null) {
            if (lastMethod.equals(BEFORE_METHOD)) {
                beforeMap.put(method, lastOp);
            }else if (lastMethod.equals(AFTER_METHOD)){
                afterMap.put(method, lastOp);
            }
            lastMethod = null;
            if (method.getReturnType().isPrimitive()){
                return primitiveDefaults.get(method.getReturnType());
            }
            return null;
        } else {
            if (beforeMap.containsKey(method)) {
                beforeMap.get(method).operate(args);
            }
            
            Object ret = method.invoke(target, args);
            
            if (afterMap.containsKey(method)) {
                afterMap.get(method).operate(args);
            }
            
            return ret;
        }
    }

    public static interface OperationMediator<T> {
        T before();
        T after();
    }

    public static interface WrapConfigurator<T> {
        OperationMediator<T> perform(Operation op);
        T applyOn(T target);
    }
}
