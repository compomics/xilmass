/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import scoringFunction.ScoreName;
import theoretical.CPeptideIon;
import theoretical.CPeptideIonType;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.CrossLinkedPeptides;
import theoretical.FragmentationMode;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class MatchAndScoreTest extends TestCase {

    public MatchAndScoreTest(String testName) {
        super(testName);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getMatchedPeak method, of class MatchAndScore.
     */
    public void testGetMatchedPeakProblemLessAccurate() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("testGetMatchedPeakProblemLessAccurate-Only one peak...");
        String expMGF = "Data\\Test\\matching/problem.mgf",
                expMGFFolder = "Data\\Test\\matching/";
        MSnSpectrum first_problem_ms = null,
                second_problem_ms = null,
                third_problem_ms = null,
                fourth_problem_ms = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("problem.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    if (title.equals("problem_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        first_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("problem2_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        second_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("problem3_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        third_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("File2235 Spectrum1957 scans: 3770")) {
                        fourth_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                }
            }
        }

        double fragTol = 0.5;
        HashSet<CPeptideIon> theoCMS2ions = new HashSet<CPeptideIon>();
        CPeptideIon cpi_1 = new CPeptideIon(100, 129.56, CPeptideIonType.Backbone_PepA, 0, "1"); // singly charged peak is 130.56
        theoCMS2ions.add(cpi_1);

        CPeptides c = null;
        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getCXPSMScore();

        ArrayList<Peak> result = new ArrayList<Peak>(instance.getMatchedPeaks());

        assertEquals(1, result.size());
        assertEquals(130.10452, result.get(0).mz, 0.01);

        // second problematic scenario
        theoCMS2ions = new HashSet<CPeptideIon>();
        theoCMS2ions.add(new CPeptideIon(100, 287.203, CPeptideIonType.Backbone_PepA, 0, "1"));
        theoCMS2ions.add(new CPeptideIon(100, 287.648, CPeptideIonType.Backbone_PepA, 0, "2"));

        instance = new MatchAndScore(second_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.getCXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(288.19623, result.get(0).mz, 0.001);

        ArrayList<CPeptidePeak> resultCPs = new ArrayList<CPeptidePeak>(instance.getMatchedTheoreticalCPeaks());
        assertEquals(1, resultCPs.size());
        assertEquals(288.21, resultCPs.get(0).getMz(), 0.01);

        // 4th problematic scenario -doublyCharged_pepA_a1_lepB_monolink_a2_mz=129.0966 singlyCharged_pepA_b1_mz=129.1022 
        theoCMS2ions = new HashSet<CPeptideIon>();
        double first = (129.0966 - ElementaryIon.proton.getTheoreticMass()),
                second = (129.1022 - ElementaryIon.proton.getTheoreticMass());
        theoCMS2ions.add(new CPeptideIon(100, first, CPeptideIonType.Backbone_PepA, 0, "1"));
        theoCMS2ions.add(new CPeptideIon(100, second, CPeptideIonType.Backbone_PepA, 0, "1"));

        instance = new MatchAndScore(fourth_problem_ms, ScoreName.AndromedaD, c, 0.01, 0, 1, 11, 100, false, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getCXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(129.10219, result.get(0).mz, 0.001);

        // 4th problematic scenario -doublyCharged_pepA_a1_lepB_monolink_a2_mz=129.0966 singlyCharged_pepA_b1_mz=129.1022 with higher fragment tolerance...
        instance = new MatchAndScore(fourth_problem_ms, ScoreName.AndromedaD, c, 0.5, 0, 1, 11, 100, false, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getCXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(129.10219, result.get(0).mz, 0.001);
    }

    /**
     * Test of getMatchedPeak method, of class MatchAndScore.
     */
    public void testGetMatchedPeak() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("getMatchedPeak");
        String expMGF = "Data\\Test\\matching/uniform_match_testing.mgf",
                expMGFFolder = "Data\\Test\\matching/",
                title = "stupid_uniform_testing_mgf";
        File expMGFFile = new File(expMGF);
        MSnSpectrum ms = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("uniform_match_testing.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    System.out.println(title2);
                    ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            }
        }

        double fragTol = 0.5;
        HashSet<CPeptideIon> theoCMS2ions = new HashSet<CPeptideIon>();
        CPeptideIon cpi_1 = new CPeptideIon(100, 99.2, CPeptideIonType.Backbone_PepA, 0, "1"),
                cpi_2 = new CPeptideIon(100, 99.7, CPeptideIonType.Backbone_PepA, 0, "2"),
                cpi_3 = new CPeptideIon(100, 100.6, CPeptideIonType.Backbone_PepA, 0, "3"),
                cpi_4 = new CPeptideIon(100, 101.6, CPeptideIonType.Backbone_PepA, 0, "4"),
                cpi_5 = new CPeptideIon(100, 102.6, CPeptideIonType.Backbone_PepA, 0, "5");
        theoCMS2ions.add(cpi_1);
        theoCMS2ions.add(cpi_2);
        theoCMS2ions.add(cpi_3);
        theoCMS2ions.add(cpi_4);
        theoCMS2ions.add(cpi_5);

        CPeptides c = null;
        MatchAndScore instance = new MatchAndScore(ms, ScoreName.MSAmandaD, c, fragTol, 0, 1, 11, 100, false, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);

        ArrayList<Peak> result = new ArrayList<Peak>(instance.getMatchedPeaks());
        Collections.sort(result, Peak.ASC_mz_order);

        assertEquals(5, result.size());

        for (Peak p : result) {
            System.out.println(p.mz);
        }

        assertEquals(100.25, result.get(0).mz);
        assertEquals(100.6, result.get(1).mz);
        assertEquals(101.4, result.get(2).mz);
        assertEquals(102.4, result.get(3).mz);
        assertEquals(103.7, result.get(4).mz);

        System.out.println("PSMScore = " + instance.getCXPSMScore());

        assertEquals(5, instance.getMatchedTheoreticalCPeaks().size());
    }

    /**
     * Test of getExpMS2 method, of class MatchAndScore.
     */
    @Test
    public void testGetExpMS2() {
        System.out.println("getExpMS2");
        MatchAndScore instance = null;
        MSnSpectrum expResult = null;
        MSnSpectrum result = instance.getExpMS2();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTheoreticalCXMS2ions method, of class MatchAndScore.
     */
    @Test
    public void testGetTheoreticalCXMS2ions() {
        System.out.println("getTheoreticalCXMS2ions");
        MatchAndScore instance = null;
        HashSet<CPeptideIon> expResult = null;
        HashSet<CPeptideIon> result = instance.getTheoreticalCXMS2ions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCPeptides method, of class MatchAndScore.
     */
    @Test
    public void testGetCPeptides() {
        System.out.println("getCPeptides");
        MatchAndScore instance = null;
        CrossLinkedPeptides expResult = null;
        CrossLinkedPeptides result = instance.getCPeptides();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTheoreticalCXPeaksAL method, of class MatchAndScore.
     */
    @Test
    public void testGetTheoreticalCXPeaksAL() {
        System.out.println("getTheoreticalCXPeaksAL");
        MatchAndScore instance = null;
        ArrayList<CPeptidePeak> expResult = null;
        ArrayList<CPeptidePeak> result = instance.getTheoreticalCXPeaksAL();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExpMS2 method, of class MatchAndScore.
     */
    @Test
    public void testSetExpMS2() {
        System.out.println("setExpMS2");
        MSnSpectrum expMS2 = null;
        MatchAndScore instance = null;
        instance.setExpMS2(expMS2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTheoreticalCXMS2ions method, of class MatchAndScore.
     */
    @Test
    public void testSetTheoreticalCXMS2ions() {
        System.out.println("setTheoreticalCXMS2ions");
        HashSet<CPeptideIon> theoreticalCXMS2ions = null;
        MatchAndScore instance = null;
        instance.setTheoreticalCXMS2ions(theoreticalCXMS2ions);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCPeptides method, of class MatchAndScore.
     */
    @Test
    public void testSetCPeptides() {
        System.out.println("setCPeptides");
        CPeptides cPeptides = null;
        MatchAndScore instance = null;
        instance.setCPeptides(cPeptides);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPrecursorCharge method, of class MatchAndScore.
     */
    @Test
    public void testGetPrecursorCharge() {
        System.out.println("getPrecursorCharge");
        MatchAndScore instance = null;
        int expResult = 0;
        int result = instance.getPrecursorCharge();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragTol method, of class MatchAndScore.
     */
    @Test
    public void testGetFragTol() {
        System.out.println("getFragTol");
        MatchAndScore instance = null;
        double expResult = 0.0;
        double result = instance.getFragTol();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoreName method, of class MatchAndScore.
     */
    @Test
    public void testGetScoreName() {
        System.out.println("getScoreName");
        MatchAndScore instance = null;
        ScoreName expResult = null;
        ScoreName result = instance.getScoreName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCXPSMScore method, of class MatchAndScore.
     */
    @Test
    public void testGetCXPSMScore() {
        System.out.println("getCXPSMScore");
        MatchAndScore instance = null;
        double expResult = 0.0;
        double result = instance.getCXPSMScore();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMatchedPeaks method, of class MatchAndScore.
     */
    @Test
    public void testGetMatchedPeaks() {
        System.out.println("getMatchedPeaks");
        MatchAndScore instance = null;
        HashSet<Peak> expResult = null;
        HashSet<Peak> result = instance.getMatchedPeaks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMatchedTheoreticalCPeaks method, of class MatchAndScore.
     */
    @Test
    public void testGetMatchedTheoreticalCPeaks() {
        System.out.println("getMatchedTheoreticalCPeaks");
        MatchAndScore instance = null;
        HashSet<CPeptidePeak> expResult = null;
        HashSet<CPeptidePeak> result = instance.getMatchedTheoreticalCPeaks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMatchedTheoreticalCPeaks method, of class MatchAndScore.
     */
    @Test
    public void testSetMatchedTheoreticalCPeaks() {
        System.out.println("setMatchedTheoreticalCPeaks");
        HashSet<CPeptidePeak> matchedTheoPeaks = null;
        MatchAndScore instance = null;
        instance.setMatchedTheoreticalCPeaks(matchedTheoPeaks);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntensities method, of class MatchAndScore.
     */
    @Test
    public void testGetIntensities() {
        System.out.println("getIntensities");
        ArrayList<Peak> filteredPeaks = new ArrayList<Peak>();
        filteredPeaks.add(new Peak(10, 10));
        filteredPeaks.add(new Peak(11, 20));
        filteredPeaks.add(new Peak(13, 70));
        filteredPeaks.add(new Peak(12, 100));

        MatchAndScore instance = null;
        double expResult = 200;
        double result = instance.getIntensities(filteredPeaks);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getTheoreticalCXPeaks method, of class MatchAndScore.
     */
    @Test
    public void testGetTheoreticalCXPeaks() throws ClassNotFoundException, IOException, MzMLUnmarshallerException {
        System.out.println("getTheoreticalCXPeaks");
        String expMGF = "Data\\Test\\matching/problem.mgf",
                expMGFFolder = "Data\\Test\\matching/";
        MSnSpectrum first_problem_ms = null,
                second_problem_ms = null,
                third_problem_ms = null,
                fourth_problem_ms = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("problem.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    if (title.equals("problem_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        first_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("problem2_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        second_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("problem3_stupid_uniform_testing_mgf")) {
                        System.out.println(title);
                        third_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                    if (title.equals("File2235 Spectrum1957 scans: 3770")) {
                        fourth_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                }
            }
        }

        double fragTol = 0.5;
        HashSet<CPeptideIon> theoCMS2ions = new HashSet<CPeptideIon>();
        CPeptideIon cpi_1 = new CPeptideIon(100, 129.56, CPeptideIonType.Backbone_PepA, 0, "1"); // singly charged peak is 130.56
        theoCMS2ions.add(cpi_1);

        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        Peptide p1 = new Peptide("EAFSLFDKDGDGTITTK", modifications),
                p2 = new Peptide("AKELLEK", modifications);
        CrossLinker linker = new DSS();
        CPeptides c = new CPeptides("proA", "proB", p1, p2, linker, 7, 1, FragmentationMode.HCD_all, false, false);

        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.getTheoreticalCXPeaks();
        instance.getCXPSMScore();

        HashSet<CPeptidePeak> expResult = null;
        HashSet<CPeptidePeak> result = instance.getTheoreticalCXPeaks();
        assertEquals(expResult, result);
    }

    /**
     * Test of getExplainedIntensities method, of class MatchAndScore.
     */
    @Test
    public void testGetExplainedIntensities() {
        System.out.println("getExplainedIntensities");
        HashSet<Peak> matchedPeaks = null;
        double expResult = 0.0;
        double result = MatchAndScore.getExplainedIntensities(matchedPeaks);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fillForClosestPeak method, of class MatchAndScore.
     */
    @Test
    public void testFill_doesFindMatchedPeaksLessPrecise() {
        System.out.println("fill_doesFindMatchedPeaksLessPrecise");
        HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak = null;
        MatchAndScore instance = null;
        instance.fillForClosestPeak(peak_and_matchedPeak);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fillForClosestPeak method, of class MatchAndScore.
     */
    @Test
    public void testFillForClosestPeak() {
        System.out.println("fillForClosestPeak");
        HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak = null;
        MatchAndScore instance = null;
        instance.fillForClosestPeak(peak_and_matchedPeak);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWeightedExplainedIntensities method, of class MatchAndScore.
     */
    @Test
    public void testGetWeightedExplainedIntensities() {
        System.out.println("getWeightedExplainedIntensities");
        CPeptidePeak cp = new CPeptidePeak(304.18, 100, 1, "name");
        MatchedPeak m1 = new MatchedPeak(new Peak(304.16177, 100), null, 0.2),
                m2 = new MatchedPeak(new Peak(304.19131, 120), null, 0.3);
        ArrayList<MatchedPeak> ms = new ArrayList<MatchedPeak>();
        ms.add(m1);
        ms.add(m2);
        HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks = new HashMap<CPeptidePeak, ArrayList<MatchedPeak>>();
        matched_theoretical_and_matched_peaks.put(cp, ms);
        MatchAndScore instance = null;
        double expResult = 54;
        double result = instance.getWeightedExplainedIntensities(matched_theoretical_and_matched_peaks, 0.5);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of isDoesFindAllMatchedPeaks method, of class MatchAndScore.
     */
    @Test
    public void testIsDoesFindAllMatchedPeaks() {
        System.out.println("isDoesFindAllMatchedPeaks");
        MatchAndScore instance = null;
        boolean expResult = false;
        boolean result = instance.isDoesFindAllMatchedPeaks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDoesFindAllMatchedPeaks method, of class MatchAndScore.
     */
    @Test
    public void testSetDoesFindAllMatchedPeaks() {
        System.out.println("setDoesFindAllMatchedPeaks");
        boolean doesFindAllMatchedPeaks = false;
        MatchAndScore instance = null;
        instance.setDoesFindAllMatchedPeaks(doesFindAllMatchedPeaks);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fillForAllFoundPeaks method, of class MatchAndScore.
     */
    @Test
    public void testFillForAllFoundPeaks() {
        System.out.println("fillForAllFoundPeaks");
        HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks = null;
        MatchAndScore instance = null;
        instance.fillForAllFoundPeaks(matched_theoretical_and_matched_peaks);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateWeightForTheoPeaks method, of class MatchAndScore.
     */
    @Test
    public void testCalculateWeightForAndromeda() {
        System.out.println("calculateWeightForAndromeda");
        HashSet<CPeptidePeak> tmps = new HashSet<CPeptidePeak>();
        tmps.add(new CPeptidePeak(1, 1, 1, "doublyCharged_pepA_a1_lepB_monolink_a3_mz=129.0966"));
        tmps.add(new CPeptidePeak(2, 1, 1, "singlyCharged_pepA_b1_mz=129.1022"));
        tmps.add(new CPeptidePeak(3, 1, 1, "singlyCharged_pepA_y1_pepB_y1_mz=147.1128"));
        tmps.add(new CPeptidePeak(4, 1, 1, "doublyCharged_pepB_a3_lepA_monolink_a1_mz=242.6601"));
        tmps.add(new CPeptidePeak(5, 1, 1, "singlyCharged_pepA_a1_lepB_monolink_a3_mz=257.1859"));
        tmps.add(new CPeptidePeak(6, 1, 1, "doublyCharged_pepA_b1_lepB_b4_mz=368.2418"));
        tmps.add(new CPeptidePeak(7, 1, 1, "doublyCharged_pepA_y5_lepB_y4_mz=576.8655"));

        // 6-FoundA and 4-FoundB
        ArrayList<CPeptidePeak> alls = new ArrayList<CPeptidePeak>();
        alls.add(new CPeptidePeak(1, 1, 1, "doublyCharged_pepA_a1_lepB_monolink_a3_mz=129.0966"));
        alls.add(new CPeptidePeak(2, 1, 1, "singlyCharged_pepA_b1_mz=129.1022"));
        alls.add(new CPeptidePeak(3, 1, 1, "singlyCharged_pepA_y1_pepB_y1_mz=147.1128"));
        alls.add(new CPeptidePeak(4, 1, 1, "doublyCharged_pepB_a3_lepA_monolink_a1_mz=242.6601"));
        alls.add(new CPeptidePeak(5, 1, 1, "singlyCharged_pepA_a1_lepB_monolink_a3_mz=257.1859"));
        alls.add(new CPeptidePeak(6, 1, 1, "doublyCharged_pepA_b1_lepB_b4_mz=368.2418"));
        alls.add(new CPeptidePeak(7, 1, 1, "doublyCharged_pepA_y5_lepB_y4_mz=576.8655"));

        alls.add(new CPeptidePeak(1, 1, 1, "doublyCharged_pepA_a2_lepB_monolink_a3_mz=129.0966"));
        alls.add(new CPeptidePeak(2, 1, 1, "singlyCharged_pepA_b2_mz=129.1022"));
        alls.add(new CPeptidePeak(3, 1, 1, "singlyCharged_pepA_y2_pepB_y2_mz=147.1128"));
        alls.add(new CPeptidePeak(4, 1, 1, "doublyCharged_pepB_a4_lepA_monolink_a2_mz=242.6601"));
        alls.add(new CPeptidePeak(5, 1, 1, "singlyCharged_pepA_a5_lepB_monolink_a2_mz=257.1859"));
        alls.add(new CPeptidePeak(6, 1, 1, "doublyCharged_pepA_b5_lepB_b2_mz=368.2418"));
        alls.add(new CPeptidePeak(7, 1, 1, "doublyCharged_pepA_y7_lepB_y6_mz=576.8655"));

        // 6-FoundA and 4-FoundB 12-AllA 8-AllB
        // 6/12*4/8= 0.25 = 
        double expResult = 0.25;
//        double result = MatchAndScore.calculateWeightForTheoPeaks(tmps, alls, true);
//        assertEquals(expResult, result, 0.0);
    }

}
