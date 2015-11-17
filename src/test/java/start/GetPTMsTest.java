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
public class GetPTMsTest {

    public GetPTMsTest() {
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
     * Test of getPTM method, of class GetPTMs.
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
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false);
        for (ModificationMatch acMatch : result) {
            assertEquals(theoreticPTM, acMatch.getTheoreticPtm());
            assertEquals(3, acMatch.getModificationSite());
            assertFalse(acMatch.isVariable());
        }
        assertEquals(1, result.size());
        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("MLcSDA", peptideObject.getSequenceWithLowerCasePtms());

    }

    @Test
    public void testGetPTMAlt() throws Exception {
        System.out.println("getPTMAlternative");
        String peptideSequence = "MLCSDAOP";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("carbamidomethyl c");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false);

        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLcSDAOP", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());

    }

    @Test
    public void testGetVariablePTMs() throws Exception {
        System.out.println("GetVariablePTMs");
        String peptideSequence = "MLCSDAOP";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("carbamidomethyl c");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);

        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLcSDAOP", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());

        assertEquals("oxidation of m", result.get(0).getTheoreticPtm());
        assertEquals(1, result.get(0).getModificationSite());
        assertEquals("carbamidomethyl c", result.get(1).getTheoreticPtm());
        assertEquals(3, result.get(1).getModificationSite());

        assertTrue(result.get(0).isVariable());
        assertTrue(result.get(1).isVariable());

        assertEquals(2, result.size());

        // Now select another PTMs..
        theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("pyro-cmc");

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);
        
        peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLcSDAOP", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());

        assertEquals("oxidation of m", result.get(0).getTheoreticPtm());
        assertEquals(1, result.get(0).getModificationSite());
        assertEquals("pyro-cmc", result.get(1).getTheoreticPtm());
        assertEquals(3, result.get(1).getModificationSite());

        assertTrue(result.get(0).isVariable());
        assertTrue(result.get(1).isVariable());

        assertEquals(2, result.size());

    }

}
