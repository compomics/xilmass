/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import junit.framework.TestCase;
import scoringFunction.ScoreName;
import theoretical.CPeptideIon;
import theoretical.CPeptideIonType;
import theoretical.CPeptides;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class MatchAndScoreTest extends TestCase {

    public MatchAndScoreTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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
        MatchAndScore instance = new MatchAndScore(ms, ScoreName.MSAmandaD, c, fragTol, 0,1,11,100);
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
     * Test of findMatchedPeak method, of class FindMatch.
     */
//    public void testFindMatchedPeak() {
//        System.out.println("findMatchedPeak");
//        MatchAndScore instance = null;
//        instance.findMatchedPeak();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
