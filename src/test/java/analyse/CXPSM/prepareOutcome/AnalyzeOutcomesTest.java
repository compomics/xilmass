/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.Outcome;
import analyse.CXPSM.outcome.XilmassResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
public class AnalyzeOutcomesTest {

    public AnalyzeOutcomesTest() {
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
     * Test of getTargetDecoy method, of class AnalyzeOutcomes.
     */
    @Test
    public void testGetTargetDecoy() {
        System.out.println("getTargetDecoy");

        String proteinA = "DECOY" + "Q145",
                proteinB = "P145";
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();
        String expResult = "TD";
        String result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);

        proteinA = "Q145";
        proteinB = "DECOY" + "P145";
        instance = new AnalyzeOutcomesImpl();
        expResult = "TD";
        result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);

        proteinA = "Q145";
        proteinB = "DECOY" + "P145";
        instance = new AnalyzeOutcomesImpl();
        expResult = "TD";
        result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);

        proteinA = "DECOY" + "Q145";
        proteinB = "DECOY" + "P145";
        instance = new AnalyzeOutcomesImpl();
        expResult = "DD";
        result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);

        proteinA = "Q145";
        proteinB = "REVERSED" + "P145";
        instance = new AnalyzeOutcomesImpl();
        expResult = "TD";
        result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);

        proteinA = "Q145";
        proteinB = "P145";
        instance = new AnalyzeOutcomesImpl();
        expResult = "TT";
        result = instance.getTargetDecoy(proteinA, proteinB);
        assertEquals(expResult, result);
    }

    public class AnalyzeOutcomesImpl extends AnalyzeOutcomes {

        public void run() throws FileNotFoundException, IOException {
        }
    }

    /**
     * Test of getValidatedPSMs method, of class AnalyzeOutcomes.
     */
    @Test
    public void testGetValidatedPSMs() throws Exception {
        System.out.println("getValidatedPSMs");
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();

        ArrayList<XilmassResult> res = new ArrayList<XilmassResult>();
        // start with Xilmass objects
        File folder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\xilmass_validated_testing\\test_mc4_TMSAm_HCD_contaminants");
        // folder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\mc4_TMSAm_HCD_contaminants\\output\\elite");
        for (File f : folder.listFiles()) {
            System.out.println(f.getName());
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("SpectrumFile") && (!line.startsWith("File")) && !line.isEmpty()) {
                    // make sure that a PSM is not contaminant-derived
                    if (!line.contains("contaminant")) {
                        XilmassResult r = new XilmassResult(line, "ScoreName");
                        // set if it is target or decoy..
                        r.setTarget_decoy(instance.getTargetDecoy(r.getProteinA(), r.getProteinB()));
                        res.add(r);
                    }
                }
            }
        }
        // now sort these
        Collections.sort(res, XilmassResult.ScoreDSCBasedTDs);
        ArrayList<Outcome> resOutcome = new ArrayList<Outcome>();
        for (XilmassResult r : res) {
            resOutcome.add(r);
        }
        System.out.println("Validated list...");
        double fdr = 0.01;
        instance.isPIT = false;
        // it contains only TTs not, TDs or DDs
        ArrayList<Outcome> result = instance.getValidatedPSMs(resOutcome, fdr, true);
        assertEquals(24, result.size());
        fdr = 0.05;
        result = instance.getValidatedPSMs(resOutcome, fdr, true);
        assertEquals(29, result.size());
        // update to calculate PLink-FDR
        resOutcome.get(26).setTarget_decoy("DD");
        instance.isPIT = false;
        result = instance.getValidatedPSMs(resOutcome, 0.05, true);
        assertEquals(30, result.size());

        // try if DD is found before finding any TD..
        resOutcome.get(25).setTarget_decoy("DD");
        result = instance.getValidatedPSMs(resOutcome, 0.05, true);
        assertEquals(24, result.size());
        
    }

}
