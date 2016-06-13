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
package bgu.dcr.az.exen.correctness;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.ExecutionResult;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "hill-climbing-tester")
public class HillClimbingCorrectnessTester extends AbstractCorrectnessTester {

    @Variable(name = "check-maximization", description = "check that the algorithm find result with more(true)/less(false) cost in each tick", defaultValue="true")
    boolean maxi = true;
    private PreparedStatement maxPstat = null;
    private PreparedStatement minPstat = null;

    @Override
    public CorrectnessTestResult test(Execution exec, ExecutionResult result) {
        try {

            System.out.println("--- Testing Solution ---");
            System.out.println("waiting for the Statistics to be writen to the database");
            DatabaseUnit.UNIT.awaitStatistics();
            Database db = DatabaseUnit.UNIT.getDatabase();


            int execn = exec.getTest().getCurrentExecutionNumber();
            System.out.println("quering statistics database");
            //            ResultSet res = db.query(""
            //                    + "SELECT a.ID FROM "
            //                    + "SOLUTION_QUALITY as a, "
            //                    + "SOLUTION_QUALITY as b "
            //                    + "WHERE a.ticknum > b.ticknum "
            //                    + "AND a.execution = " + execn + " "
            //                    + "AND a.execution = b.execution "
            //                    + "AND a.solquality " + sign + " b.solquality");

            if (maxPstat == null || minPstat == null) {
                maxPstat = DatabaseUnit.UNIT.prepare(""
                        + "select id from "
                        + "SOLUTION_QUALITY as a "
                        + "where a.execution = ? "
                        + "and a.prevSolutionQuality <> -1 "
                        + "and a.solQuality < a.prevSolutionQuality");

                minPstat = DatabaseUnit.UNIT.prepare(""
                        + "select id from "
                        + "SOLUTION_QUALITY as a "
                        + "where a.execution = ? "
                        + "and a.prevSolutionQuality <> -1 "
                        + "and a.solQuality > a.prevSolutionQuality");
            }

//            ResultSet res = db.query(""
//                    + "select id from "
//                    + "SOLUTION_QUALITY as a "
//                    + "where a.execution = " + execn + " "
//                    + "and a.prevSolutionQuality <> -1 "
//                    + "and a.solQuality " + sign + " a.prevSolutionQuality");
            ResultSet res = null;
            if (maxi){
                maxPstat.setObject(1, execn);
                res = maxPstat.executeQuery();
            }else {
                minPstat.setObject(1, execn);
                res = minPstat.executeQuery();
            }
            
            System.out.println("quering complete - testing solution");
            if (res.next()) {
                return new CorrectnessTestResult(null, false);
            } else {
                return new CorrectnessTestResult(null, true);
            }


        } catch (SQLException ex) {
            Logger.getLogger(HillClimbingCorrectnessTester.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
