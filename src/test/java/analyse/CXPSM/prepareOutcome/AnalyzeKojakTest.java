/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sule
 */
public class AnalyzeKojakTest {

    public AnalyzeKojakTest() {
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
     * Test of run method, of class AnalyzeKojak.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        File kojakFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\kojak_validated_testing"),
                output = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\kojak_validated_testingtesting_output.txt"),
                predictionFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
                psms_contaminant = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\10FDRPSMs/hcd_dss_elite_cleaning_fdr10PSMs.txt"),
                databaseF = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases\\test/target_Rdecoy_cam_plectin.fasta");
        double fdr = 1;
        boolean isConventialFDR = false;
        String target_names[] = {"P62158", "Q15149"};

        AnalyzeKojak instance = new AnalyzeKojak(output, kojakFolder, predictionFile, psms_contaminant, databaseF, target_names, fdr, isConventialFDR);
        instance.run();
        instance.getValidatedPSMs();
        assertEquals(0, instance.getValidatedPSMs().size());
        
        kojakFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\Kojak_mc4_v136\\output\\elite");
        instance = new AnalyzeKojak(output, kojakFolder, predictionFile, psms_contaminant, databaseF, target_names, fdr, isConventialFDR);
        instance.run();
        instance.getValidatedPSMs();
    }

}
