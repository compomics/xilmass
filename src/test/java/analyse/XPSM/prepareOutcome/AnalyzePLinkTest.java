/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.XPSM.prepareOutcome;

import analyse.XPSM.outcome.PLinkResult;
import java.io.File;
import java.util.ArrayList;
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
public class AnalyzePLinkTest {
    
    public AnalyzePLinkTest() {
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
     * Test of run method, of class AnalyzePLink.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        boolean isConventialFDR= false;
        double fdr_cutoff = 0.05;
        File pLinkFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_DSSHCDElite_ONLYTARGET_mc4\\query"),
                pLinkAllOutput = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_DSSHCDElite_ONLYTARGET_mc4\\testall222.txt"),
                output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_DSSHCDElite_ONLYTARGET_mc4\\test.txt"),
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
                psms_contamination = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\10FDRPSMs/hcd_dss_elite_cleaning_fdr10PSMs.txt");
        String [] protein_names = {"O", "P"};
        
        AnalyzePLink instance = new AnalyzePLink(pLinkFolder, pLinkAllOutput, output, prediction, psms_contamination, protein_names, fdr_cutoff, isConventialFDR);

        instance.run();
        
        ArrayList<PLinkResult> allRes = instance.getAllResults();
        assertEquals(484, allRes.size());
    }

    /**
     * Test of getAllResults method, of class AnalyzePLink.
     */
    @Test
    public void testGetAllResults() {
        System.out.println("getAllResults");
        AnalyzePLink instance = null;
        ArrayList<PLinkResult> expResult = null;
        ArrayList<PLinkResult> result = instance.getAllResults();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

  
}
