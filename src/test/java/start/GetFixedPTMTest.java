/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
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
public class GetFixedPTMTest {

    public GetFixedPTMTest() {
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
     * This simple getPTM method runs to check one example case.
     *
     * Test of getPTM method, of class GetFixedPTM.
     */
    @Test
    public void testGetPTM() throws Exception {
        System.out.println("getPTM");
        String peptideSequence = "MLCSDA";
        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        // Getting one fixed PTMs
        PTM ptmName = ptmFactory.getPTM("carbamidomethyl c");
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("carbamidomethyl c");
        String theoreticPTM = ptmName.getName();
        ArrayList<ModificationMatch> result = GetFixedPTM.getPTM(ptmFactory, theoreticPTMs, peptideSequence);
        for (ModificationMatch acMatch : result) {
            assertEquals(theoreticPTM, acMatch.getTheoreticPtm());
            assertEquals(3, acMatch.getModificationSite());
        }
        assertEquals(1, result.size());
        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("MLcSDA", peptideObject.getSequenceWithLowerCasePtms());

    }

    @Test
    public void testGetPTMAlt() throws Exception {
        System.out.println("getPTM");
        String peptideSequence = "MLCSDAOP";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("pyro-cmc");
        ArrayList<ModificationMatch> result = GetFixedPTM.getPTM(ptmFactory, theoreticPTMs, peptideSequence);
        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLcSDAOP", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());

    }

}
