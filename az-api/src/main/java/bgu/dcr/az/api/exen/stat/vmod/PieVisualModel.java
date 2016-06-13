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
