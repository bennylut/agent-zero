/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ConfigurationMetadata {
    private static Map<Class, ConfigurationMetadata[]> cache = new HashMap<Class, ConfigurationMetadata[]>();
    public final String name;
    public final String description;
    public final boolean isList;
    public final Class type;
    private Method putMtd;
    private Method getMtd;

    private ConfigurationMetadata(Method m) {
        try {
            Configuration conf = (Configuration) m.getAnnotation(Configuration.class);
            m.setAccessible(true);
            putMtd = m;
            this.name = conf.name();
            this.description = conf.description();
            this.type = m.getParameterTypes()[0];
            if (m.getName().startsWith("add")) {
                isList = true;
                getMtd = m.getDeclaringClass().getMethod(m.getName().replaceFirst("add", "get") + "s");
            } else {
                isList = false;
                getMtd = m.getDeclaringClass().getMethod(m.getName().replaceFirst("set", "get"));
            }
            getMtd.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            throw new InternalErrorException("cannot read configuration", ex);
        } catch (SecurityException ex) {
            throw new InternalErrorException("cannot read configuration", ex);
        }
    }

    public static ConfigurationMetadata[] scan(Class c) {
        if (cache.containsKey(c)) {
            return cache.get(c);
        }
        List<Method> confs = ReflectionUtil.getRecursivelyMethodsWithAnnotation(c, Configuration.class);
        ConfigurationMetadata[] ret = new ConfigurationMetadata[confs.size()];
        int i = 0;
        for (Method conf : confs) {
            ret[i++] = new ConfigurationMetadata(conf);
        }
        cache.put(c, ret);
        return ret;
    }

    public Object get(Object from) {
        try {
            return getMtd.invoke(from);
        } catch (IllegalAccessException ex) {
            throw new InternalErrorException("cannot get configuration", ex);
        } catch (IllegalArgumentException ex) {
            throw new InternalErrorException("cannot get configuration", ex);
        } catch (InvocationTargetException ex) {
            throw new InternalErrorException("cannot get configuration", ex);
        }
    }

    public void insert(Object on, Object what) {
        try {
            putMtd.invoke(on, what);
        } catch (IllegalAccessException ex) {
            throw new InternalErrorException("cannot insert configuration", ex);
        } catch (IllegalArgumentException ex) {
            throw new InternalErrorException("cannot insert configuration", ex);
        } catch (InvocationTargetException ex) {
            throw new InternalErrorException("cannot insert configuration", ex);
        }
    }

    public static void bubbleDownVariable(Object on, String var, Object value) {
        Object n;
        ConfigurationMetadata[] confs = scan(on.getClass());
        VariableMetadata.tryAssign(on, var, value);
        for (ConfigurationMetadata conf : confs) {
            if (conf.isList) {
                List list = (List) conf.get(on);
                for (Object c : list) {
                    bubbleDownVariable(c, var, value);
                }
            } else {
                n = conf.get(on);
                if (n != null) {
                    bubbleDownVariable(n, var, value);
                }
            }
        }
    }

    /**
     * after finish configuring all the elements call this method so that
     * all the configuration aware object that you just done configure will get refreshed
     * @param o
     */
    public static void notifyAfterExternalConfiguration(Object o) {
        ConfigurationMetadata[] confs = scan(o.getClass());
        if (o instanceof ExternalConfigurationAware) {
            ((ExternalConfigurationAware) o).afterExternalConfiguration();
        }
        Object n;
        for (ConfigurationMetadata conf : confs) {
            if (conf.isList) {
                List list = (List) conf.get(o);
                for (Object c : list) {
                    notifyAfterExternalConfiguration(c);
                }
            } else {
                n = conf.get(o);
                if (n != null) {
                    notifyAfterExternalConfiguration(n);
                }
            }
        }
    }

    public static boolean canAccept(Object o, Class c) {
        ConfigurationMetadata[] confs = scan(o.getClass());
        for (ConfigurationMetadata conf : confs) {
            if (conf.type.isAssignableFrom(c)) {
                return true;
            }
        }
        return false;
    }

    public static void insertConfiguration(Object o, Object conf) {
        ConfigurationMetadata[] confs = scan(o.getClass());
        for (ConfigurationMetadata c : confs) {
            if (c.type.isAssignableFrom(conf.getClass())) {
                c.insert(o, conf);
                return;
            }
        }
    }
    
}
