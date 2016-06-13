/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

/**
 *
 * @author bennyl
 */
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaDSL {

    private static final Logger log = Logger.getLogger(JavaDSL.class.getName());

    public static void sleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ex) {
            log(ex);
        }
    }

    public static void log(Exception ex, String desc) {
        log.log(Level.SEVERE, desc, ex);
    }

    public static void log(Exception ex) {
        log(ex, null);
    }

    public static void log(String what) {
        //log.info(what);
        System.out.println(what);
    }

    public static boolean eq(Object str1, Object str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean eq(Object... all) {
        if (all.length == 0) {
            return true;
        }

        for (int i = 1; i < all.length; i++) {
            if (!eq(all[0], all[i])) {
                return false;
            }
        }

        return true;
    }

    public static boolean eqor(String str1, String... ors) {
        for (String o : ors) {
            if (eq(str1, o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean eqor(Object str1, Object... ors) {
        for (Object o : ors) {
            if (str1.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static String str(Collection col) {
        if (col == null) {
            return "![]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object c : col) {
            sb.append(c.toString()).append(", ");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("]");

        return sb.toString();
    }

    public static String drop(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(sum);
    }

    public static String take(String from, int sum) {
        if (sum < 0) {
            sum = from.length() + sum;
        }

        return from.substring(0, sum);
    }

    public static boolean nummeric(char who) {
        return who >= '0' && who <= '9';
    }

    public static boolean nummeric(String who) {
        if (who.isEmpty()) {
            return false;
        }
        for (char c : who.toCharArray()) {
            if (!nummeric(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean integeric(String who) {
        return who.length() < 10 && nummeric(who);
    }

    public static boolean between(int who, int a, int b) {
        return who >= a && who <= b;
    }

    public static String lc(String text) {
        return text == null ? null : text.toLowerCase();
    }

    public static <T extends Enum> T enumarate(String what, Class<T> cls) {
        EnumSet es = EnumSet.allOf(cls);
        for (Object e : es) {
            if (eq(((Enum) e).name(), what)) {
                return (T) e;
            }
        }

        return null;
    }

    public static <T> List<T> list(T... data) {
        ArrayList al = new ArrayList(data.length);
        for (T d : data) {
            al.add(d);
        }
        return al;
    }

    public static <T1, T2> SimpleEntry<T1, T2> cons(T1 t1, T2 t2) {
        return new SimpleEntry<T1, T2>(t1, t2);
    }

    public static Object car(Object obj) {
        return car((SimpleEntry) obj);
    }

    public static Object cdr(Object obj) {
        return cdr((SimpleEntry) obj);
    }

    public static <T> T car(SimpleEntry<T, ?> pair) {
        return pair.getKey();
    }

    public static <T> T cdr(SimpleEntry<?, T> pair) {
        return pair.getValue();
    }

    public static String strpad(String pad, int n) {
        if (n < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(pad.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public static <T> List<T> filter(List<T> what, Fn<Boolean> filter) {
        List<T> ret = new LinkedList<T>();
        for (T t : what) {
            if (filter.invoke(t)) {
                ret.add(t);
            }
        }

        return ret;
    }

    public static interface Fn<T> {

        public T invoke(Object... args);
    }

    public static abstract class Fn1<RET, GET> implements Fn<RET> {

        public abstract RET invoke(GET arg);

        public RET invoke(Object... args) {
            return invoke((GET) args[0]);
        }
    }

    public static <T, E> List<T> map(Collection<E> l, Fn1<T, E> fn) {
        List<T> ret = new LinkedList<T>();
        for (E e : l) {
            ret.add(fn.invoke(e));
        }

        return ret;
    }

    public static <T, E> List<T> map(E[] l, Fn1<T, E> fn) {
        List<T> ret = new LinkedList<T>();
        for (E e : l) {
            ret.add(fn.invoke(e));
        }

        return ret;
    }

    public static boolean ne(Object a, Object b) {
        return !eq(a, b);
    }

    public static int min(int... a) {
        int min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (min > a[i]) {
                min = a[i];
            }
        }

        return min;
    }

    public static int max(int... a) {
        int min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (min < a[i]) {
                min = a[i];
            }
        }

        return min;
    }

    public static String cstr(Object x) {
        return "" + x;
    }

    /**
     * if nummeric?(cstr(x)) return cstr(x) as int else return null
     * @param x
     * @return
     */
    public static Integer cint(Object x) {
        String s = cstr(x);
        if (nummeric(s)) {
            return Integer.parseInt(s);
        } else {
            return null;
        }
    }

    public static int cint(Object x, int def) {
        Integer ret = cint(x);
        return ret == null ? def : ret;
    }

    public static <T> T cast(Object what) {
        try {
            return (T) what;
        } catch (ClassCastException ex) {
            return null;
        }
    }

    public static <K, V> Map<K, V> assoc(Map<K, V> map, Object... kvs) {
        for (int i = 0; i < kvs.length; i += 2) {
            map.put((K) kvs[i], (V) kvs[i + 1]);
        }

        return map;
    }
    
    public static <K, V> Map<K, V> cassoc(K k, V v, Object... kvs){
        Map<K,V> ret = new HashMap<K, V>();
        ret.put(k, v);
        assoc(ret, kvs);
        return ret;
    }

    public static boolean isAlphaNummeric(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9');
    }

    public static String camelCase(String what) {
        char[] chars = what.toCharArray();
        StringBuilder sb = new StringBuilder();
        boolean camel = true;
        char c;

        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
            if (isAlphaNummeric(c)) {
                if (camel) {
                    camel = false;
                    c = Character.toUpperCase(c);
                }

                sb.append(c);
            } else {
                camel = true;
            }
        }

        return sb.toString();
    }
    
    
    public static void throwUncheked(Throwable e) {
        JavaDSL.<RuntimeException>throwAny(e);
    }
   
    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(Throwable e) throws E {
        throw (E)e;
    }
    
    public static <T,K,V> Map<K,V> innerMap(Map<T, Map<K,V>> map, T key){
        Map<K, V> inner = map.get(key);
        if (inner == null){
            inner = new HashMap<K, V>();
            map.put(key, inner);
        }
        
        return inner;
    } 
    
    /**
     * return a string that has the begining of what but if what ends with ending - it removed 
     * @param what
     * @param ending
     * @return 
     */
    public static String chop(String what, String ending){
        if(what.endsWith(ending)){
            return what.substring(0, what.length() - ending.length());
        }
        
        return what;
    }
}
