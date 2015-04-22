/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 *
 * @author Sule
 */
public class LinkedPeptideFragmentIonTest extends TestCase {

    public LinkedPeptideFragmentIonTest(String testName) {
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
     * Test of getCTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testGetCTerminiMasses() {
        System.out.println("getCTerminiMasses");
        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);

        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2);
        ArrayList<Double> result = instance.getCTerminiMasses(PeptideFragmentIon.Y_ION);
        assertEquals(result.size(), 6);

        assertEquals(result.get(0), 128.09, 0.1); // Only K
        assertEquals(result.get(1), 241.17, 0.1); // IK
        assertEquals(result.get(2), 312.21, 0.1); // AIK
        assertEquals(result.get(3), (242.13 + 18), 0.1); // KN+c-termini
        assertEquals(result.get(4), (355.22 + 18), 0.1); // IKN+c-termini
        assertEquals(result.get(5), (426.26 + 18), 0.1); // AIKN+c-termini 

        instance = new LinkedPeptideFragmentIon(peptide_alpha, 3);
        result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 8);
    }

    /**
     * Test of getNTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testGetNTerminiMasses() {
        System.out.println("getnTerminiMasses");
        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);

        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2);
        ArrayList<Double> result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 6);

        assertEquals(result.get(0), 128.09, 0.1); // Only K
        assertEquals(result.get(1), 241.17, 0.1); // IK
        assertEquals(result.get(2), 312.21, 0.1); // AIK
        assertEquals(result.get(3), 242.13, 0.1); // KN
        assertEquals(result.get(4), 355.22, 0.1); // IKN
        assertEquals(result.get(5), 426.26, 0.1); // AIKN

        instance = new LinkedPeptideFragmentIon(peptide_alpha, 3);
        result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 8);
    }

    /**
     * Test of getAALinkedPeptide method, of class LinkedPeptideFragmentIon.
     */
    public void testGetAALinkedPeptide() {
        System.out.println("getAALinkedPeptide");

        System.out.println("getnTerminiMasses");
        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);

        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2);
        ArrayList<ArrayList<Character>> result = instance.getAALinkedPeptide();

        // Linked amino acids on the pepB!
        //K
        assertEquals("K", result.get(0).get(0).toString());
        //IK        
        assertEquals("I", result.get(1).get(0).toString());
        assertEquals("K", result.get(1).get(1).toString());

        //AIK
        assertEquals("A", result.get(2).get(0).toString());
        assertEquals("I", result.get(2).get(1).toString());
        assertEquals("K", result.get(2).get(2).toString());
        //KN
        assertEquals("K", result.get(3).get(0).toString());
        assertEquals("N", result.get(3).get(1).toString());
        //IKN
        assertEquals("I", result.get(4).get(0).toString());
        assertEquals("K", result.get(4).get(1).toString());
        assertEquals("N", result.get(4).get(2).toString());
        //AIKN
        assertEquals("A", result.get(5).get(0).toString());
        assertEquals("I", result.get(5).get(1).toString());
        assertEquals("K", result.get(5).get(2).toString());
        assertEquals("N", result.get(5).get(3).toString());
    }

}
