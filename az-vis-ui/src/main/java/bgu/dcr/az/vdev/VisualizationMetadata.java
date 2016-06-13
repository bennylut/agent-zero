/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev;

import bc.dsl.JavaDSL;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.escan.Registery;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exp.PanicException;
import bgu.dcr.az.utils.CodeUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class VisualizationMetadata {

    private Visualization visualization;
    private String vname;

    public VisualizationMetadata(Class<? extends Visualization> vClass) {
        try {
            visualization = vClass.newInstance();
            vname = JavaDSL.chop(CodeUtils.camelToWords(visualization.getClass().getSimpleName()), " visualization");
        } catch (InstantiationException | IllegalAccessException ex) {
            Agt0DSL.panic("cannot load visualization: " + vClass.getSimpleName(), ex);
        }
    }

    public Visualization getVisualization() {
        return visualization;
    }

    public String getName() {
        return vname;
    }

    public static List<VisualizationMetadata> load(Collection<? extends String> visList) {
        LinkedList<VisualizationMetadata> ret = new LinkedList<>();
        for (Object v : visList) {
            try {
                ret.add(new VisualizationMetadata(Registery.UNIT.getXMLEntity(v.toString())));
            } catch (PanicException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return ret;
    }

    public Visualization getNewVisualization() {
        try {
            return visualization.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Agt0DSL.panic("cannot load visualization: " + visualization.getClass().getSimpleName(), ex);
        }
        
        return null;
    }
}
