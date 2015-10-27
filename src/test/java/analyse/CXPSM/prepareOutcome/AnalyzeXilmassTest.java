/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.prepareOutcome.AnalyzeXilmass;
import analyse.CXPSM.outcome.XilmassResult;
import java.io.File;
import java.io.IOException;
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
public class AnalyzeXilmassTest {

    public AnalyzeXilmassTest() {
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
     * Test of run method, of class AnalyzeXilmass.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        AnalyzeXilmass instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValidatedPSMs method, of class AnalyzeXilmass.
     */
    @Test
    public void testGetValidatedPSMs() throws IOException {
        System.out.println("getValidatedPSMs");
        File xilmassFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\xilmass_validated_testing/test_mc4_TMSAm_HCD_contaminants"),
                output = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\xilmass_validated_testingtesting_output.txt"),
                predictionFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
                psms_contaminant = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\10FDRPSMs/hcd_dss_elite_cleaning_fdr10PSMs.txt");
        double fdr = 0.05;
        boolean isConventialFDR = false,
                isMS1ErrPPM = true;
        String target_names[] = {"P62158", "Q15149"};
        AnalyzeXilmass instance = new AnalyzeXilmass(xilmassFolder, output, predictionFile, psms_contaminant, target_names, fdr, isConventialFDR, isMS1ErrPPM, false, false);
        instance.run();
        HashSet<XilmassResult> result = instance.getValidatedPSMs();
        assertEquals(29, result.size());

        instance = new AnalyzeXilmass(xilmassFolder, output, predictionFile, psms_contaminant, target_names, 0.36, true, isMS1ErrPPM, false, false);
        instance.run();
        result = instance.getValidatedPSMs();
        assertEquals(31, result.size());

    }

}
