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
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\main\\resources/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        // Getting one fixed PTMs
        String carbamidomethylc = "carbamidomethyl c";
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add(carbamidomethylc);

        boolean containsProteinNTermini = true,
                containsProteinCTermini = true;
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, containsProteinNTermini, containsProteinCTermini);
        for (ModificationMatch acMatch : result) {
            assertEquals(carbamidomethylc, acMatch.getTheoreticPtm());
            assertEquals(3, acMatch.getModificationSite());
            assertFalse(acMatch.isVariable());
        }
        assertEquals(1, result.size());
        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("MLcSDA", peptideObject.getSequenceWithLowerCasePtms());

        // now check for aa at protein n-termini
        theoreticPTMs = new ArrayList<String>();
        String acetylationproteinntermini = "acetylation of protein n-term"; // modN

        theoreticPTMs.add(carbamidomethylc);
        theoreticPTMs.add(acetylationproteinntermini);
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, containsProteinNTermini, containsProteinCTermini);
        assertEquals(2, result.size());
        for (ModificationMatch acMatch : result) {
            assertFalse(acMatch.isVariable());
        }

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, containsProteinCTermini);
        assertEquals(1, result.size());
        for (ModificationMatch acMatch : result) {
            assertFalse(acMatch.isVariable());
        }

        String methylationpeptidecterm = "methylation of peptide c-term";// MODCP - peptide c-termini 
        theoreticPTMs.add(methylationpeptidecterm);
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, false);
        assertEquals(2, result.size());

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, true);
        assertEquals(3, result.size());

        String formpeptidenterm = "formylation of peptide n-term";//MODNP - peptide n-termini 
        theoreticPTMs.add(formpeptidenterm);
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, false);
        assertEquals(3, result.size());

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, true);
        assertEquals(4, result.size());

        String glucuronylationproteinnterm = "glucuronylation of protein n-term";//MODNAA - particular amino acid (G) on PROTEIN n-termini 

        theoreticPTMs.add(glucuronylationproteinnterm);
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, false);
        assertEquals(3, result.size());

        peptideSequence = "MLCSDAG";
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, false);
        assertEquals(4, result.size());

        peptideSequence = "GMLCSDAG";
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, false);
        assertEquals(5, result.size());

        String homoserine = "homoserine";// MODCPAA - particular amino acid (M) on PEPTIDE c-termini 
        theoreticPTMs.add(homoserine);
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, false);
        assertEquals(5, result.size());

        peptideSequence = "MGMLCSDAG";
        theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add(homoserine);

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, false);
        assertEquals(0, result.size());

        peptideSequence = "MGMLCSDAGM";
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, true, false);
        assertEquals(1, result.size());

        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, false);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetPTMAlt() throws Exception {
        System.out.println("getPTMAlternative");
        String peptideSequence = "MLCSDAOP";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\main\\resources/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("carbamidomethyl c");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, false, false, false);

        Peptide peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLcSDAOP", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());

    }

    @Test
    public void testGetVariablePTMs() throws Exception {
        System.out.println("GetVariablePTMs");
        String peptideSequence = "MLCSDAOP";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\src\\main\\resources/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
        theoreticPTMs.add("carbamidomethyl c");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true, false, false);

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
        theoreticPTMs.add("pyro-cmc"); //modnpaa 'C' must be at the beginning of the peptide 

        peptideSequence = "MLCSDAOPC";
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true, false, false);
        peptideObject = new Peptide(peptideSequence, result);
        assertEquals("mLCSDAOPC", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(1, result.size());
        assertEquals("oxidation of m", result.get(0).getTheoreticPtm());

        peptideSequence = "CMLCSDAOPC";
        result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true, false, false);
        peptideObject = new Peptide(peptideSequence, result);
        assertEquals("cmLCSDAOPC", peptideObject.getSequenceWithLowerCasePtms());
        assertEquals(2, result.size());
        assertEquals("oxidation of m", result.get(0).getTheoreticPtm());
        assertEquals("pyro-cmc", result.get(1).getTheoreticPtm());
        assertEquals(2, result.get(0).getModificationSite());
        assertEquals(1, result.get(1).getModificationSite());
        assertTrue(result.get(0).isVariable());
        assertTrue(result.get(1).isVariable());
    }

}
