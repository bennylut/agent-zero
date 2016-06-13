/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.dsl;

import bc.dsl.JavaDSL.Fn;
import static bc.dsl.JavaDSL.eq;
import static bc.dsl.JavaDSL.filter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nu.xom.*;

/**
 *
 * @author bennyl
 */
public class XNavDSL {

    public static List<Element> all(String what, Element parent) {
        List<Element> ret = new LinkedList<Element>();
        if (eq(parent.getLocalName(), what)) {
            ret.add(parent);
        } else {
            Elements elements = parent.getChildElements();
            for (int i = 0; i < elements.size(); i++) {
                ret.addAll(all(what, elements.get(i)));
            }
        }

        return ret;
    }

    public static Element parent(Element child){
        return (Element) child.getParent();
    }

    public static List<Element> childs(String what, Element parent) {
        List<Element> ret = new LinkedList<Element>();
        Elements elements = parent.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            final Element e = elements.get(i);
            if (eq(e.getLocalName(), what)) {
                ret.add(e);
            }
        }

        return ret;
    }

    public static List<Element> childs(Element parent) {
        List<Element> ret = new LinkedList<Element>();
        Elements elements = parent.getChildElements();
        for (int i = 0; i < elements.size(); i++) {
            ret.add(elements.get(i));
        }

        return ret;
    }

    public static boolean isa(Element e, String type) {
        return eq(e.getLocalName(), type);
    }

    public static Document xload(File xml) throws ParsingException, IOException {
        FileInputStream fis = new FileInputStream(xml);
        Builder b = new Builder();
        Document doc = b.build(fis);
        return doc;
    }
    
    public static boolean named(Element e, String name){
        return eq(name, e.getAttributeValue("name"));
    }
    
    public static boolean typed(Element e, String name){
        return eq(name, e.getAttributeValue("type"));
    }
    
    public static String attr(Element e, String attr){
        final String val = e.getAttributeValue(attr);
        return val == null? "": val;
    }

    public static boolean hasAttr(Element e, String attr){
        final String val = e.getAttributeValue(attr);
        return val != null;
    }
    
    public static String attr(Object e, String attr){
        return attr((Element)e, attr);
    }
    
    public static List<Element> filterByAttr(List<Element> elements, final String attr, final String value){
        return filter(elements, new Fn<Boolean>() {
            @Override
            public Boolean invoke(Object... args) {
                return eq(attr(args[0], attr), value);
            }
        });
    }
    
    public static Map<String, String> attributes(Element e){
        Map<String, String> ret = new HashMap<String, String>();
        for (int i=0; i<e.getAttributeCount(); i++){
            Attribute a = e.getAttribute(i);
            ret.put(a.getLocalName(), a.getValue());
        }
        
        return ret;
    }
}
