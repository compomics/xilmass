/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.prepareOutcome.AnalyzeOutcomes;
import analyse.CXPSM.outcome.KojakResult;
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
     * Test of run method, of class AnalyzeOutcomes.
     */
    @Test
    public void testRun() throws Exception {
        System.out.println("run");
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of assetTrueLinking method, of class AnalyzeOutcomes.
     */
    @Test
    public void testAssetTrueLinking() throws Exception {
        System.out.println("assetTrueLinking");
        String uniprotProAacces = "P62158";
        String uniprotProBacces = "P62158";
        int uniprotLinkingSiteA = 14;
        int uniprotLinkingSiteB = 22;
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();
        instance.prediction_file = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt");

        String result = instance.assetTrueLinking(uniprotProAacces, uniprotProBacces, uniprotLinkingSiteA, uniprotLinkingSiteB);
        assertTrue(result.startsWith("POSSIBLE"));

        result = instance.assetTrueLinking(uniprotProAacces, uniprotProBacces, 9, uniprotLinkingSiteB);
        assertFalse(result.startsWith("POSSIBLE"));
        assertTrue(result.startsWith("Not-pre"));

    }

    /**
     * Test of getContaminant_MSMS method, of class AnalyzeOutcomes.
     */
    @Test
    public void testGetContaminant_MSMS() throws Exception {
        System.out.println("getContaminant_MSMS");
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();
        HashSet<String> expResult = null;
        HashSet<String> result = instance.getContaminant_MSMS();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getContaminant_MSMSMap method, of class AnalyzeOutcomes.
     */
    @Test
    public void testGetContaminant_MSMSMap() throws Exception {
        System.out.println("getContaminant_MSMSMap");
        AnalyzeOutcomes instance = new AnalyzeOutcomesImpl();
        HashMap<String, HashSet<String>> expResult = null;
        HashMap<String, HashSet<String>> result = instance.getContaminant_MSMSMap();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
                        XilmassResult r = new XilmassResult(line, false, false);
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
        ArrayList<Outcome> result = instance.getValidatedPSMs(resOutcome, fdr);
        assertEquals(24, result.size());
        fdr = 0.05;
        result = instance.getValidatedPSMs(resOutcome, fdr);
        assertEquals(29, result.size());
        // update to calculate PLink-FDR
        resOutcome.get(26).setTarget_decoy("DD");
        instance.isPIT = false;
        result = instance.getValidatedPSMs(resOutcome, 0.05);
        assertEquals(30, result.size());

        // try if DD is found before finding any TD..
        resOutcome.get(25).setTarget_decoy("DD");
        result = instance.getValidatedPSMs(resOutcome, 0.05);
        assertEquals(24, result.size());

        // check how sorting works..
        // Analyze KOJAK results
        ArrayList<KojakResult> kojakRes = new ArrayList<KojakResult>();
        // start with Xilmass objects
        folder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\kojak_validated_testing");
        File database = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases/cam_plectin.fasta");
        for (File f : folder.listFiles()) {
            System.out.println(f.getName());
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("Kojak") && (!line.startsWith("Scan")) && !line.isEmpty()) {
                    // make sure that a PSM is not contaminant-derived
                    if (!line.contains("contaminant")) {
                        String[] sp = line.split("\t");
                        String scanNumber = sp[0],
                                peptide1 = sp[8],
                                protein1 = sp[10],
                                peptide2 = sp[11],
                                protein2 = sp[13];
                        double obsMass = Double.parseDouble(sp[1]),
                                psmMass = Double.parseDouble(sp[3]),
                                ppmErr = Double.parseDouble(sp[4]),
                                score = Double.parseDouble(sp[5]),
                                dScore = Double.parseDouble(sp[6]),
                                pepDiff = Double.parseDouble(sp[7]),
                                linkerMass = Double.parseDouble(sp[14]);
                        // So making sure that it is indeed a cross linked one..
                        if (!peptide1.equals("-") && !peptide2.equals("-")) {
                            int charge = Integer.parseInt(sp[2]),
                                    link1 = Integer.parseInt(sp[9]),
                                    link2 = Integer.parseInt(sp[12]);
                            String td = instance.getTargetDecoy(protein1, protein2);
                            // just keep cross linked pairs                    
                            if (link1 != -1 && link2 != -1) {
                                protein1 = protein1.split("\\|")[1];
                                protein2 = protein2.split("\\|")[1];
                                KojakResult kr = new KojakResult("Probe2_v_mc1_top15HCD-1.mgf", scanNumber, obsMass, charge, psmMass, ppmErr, score, dScore,
                                        pepDiff, peptide1, link1, protein1, peptide2, link2, protein2, linkerMass, null, database, td);
                                kojakRes.add(kr);

                            }
                        }
                    }
                }
            }
            assertEquals(8, kojakRes.size());

            Collections.sort(kojakRes, KojakResult.ScoreDSC);
            resOutcome = new ArrayList<Outcome>();
            for (KojakResult r : kojakRes) {
                resOutcome.add(r);
            }

            ArrayList<Outcome> validatedPSMs = instance.getValidatedPSMs(resOutcome, 0.05);
            assertEquals(0, validatedPSMs.size());

            resOutcome.get(0).setTarget_decoy("TT");
            resOutcome.get(1).setTarget_decoy("TT");
            validatedPSMs = instance.getValidatedPSMs(resOutcome, 0.05);
            assertEquals(2, validatedPSMs.size());

            kojakRes.addAll(kojakRes);
            Collections.sort(kojakRes, KojakResult.ScoreDSC);
            assertEquals(16, kojakRes.size());
            resOutcome = new ArrayList<Outcome>();
            for (KojakResult r : kojakRes) {
                resOutcome.add(r);
                System.out.println(r.toPrint());
            }
            validatedPSMs = instance.getValidatedPSMs(resOutcome, 0.05);
            assertEquals(4, validatedPSMs.size());
            
            File plinkFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\plink/all");
            
            

        }
    }

}
