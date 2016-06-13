/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.stat.vmod;

import bgu.dcr.az.api.exen.stat.VisualModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class BarVisualModel implements VisualModel {

    private Map<String, Map<String, Double>> values = new HashMap<String, Map<String, Double>>();
    private String title;
    private String domainAxisName;
    private String rangeAxisName;

    public BarVisualModel(String title, String domainAxisName, String rangeAxisName) {
        this.title = title;
        this.domainAxisName = domainAxisName;
        this.rangeAxisName = rangeAxisName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void putBar(String algorithm, String barName, double value) {
        Map<String, Double> map = values.get(algorithm);
        if (map == null) {
            map = new LinkedHashMap<String, Double>();
            values.put(algorithm, map);
        }

        map.put(barName, value);
    }
    
    public void load(String algorithmColumn, String barNameColumn, String valueColumn, ResultSet data){
        try {
            while (data.next()){
                putBar(data.getString(algorithmColumn), data.getString(barNameColumn), data.getDouble(valueColumn));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BarVisualModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, Double> getValues(String algorithm) {
        return values.get(algorithm);
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
    public List<String> getAlgorithms() {
        return new LinkedList<String>(values.keySet());
    }

    @Override
    public String getDomainAxisLabel() {
        return domainAxisName;
    }

    @Override
    public String getRangeAxisLabel() {
        return rangeAxisName;
    }
}
