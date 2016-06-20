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
import java.util.Comparator;
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
    @Override
    public void setUp() throws Exception {
    }

    @After
    @Override
    public void tearDown() throws Exception {
    }

    /**
     * Test of getMatchedPeak method, of class MatchAndScore.
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
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
        // CPeptides must be already generated.. to prepare CPeptides object to score
        ArrayList<ModificationMatch> mods = new ArrayList<ModificationMatch>();
        String proteinA = "proA(9-20)",
                proteinB = "proB(5-10)";
        Peptide peptideA = new Peptide("peptideA", mods),
                peptideB = new Peptide("peptideB", mods);
        int linkerA = 0,
                linkerB = 0;
        DSS linker = new DSS();
        CPeptides c = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerA, linkerB, FragmentationMode.HCD, false);
        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        HashSet<CPeptideIon> theoCMS2ions = new HashSet<CPeptideIon>();
        CPeptideIon cpi_1 = new CPeptideIon(100, 129.56, CPeptideIonType.Backbone_PepA, 0, "1"); // singly charged peak is 130.56
        theoCMS2ions.add(cpi_1);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        ArrayList<Peak> result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(130.10452, result.get(0).mz, 0.01);

        // second problematic scenario
        theoCMS2ions = new HashSet<CPeptideIon>();
        theoCMS2ions.add(new CPeptideIon(100, 287.203, CPeptideIonType.Backbone_PepA, 0, "1"));
        theoCMS2ions.add(new CPeptideIon(100, 287.648, CPeptideIonType.Backbone_PepA, 0, "2"));

        instance = new MatchAndScore(second_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.getXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(288.19623, result.get(0).mz, 0.001);

        ArrayList<CPeptidePeak> resultCPs = new ArrayList<CPeptidePeak>(instance.getMatchedTheoreticalXLPeaks());
        for(CPeptidePeak p : resultCPs){
            System.out.println(p.toString());
        }
        assertEquals(2, resultCPs.size());
        assertEquals(288.21, resultCPs.get(0).getMz(), 0.01);

        // 4th problematic scenario -doublyCharged_pepA_a1_lepB_monolink_a2_mz=129.0966 singlyCharged_pepA_b1_mz=129.1022 
        theoCMS2ions = new HashSet<CPeptideIon>();
        double first = (129.0966 - ElementaryIon.proton.getTheoreticMass()),
                second = (129.1022 - ElementaryIon.proton.getTheoreticMass());
        theoCMS2ions.add(new CPeptideIon(100, first, CPeptideIonType.Backbone_PepA, 0, "1"));
        theoCMS2ions.add(new CPeptideIon(100, second, CPeptideIonType.Backbone_PepA, 0, "1"));

        instance = new MatchAndScore(fourth_problem_ms, ScoreName.AndromedaD, c, 0.01, 0, 1, 11, 100, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(129.10219, result.get(0).mz, 0.001);

        // 4th problematic scenario -doublyCharged_pepA_a1_lepB_monolink_a2_mz=129.0966 singlyCharged_pepA_b1_mz=129.1022 with higher fragment tolerance...
        instance = new MatchAndScore(fourth_problem_ms, ScoreName.AndromedaD, c, 0.5, 0, 1, 11, 100, false, false);
        instance.setDoesFindAllMatchedPeaks(false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(1, result.size());
        assertEquals(129.10219, result.get(0).mz, 0.001);
    }

    /**
     * Test of Problem, of class MatchAndScore.
     */
    public void testProblem() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("Problem");
        String expMGFFolder = "Data\\Test\\matching/";
        MSnSpectrum first_problem_ms = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("problem.mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    if (title.equals("problem_stupid_uniform_testing_mgf")) {
                        first_problem_ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    }
                }
            }
        }
        double fragTol = 0.5;
        // CPeptides must be already generated.. to prepare CPeptides object to score
        ArrayList<ModificationMatch> mods = new ArrayList<ModificationMatch>();
        // Fixed modification is Carbamidomethylation of C
        String proteinA = "proA(9-20)",
                proteinB = "proB(5-10)";
        Peptide peptideA = new Peptide("FAERIEEESDTDKMKR", mods),
                peptideB = new Peptide("EKGRMRFHK", mods);
        DSS linker = new DSS(true);
        CPeptides c = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, 14, 1, FragmentationMode.HCD, false);
        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 10, 100, true, true);

        ArrayList<CPeptidePeak> theoreticalCXPeaksAL = instance.getTheoreticalXPeaksAL();
        boolean exist = false;
        for (CPeptidePeak cc : theoreticalCXPeaksAL) {
            System.out.println(cc.getName());
            if (cc.getName().equals("By8Ay14_Ay13")) {
                exist = true;
            } else if (cc.getName().equals("Ay13_By8Ay14")) {
                exist = true;
            }
        }
        System.out.println("instance.getTheoreticalXLMS2ions()"+instance.getTheoreticalXLMS2ions().size());
        System.out.println("instance.getTheoreticalXPeaksAL()"+instance.getTheoreticalXPeaksAL().size());
        assertTrue(exist);
        assertEquals(90, instance.getTheoreticalXLMS2ions().size());
        assertEquals(180, instance.getTheoreticalXPeaksAL().size());
    }

    /**
     * Test of getMatchedPeak method, of class MatchAndScore.
     */
    public void testGetMatchedPeak() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("getMatchedPeak");
        String expMGF = "Data\\Test\\matching/uniform_match_testing.mgf",
                expMGFFolder = "Data\\Test\\matching/",
                title = "stupid_uniform_testing_mgf",
                title2 = "stupid_uniform_testing_mgf_2",
                title3 = "stupid_uniform_testing_mgf_3";
        MSnSpectrum ms = null,
                ms2 = null,
                ms3 = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("uniform_match_testing.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                ms2 = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                ms3 = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title3);
            }
        }

        // needs to prepare CPeptides object to score
        ArrayList<ModificationMatch> mods = new ArrayList<ModificationMatch>();
        String proteinA = "proA(9-20)",
                proteinB = "proB(5-10)";
        Peptide peptideA = new Peptide("peptideA", mods),
                peptideB = new Peptide("peptideB", mods);
        int linkerA = 0,
                linkerB = 0;
        DSS linker = new DSS();
        CPeptides c = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerA, linkerB, FragmentationMode.HCD, false);
        c.setTheoretical_xlinked_mass(1000);
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

        // if matched peak is found only once...
        double fragTol = 0.5;
        MatchAndScore instance = new MatchAndScore(ms, ScoreName.MSAmandaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        ArrayList<Peak> result = new ArrayList<Peak>(instance.getMatchedPeaks());
        Collections.sort(result, new Comparator<Peak>() {
            @Override
            public int compare(Peak o1, Peak o2) {
                return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
            }
        });

        assertEquals(5, result.size());
        // just printing to see matched peaks..
        for (Peak p : result) {
            System.out.println(p.mz);
        }

        assertEquals(100.25, result.get(0).mz);
        assertEquals(100.6, result.get(1).mz);
        assertEquals(101.4, result.get(2).mz);
        assertEquals(102.4, result.get(3).mz);
        assertEquals(103.7, result.get(4).mz);
        assertEquals(5, instance.getMatchedTheoreticalXLPeaks().size());

        System.out.println("PSMScore = " + instance.getXPSMScore());

        instance = new MatchAndScore(ms2, ScoreName.MSAmandaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();
        System.out.println("PSMScore = " + instance.getXPSMScore());

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(5, result.size());

        // now it allows finding all matched peaks, even finding the same peak more than once..
        instance = new MatchAndScore(ms, ScoreName.MSAmandaD, c, fragTol, 0, 1, 11, 100, true, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        Collections.sort(result, new Comparator<Peak>() {
            @Override
            public int compare(Peak o1, Peak o2) {
                return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
            }
        });

        assertEquals(10, result.size());
        //eventhough the number of the matched experimental peaks is 10, the number of the theoretical peak is 5 (cannot be bigger than what we have as a list)
        assertEquals(5, instance.getMatchedTheoreticalXLPeaks().size());

        // just printing to see matched peaks..
        for (Peak p : result) {
            System.out.println(p.mz);
        }

        theoCMS2ions = new HashSet<CPeptideIon>();
        cpi_1 = new CPeptideIon(100, 99.2, CPeptideIonType.Backbone_PepA, 0, "1");  //100.207276
        cpi_2 = new CPeptideIon(100, 99.7, CPeptideIonType.Backbone_PepA, 0, "2");  //100.707276
        cpi_3 = new CPeptideIon(100, 100.6, CPeptideIonType.Backbone_PepA, 0, "3"); //101.607276
        cpi_4 = new CPeptideIon(100, 101.6, CPeptideIonType.Backbone_PepA, 0, "4"); //102.607276
        cpi_5 = new CPeptideIon(100, 102.6, CPeptideIonType.Backbone_PepA, 0, "5"); //103.607276
        theoCMS2ions.add(cpi_1);
        theoCMS2ions.add(cpi_2);
        theoCMS2ions.add(cpi_3);
        theoCMS2ions.add(cpi_4);
        theoCMS2ions.add(cpi_5);

        fragTol = 0.1;
        instance = new MatchAndScore(ms3, ScoreName.MSAmandaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.setTheoreticalCXMS2ions(theoCMS2ions);
        instance.getXPSMScore();

        result = new ArrayList<Peak>(instance.getMatchedPeaks());
        assertEquals(2, result.size());

        Collections.sort(result, new Comparator<Peak>() {
            @Override
            public int compare(Peak o1, Peak o2) {
                return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
            }
        });
        assertEquals(100.25, result.get(0).mz);
        assertEquals(103.70, result.get(1).mz);
        assertEquals(2, instance.getMatchedPeaks().size());

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
     * Test of getTheoreticalXLPeaks method, of class MatchAndScore.
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
        Peptide p1 = new Peptide("EKK", modifications),
                p2 = new Peptide("AKEK", modifications);
        CrossLinker linker = new DSS();
        CPeptides c = new CPeptides("proA(1-25)", "proB(1-25)", p1, p2, linker, 1, 1, FragmentationMode.CID, false);

        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, fragTol, 0, 1, 11, 100, false, false);
        instance.getTheoreticalXLPeaks();
        instance.getXPSMScore();

        HashSet<CPeptidePeak> result = instance.getTheoreticalXLPeaks();
        assertEquals(38, result.size());
    }

    /**
     * Test of getWeightedExplainedIntensities method, of class MatchAndScore.
     */
    @Test
    public void testGetWeightedExplainedIntensities() {
        System.out.println("getWeightedExplainedIntensities");
        CPeptidePeak cp = new CPeptidePeak(304.18, 100, "name");
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
     * Test of calculateWeightForTheoPeaks method, of class MatchAndScore.
     */
    @Test
    public void testCalculateWeightForAndromeda() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("calculateWeightForAndromeda");
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

        HashSet<CPeptidePeak> tmps = new HashSet<CPeptidePeak>();
        tmps.add(new CPeptidePeak(1, 1, "Aa1linker_mz=129.0966", 'A', 2));
        tmps.add(new CPeptidePeak(2, 1, "Ab1_mz=129.1022", 'A', 1));
        tmps.add(new CPeptidePeak(3, 1, "Ay1By1_mz=147.1128", 'X', 1));
        tmps.add(new CPeptidePeak(4, 1, "Ba3linker_mz=242.6601", 'X', 2));
        tmps.add(new CPeptidePeak(5, 1, "Aa1linker_mz=257.1859", 'X', 1));
        tmps.add(new CPeptidePeak(6, 1, "Ab1Bb4_mz=368.2418", 'X', 2));
        tmps.add(new CPeptidePeak(7, 1, "Ay5By4_mz=576.8655", 'X', 2));

        // 6-FoundA and 4-FoundB
        ArrayList<CPeptidePeak> alls = new ArrayList<CPeptidePeak>();
        alls.add(new CPeptidePeak(1, 1, "Aa1linker_mz=129.0966",'X',2));
        alls.add(new CPeptidePeak(2, 1, "Ab1_mz=129.1022",'X',1));
        alls.add(new CPeptidePeak(3, 1, "Ay1By1_mz=147.1128",'X',1));
        alls.add(new CPeptidePeak(4, 1, "Ba3linker_mz=242.6601",'X',2));
        alls.add(new CPeptidePeak(5, 1, "Aa1linker_mz=257.1859",'X',1));
        alls.add(new CPeptidePeak(6, 1, "Ab1Bb4_mz=368.2418",'X',2));
        alls.add(new CPeptidePeak(7, 1, "Ay5By4_mz=576.8655",'X',2));

        alls.add(new CPeptidePeak(1, 1, "Aa2linker_mz=129.0966",'X',2));
        alls.add(new CPeptidePeak(2, 1, "Ab2_mz=129.1022",'X',1));
        alls.add(new CPeptidePeak(3, 1, "Ay2By2_mz=147.1128",'X',1));
        alls.add(new CPeptidePeak(4, 1, "Ba4linker_mz=242.6601",'X',2));
        alls.add(new CPeptidePeak(5, 1, "Aa5linker_mz=257.1859",'X',1));
        alls.add(new CPeptidePeak(6, 1, "Ab5Bb2_mz=368.2418",'X',2));
        alls.add(new CPeptidePeak(7, 1, "Ay7By6_mz=576.8655",'X',2));

        // 6-FoundA and 4-FoundB 12-AllA 8-AllB
        // 6/12*4/8= 0.25         
        double expResult = 0.25;
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        Peptide p1 = new Peptide("EKK", modifications),
                p2 = new Peptide("AKEK", modifications);
        CrossLinker linker = new DSS();
        CPeptides c = new CPeptides("proA(1-25)", "proB(1-25)", p1, p2, linker, 1, 1, FragmentationMode.CID, false);
        MatchAndScore instance = new MatchAndScore(first_problem_ms, ScoreName.AndromedaD, c, 0.05, 0, 1, 11, 100, false, false);

        double result = instance.calculateWeightForTheoPeaks(tmps, alls, true);
        assertEquals(expResult, result, 0.0);
    }

}
