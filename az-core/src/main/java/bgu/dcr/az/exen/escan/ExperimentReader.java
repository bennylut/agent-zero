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
package bgu.dcr.az.exen.escan;

import bgu.dcr.az.api.exen.escan.Registery;
import bc.dsl.ReflectionDSL;
import bgu.dcr.az.api.exen.escan.ConfigurationMetadata;
import bgu.dcr.az.api.exp.InvalidValueException;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.escan.VariableMetadata;
import bgu.dcr.az.exen.ExperimentImpl;
import java.io.File;
import static bc.dsl.XNavDSL.*;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.ExecutionSelector;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.Configuration;
import bgu.dcr.az.exen.AbstractTest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.ParsingException;

/**
 *
 * @author bennyl
 */
public class ExperimentReader {

    
    /**
     * writes the given object using the given print writer
     * the object must be configurable using the Configuration and Variable annotations.
     * @param object
     * @param pw 
     */
    public static void write(Object object, PrintWriter pw) {
        String confName = Registery.UNIT.getEntityName(object);
        Element e = new Element(confName);
        write(object, e);

        pw.append(e.toXML());
    }

    private static void write(Object conf, Element root) {
        for (VariableMetadata v : VariableMetadata.scan(conf)) {
            root.addAttribute(new Attribute(v.getName(), v.getCurrentValue().toString()));
        }


        for (ConfigurationMetadata c : ConfigurationMetadata.scan(conf.getClass())) {
            List list;
            if (c.isList) {
                 list = (List) c.get(conf);
            } else {
                list = Arrays.asList(c.get(conf));
            }

            for (Object i : list) {
                if (i == null){
                    continue;
                }
                String confName = Registery.UNIT.getEntityName(i);
                Element e = new Element(confName);
                write(i, e);
                root.appendChild(e);
            }

        }
    }

    public static Experiment read(File from) throws IOException, InstantiationException, IllegalAccessException {
        try {
            Experiment exp = new ExperimentImpl();
            Element root = xload(from).getRootElement();

            configure(exp, root);
            
            ConfigurationMetadata.notifyAfterExternalConfiguration(exp);
            return exp;

        } catch (ParsingException ex) {
            Logger.getLogger(ExperimentReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("cannot parse file", ex);
        }
    }

    private static void configure(Object c, Element root) throws InstantiationException, IllegalAccessException, InvalidValueException {
        Object cc;
        for (Element child : childs(root)) {
            String name = child.getLocalName();
            Class cls = Registery.UNIT.getXMLEntity(name);
            if (cls == null) {
                throw new InvalidValueException("cannot parse " + name + " xml entity: no entity with that name on the registery");
            } else if (! ConfigurationMetadata.canAccept(c, cls)) {
                throw new InvalidValueException("element '" + root.getLocalName() + "' cannot contain child '" + name + "'");
            } else {
                cc = cls.newInstance();
                configure(cc, child);
                ConfigurationMetadata.insertConfiguration(c, cc);
            }
        }
        HashMap<String, Object> conf = new HashMap<String, Object>();
        VariableMetadata[] evar = VariableMetadata.scan(c);
        Map<String, VariableMetadata> varmap = new HashMap<String, VariableMetadata>();

        for (VariableMetadata v : evar) {
            varmap.put(v.getName(), v);
        }
        VariableMetadata var;

        for (Entry<String, String> a : attributes(root).entrySet()) {
            if (varmap.containsKey(a.getKey())) {
                var = varmap.get(a.getKey());
                Object value = ReflectionDSL.valueOf(a.getValue(), var.getType());
                conf.put(a.getKey(), value);
            } else {
                System.out.println("found attribute '" + a.getKey() + "' in element '" + root.getLocalName() + "' but this element not expecting this attribute - ignoring.");
            }
        }
        VariableMetadata.assign(c, conf);
    }
    
    public static List<ExecutionSelector> listExecutions(File experimentFile) throws IOException, InstantiationException, IllegalAccessException{
        Experiment exp = ExperimentReader.read(experimentFile);
        LinkedList<ExecutionSelector> selectors = new LinkedList<ExecutionSelector>();
        for (Test t : exp.getTests()){
            selectors.addAll(((AbstractTest) t).listAllExecutionSelectors());
        }
        
        return selectors;
        
    }

}
