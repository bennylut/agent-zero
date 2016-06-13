/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import bgu.dcr.az.api.DeepCopyable;
import com.esotericsoftware.kryo.Kryo;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author bennyl
 */
public class DeepCopyUtil {

    private static final ThreadLocal<Kryo> kryo = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo k = new Kryo();
            k.setAsmEnabled(true);
            k.setInstantiatorStrategy(new StdInstantiatorStrategy());
            k.setRegistrationRequired(false);
            return k;
        }

    };

    /**
     * @param <T>
     * @param orig
     * @return deep copy of orig using a generic deep copy framework
     */
    public static <T> T deepCopy(T orig) {
        if (orig instanceof Enum || orig instanceof Throwable) {
            return orig;
        }

        if (orig instanceof DeepCopyable) {
            return (T) ((DeepCopyable) orig).deepCopy();
        }

        return kryo.get().copy(orig);
    }
}
