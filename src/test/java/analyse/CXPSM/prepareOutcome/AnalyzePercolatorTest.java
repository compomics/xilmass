/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import static analyse.CXPSM.NameTargetDecoy.getAccs;
import analyse.CXPSM.outcome.PercolatorResult;
import config.ConfigHolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sule
 */
public class AnalyzePercolatorTest {

    public AnalyzePercolatorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of filter method, of class AnalyzePercolator.
     */
    @Test
    public void testFilterAndRun() throws Exception {
        System.out.println("filter");
        File test_file = new File("Data/Test/analyze/percolator_xilmass_partial_elite.txt");
        File output = new File("Data/Test/analyze/percolator_non_redundant.txt"),
                percolatorXilmassFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.xilmass.elite")),
                prediction = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("prediction")),
                psms_contamination = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("psms.contaminant.elite")),
                database = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("database.file"));
        String proteinA = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinA"),
                proteinB = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinB");
        String[] protein_names = {proteinA, proteinB};
        HashMap<String, String> accs = getAccs(database);
        double qvalue = 0.05;

        AnalyzePercolator instance = new AnalyzePercolator(output, percolatorXilmassFolder, prediction, psms_contamination, protein_names, accs, true, qvalue, true);
        instance.run();
        int line = readFile(output);
        assertEquals(139, line);

        instance = new AnalyzePercolator(output, test_file, prediction, psms_contamination, protein_names, accs, true, qvalue, true);
        HashMap<String, ArrayList<PercolatorResult>> id_and_percolatorResults = prepare();
        ArrayList<PercolatorResult> result = instance.filter(id_and_percolatorResults);
        assertEquals(2, result.size());
    }

    private int readFile(File output) throws FileNotFoundException, IOException {
        int num = 0;
        BufferedReader br = new BufferedReader(new FileReader(output));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spec")) {
                String[] split = line.split("\t");
                num++;
            }
        }
        return num;
    }

    /**
     * Test of returnRandomIndex method, of class AnalyzePercolator.
     */
    @Test
    public void testReturnRandomIndex() {
        System.out.println("returnRandomIndex");
        int list_size = 5;
        int result = AnalyzePercolator.returnRandomIndex(list_size);
        System.out.println(result);
        assertTrue(result < 6);
        assertTrue(result > -1);

        result = AnalyzePercolator.returnRandomIndex(2);
        System.out.println("with 2 " + result);
        assertTrue(result < 2);
        assertTrue(result > -1);

        result = AnalyzePercolator.returnRandomIndex(2);
        System.out.println("with 2 " + result);
        assertTrue(result < 2);
        assertTrue(result > -1);

        result = AnalyzePercolator.returnRandomIndex(2);
        System.out.println("with 2 " + result);
        assertTrue(result < 2);
        assertTrue(result > -1);

        result = AnalyzePercolator.returnRandomIndex(list_size);
        System.out.println(result);
        assertTrue(result < 6);

        result = AnalyzePercolator.returnRandomIndex(4);
        System.out.println(result);
        assertTrue(result < 5);
    }

    private HashMap<String, ArrayList<PercolatorResult>> prepare() throws Exception {

        HashMap<String, ArrayList<PercolatorResult>> all = new HashMap<String, ArrayList<PercolatorResult>>();
        String pepA = "-.LFNAIIHRHKPMLIDMNKVYR(10)--DERDRVQKKTFTK(9).-",
                pepB = "-.LFNAIIHRHKPMLIDMNKVYR(18)--DERDRVQKKTFTK(9).-",
                pepC = "-.SLGQNPTEAELQDM[15.99]INEVDADGNGTIDFPEFLTMMARKM[15.99]K(38)--KTFTK(1).-",
                pepD = "-.SLGQNPTEAELQDM[15.99]INEVDADGNGTIDFPEFLTMM[15.99]ARKMK(38)--KTFTK(1).-",
                pepE = "-.SLGQNPTEAELQDM[15.99]INEVDADGNGTIDFPEFLTM[15.99]MARKMK(38)--KTFTK(1).-",
                proteins = "Q15149(171-191)-Q15149(17-29)",
                proteinsCE = "P62158(39-78)-Q15149(25-29)";
        File database = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("database.file"));
        String proteinA = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinA"),
                proteinB = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinB");
        String[] protein_names = {proteinA, proteinB};
        HashMap<String, String> accs = getAccs(database);
        PercolatorResult o = new PercolatorResult("Probe2_v_x1_top15HCD-1.mgf", "T-8058", pepA, proteins, "intra_proteins", 0, 0, 0.319, accs, true, true),
                o2 = new PercolatorResult("Probe2_v_x1_top15HCD-1.mgf", "T-8058", pepB, proteins, "intra_proteins", 0, 0, 0.319, accs, true, true),
                o3 = new PercolatorResult("Probe2_v_x1_top15HCD-1.mgf", "T-835", pepC, proteinsCE, "inter_proteins", 0.21, 0, 0.0732908, accs, true, true),
                o4 = new PercolatorResult("Probe2_v_x1_top15HCD-1.mgf", "T-835", pepD, proteinsCE, "inter_proteins", 0.21, 0, 0.0732908, accs, true, true),
                o5 = new PercolatorResult("Probe2_v_x1_top15HCD-1.mgf", "T-835", pepE, proteinsCE, "inter_proteins", 0.21, 0, 0.0732908, accs, true, true);

        ArrayList<PercolatorResult> first = new ArrayList<PercolatorResult>(),
                second = new ArrayList<PercolatorResult>();
        first.add(o);
        first.add(o2);
        second.add(o3);
        second.add(o4);
        second.add(o5);
        all.put("Probe2_v_x1_top15HCD-1.mgf_8058", first);
        all.put("Probe2_v_x1_top15HCD-1.mgf_835", second);
        return all;
    }

    /**
     * Test of run method, of class AnalyzePercolator.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        AnalyzePercolator instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of filter method, of class AnalyzePercolator.
     */
    @Test
    public void testFilter() throws Exception {
        System.out.println("filter");
        HashMap<String, ArrayList<PercolatorResult>> id_and_percolatorResults = null;
        AnalyzePercolator instance = null;
        ArrayList<PercolatorResult> expResult = null;
        ArrayList<PercolatorResult> result = instance.filter(id_and_percolatorResults);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
