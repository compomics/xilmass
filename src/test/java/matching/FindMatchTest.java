/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import junit.framework.TestCase;
import theoretical.CPeptideIon;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class FindMatchTest extends TestCase {

    public FindMatchTest(String testName) {
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
     * Test of getMatchedPeak method, of class FindMatch.
     */
    public void testGetMatchedPeak() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("getMatchedPeak");
        // Load an mgf file 

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
        ArrayList<CPeptideIon> theoCMS2ions = new ArrayList<CPeptideIon>();
        CPeptideIon cpi_1 = new CPeptideIon(100, 99.2),
                cpi_2 = new CPeptideIon(100, 99.7),
                cpi_3 = new CPeptideIon(100, 100.6),
                cpi_4 = new CPeptideIon(100, 101.6),
                cpi_5 = new CPeptideIon(100, 102.6);
        theoCMS2ions.add(cpi_1);
        theoCMS2ions.add(cpi_2);
        theoCMS2ions.add(cpi_3);
        theoCMS2ions.add(cpi_4);
        theoCMS2ions.add(cpi_5);

        FindMatch instance = new FindMatch(ms, theoCMS2ions, fragTol);
        ArrayList<Peak> result = instance.getMatchedPeak();

        assertEquals(100.25, result.get(0).mz);
        assertEquals(100.6, result.get(1).mz);
        assertEquals(101.4, result.get(2).mz);
        assertEquals(102.4, result.get(3).mz);
        assertEquals(103.7, result.get(4).mz);
        
        assertEquals(5, result.size());
        
        System.out.println(instance.getPSMscore());
    }

    /**
     * Test of findMatchedPeak method, of class FindMatch.
     */
//    public void testFindMatchedPeak() {
//        System.out.println("findMatchedPeak");
//        FindMatch instance = null;
//        instance.findMatchedPeak();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
