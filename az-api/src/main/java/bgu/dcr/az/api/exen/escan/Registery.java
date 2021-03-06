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
package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ano.Algorithm;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author bennyl
 */
public enum Registery {

    UNIT;
    Map<String, Class> registeredXMLEntities = new HashMap<String, Class>();
    Map<String, Class> agents = new HashMap<String, Class>();
    Map<Class, Set<String>> entitiyInheritence = null;

    private Registery() {
        scanClasses();
    }

    public void rescanRegistery() {
        agents = new HashMap<String, Class>();
        registeredXMLEntities = new HashMap<String, Class>();
        entitiyInheritence = null;
        scanClasses();
    }

    private void scanClasses() {
        Reflections ref;
        Set<Class<?>> types;

//        ref = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("bgu.dcr.az"), ClasspathHelper.forPackage("ext.sim")).setScanners(new TypeAnnotationsScanner()));
        String[] packagesToScan = {"bgu.dcr.az", "ext.sim"};
        List<URL> packagesToScanUrls = new ArrayList<>();
        for (String p : packagesToScan) {
            packagesToScanUrls.addAll(ClasspathHelper.forPackage(p));
        }
        ref = new Reflections(new ConfigurationBuilder().addUrls(packagesToScanUrls).setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));

        //SCANNING XML ENTITIES
        System.out.println("Scanning XML Entities");
        types = ref.getTypesAnnotatedWith(Register.class);
        for (Class<?> type : types) {
            if (type.isInterface() || type.isAnonymousClass() || Modifier.isAbstract(type.getModifiers())) {
                System.out.println("Found Abstract Registered Item - ignoring it: " + type.getSimpleName());
            } else {
                final String name = type.getAnnotation(Register.class).name();
                System.out.println("Found Registered item : " + type.getSimpleName() + " as " + name);
                registeredXMLEntities.put(name, type);
            }
        }
        //SCANNING AGENTS
        System.out.println("Scanning Agents");
        types = ref.getTypesAnnotatedWith(Algorithm.class);
        for (Class<?> type : types) {
            if (type.isInterface() || type.isAnonymousClass() || Modifier.isAbstract(type.getModifiers())) {
                System.out.println("Found Abstract Agent - ignoring it: " + type.getSimpleName());
            } else {
                final String name = type.getAnnotation(Algorithm.class).name();
                System.out.println("Found Agent : " + type.getSimpleName() + " as " + name);
                agents.put(name, type);
            }
        }
    }

    public void scanEntityInheritance() {
        Set<Class> graph;
        if (entitiyInheritence == null) {
            entitiyInheritence = new HashMap<Class, Set<String>>();
            for (Entry<String, Class> ent : registeredXMLEntities.entrySet()) {
                graph = ReflectionUtil.getClassGraph(ent.getValue());
                for (Class v : graph) {
                    Set<String> inh = entitiyInheritence.get(v);
                    if (inh == null) {
                        inh = new HashSet<String>();
                        entitiyInheritence.put(v, inh);
                    }
                    inh.add(ent.getKey());
                }
            }
        }
    }

    public Set<String> getExtendingEntities(Class c) {
        if (entitiyInheritence == null) {
            scanEntityInheritance();
        }

        Set<String> ret = entitiyInheritence.get(c);
        if (ret == null) {
            return Collections.emptySet();
        }

        return ret;
    }

    public Class getXMLEntity(String type) {
        return registeredXMLEntities.get(type);
    }

    public List<Class> getAllAgentTypes() {
        return new LinkedList<Class>(agents.values());
    }

    public Set<String> getAllAlgorithmNames() {
        return new HashSet<String>(agents.keySet());
    }

    public Class<? extends Agent> getAgentByAlgorithmName(String name) {
        return agents.get(name);
    }

    public String getEntityName(Object conf) {
        return conf.getClass().getAnnotation(Register.class).name();
    }
}
