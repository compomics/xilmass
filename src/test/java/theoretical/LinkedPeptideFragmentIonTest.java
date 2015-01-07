/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
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
     * Test of getAaLinkedPeptideStrs method, of class LinkedPeptideFragmentIon.
     */
    public void testGetAaLinkedPeptideStrs() {
        System.out.println("getAaLinkedPeptideStrs");
        LinkedPeptideFragmentIon instance = null;
        ArrayList<ArrayList<Character>> expResult = null;
        ArrayList<ArrayList<Character>> result = instance.getAaLinkedPeptideStrs();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAaLinkedPeptideStrs method, of class LinkedPeptideFragmentIon.
     */
    public void testSetAaLinkedPeptideStrs() {
        System.out.println("setAaLinkedPeptideStrs");
        ArrayList<ArrayList<Character>> aaLinkedPeptideStrs = null;
        LinkedPeptideFragmentIon instance = null;
        instance.setAaLinkedPeptideStrs(aaLinkedPeptideStrs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getcTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testGetcTerminiMasses() {
        System.out.println("getcTerminiMasses");
        int type = 0;
        LinkedPeptideFragmentIon instance = null;
        ArrayList<Double> expResult = null;
        ArrayList<Double> result = instance.getcTerminiMasses(type);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setcTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testSetcTerminiMasses() {
        System.out.println("setcTerminiMasses");
        ArrayList<Double> cTerminiMasses = null;
        LinkedPeptideFragmentIon instance = null;
        instance.setcTerminiMasses(cTerminiMasses);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getnTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testGetnTerminiMasses() {
        System.out.println("getnTerminiMasses");
        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();

        CPeptides o = new CPeptides(peptide_alpha, peptide_beta, linker, 3, 2, FragmentationMode.CID, 1);

        int type = 0;
        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2);
        ArrayList<Double> result = instance.getnTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 6);

        assertEquals(result.get(0), 266.1681 - 138.0681, 0.01);
        assertEquals(result.get(1), 379.2481 - 138.0681, 0.01);
        assertEquals(result.get(2), 450.2881 - 138.0681, 0.01);
        assertEquals(result.get(3), 380.2081 - 138.0681, 0.01);
        assertEquals(result.get(4), 493.2881 - 138.0681, 0.01);
        assertEquals(result.get(5), 564.3281 - 138.0681, 0.01);
        

        instance = new LinkedPeptideFragmentIon(peptide_alpha, 3);
        result = instance.getnTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 6);
        
        
    }

    /**
     * Test of setnTerminiMasses method, of class LinkedPeptideFragmentIon.
     */
    public void testSetnTerminiMasses() {
        System.out.println("setnTerminiMasses");
        ArrayList<Double> nTerminiMasses = null;
        LinkedPeptideFragmentIon instance = null;
        instance.setnTerminiMasses(nTerminiMasses);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAALinkedPeptide method, of class LinkedPeptideFragmentIon.
     */
    public void testGetAALinkedPeptide() {
        System.out.println("getAALinkedPeptide");
        LinkedPeptideFragmentIon instance = null;
        ArrayList<ArrayList<Character>> expResult = null;
        ArrayList<ArrayList<Character>> result = instance.getAALinkedPeptide();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
