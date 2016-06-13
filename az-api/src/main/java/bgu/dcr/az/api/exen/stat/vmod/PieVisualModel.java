/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.stat.vmod;

import bgu.dcr.az.api.exen.stat.VisualModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * //NOT READY!!!
 * 
 */
public class PieVisualModel implements VisualModel {

    private Map<String, Map<String, Double>> values = new HashMap<String, Map<String, Double>>();
    private String title;

    public PieVisualModel(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void putSlice(String algorithm, String sliceName, double value) {
        Map<String, Double> map = values.get(algorithm);
        if (map == null) {
            map = new LinkedHashMap<String, Double>();
            values.put(algorithm, map);
        }

        map.put(sliceName, value);
    }

    public Map<String, Double> getValues(String algorithm) {
        return values.get(algorithm);
    }

    @Override
    public List<String> getAlgorithms() {
        return new LinkedList<String>(values.keySet());
    }

    @Override
    public void exportToCSV(File csv) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csv);
            pw.println("algorithm, name, value");
            for (Entry<String, Map<String, Double>> v : values.entrySet()) {
                for (Entry<String, Double> vs : v.getValue().entrySet()) {
                    pw.println("" + v.getKey() + ", " + vs.getKey() + ", " + vs.getValue());
                }
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LineVisualModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    @Override
    public String getDomainAxisLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRangeAxisLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
