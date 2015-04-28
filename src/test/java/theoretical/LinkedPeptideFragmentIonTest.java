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
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

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
    public void testGetCTerminiMasses_Branching() {
        System.out.println("getCTerminiMasses_Branching(");
        String peptide_alpha_str = "MLSDAK",
                peptide_beta_str = "AIKNK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);

        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2, false, 100, true);
        ArrayList<CPeptideIon> result = instance.getCTerminiMasses(PeptideFragmentIon.Y_ION);
        assertEquals(4, result.size());
        assertEquals(128.09, result.get(0).getMass(), 0.1);  // K  
        assertEquals(242.13, result.get(1).getMass(), 0.1);  // KN  
        assertEquals(388.23, result.get(2).getMass(), 0.1);  // KNK+C-termini 
        assertEquals(241.17, result.get(3).getMass(), 0.1);  // IK 

        instance = new LinkedPeptideFragmentIon(peptide_alpha, 3, true, 100, true);

        result = instance.getCTerminiMasses(PeptideFragmentIon.Y_ION);
        assertEquals(5, result.size());

        assertEquals(115.0269, result.get(0).getMass(), 0.1); // D 
        assertEquals(186.0641, result.get(1).getMass(), 0.1); // DA 
        assertEquals(332.159, result.get(2).getMass(), 0.1);  // DAK+C-termini 
        assertEquals(202.059, result.get(3).getMass(), 0.1);  // SD 
        assertEquals(315.143, result.get(4).getMass(), 0.1);  // LSD

//        instance = new LinkedPeptideFragmentIon(peptide_alpha, 3, true, 100, true);
//        result = instance.getCTerminiMasses(PeptideFragmentIon.Z_ION);
//        assertEquals(result.size(), 3);
//
//        assertEquals(result.get(0).getMass(), 115.0269, 0.1); // D with c-termini
//        assertEquals(result.get(1).getMass(), 202.059, 0.1);  // SD with c-termini 
//        assertEquals(result.get(2).getMass(), 315.143, 0.1);  // LSD with c-termini 
//        assertEquals(result.get(3).getMass(), 188.0641, 0.1); // DA with c-termini 
//        assertEquals(result.get(4).getMass(), 275.0961, 0.1); // SDA with c-termini 
//        assertEquals(result.get(5).getMass(), 388.1801, 0.1); // LSDA with c-termini 
    }

    public void testGetCTerminiMasses_Attaching() {
        System.out.println("getCTerminiMasses-Attaching");
        String peptideA_str = "MLSDAK",
                peptideB_str = "AIKNK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideA_str, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideB_str, parent_proteins_test, modifications_test);
        //(Peptide linkedPeptide, int linker_position_on_linkedPeptide, boolean isLinkedPepA, double intensity, boolean isBranchingApproach) 
        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptideB, 2, false, 100, false);
        ArrayList<CPeptideIon> result = instance.getCTerminiMasses(PeptideFragmentIon.Y_ION);
        assertEquals(result.size(), 4);

        assertEquals(result.get(0).getMass(), 146.11, 0.1);  // K-attached
        assertEquals(result.get(1).getMass(), 260.15, 0.1);  // N-attached
        assertEquals(result.get(2).getMass(), 388.25, 0.1);  // K-attached 
        assertEquals(result.get(3).getMass(), 501.33, 0.1);  // I-attached

        instance = new LinkedPeptideFragmentIon(peptideA, 3, true, 100, false);
        result = instance.getCTerminiMasses(PeptideFragmentIon.Y_ION);
        assertEquals(result.size(), 5);

        assertEquals(result.get(0).getMass(), 146.11, 0.1);  // K-attached
        assertEquals(result.get(1).getMass(), 217.15, 0.1);  // A-attached
        assertEquals(result.get(2).getMass(), 332.17, 0.1);  // D-attached 
        assertEquals(result.get(3).getMass(), 419.20, 0.1);  // S-attached
        assertEquals(result.get(4).getMass(), 532.29, 0.1);  // L-attached
    }

    public void testGetNTerminiMasses_Attaching() {
        System.out.println("getNTerminiMasses-Attaching");
        String peptideA_str = "MLSDAK",
                peptideB_str = "AIKNK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideA_str, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideB_str, parent_proteins_test, modifications_test);
        //(Peptide linkedPeptide, int linker_position_on_linkedPeptide, boolean isLinkedPepA, double intensity, boolean isBranchingApproach) 
        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptideB, 2, false, 100, false);
        ArrayList<CPeptideIon> result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 4);

        assertEquals(result.get(0).getMass(), 71.04, 0.1);   // A-attached
        assertEquals(result.get(1).getMass(), 184.13, 0.1);  // I-attached
        assertEquals(result.get(2).getMass(), 312.224, 0.1); // K-attached 
        assertEquals(result.get(3).getMass(), 426.26, 0.1);  // N-attached

        instance = new LinkedPeptideFragmentIon(peptideA, 3, false, 100, false);
        result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(result.size(), 5);

        assertEquals(result.get(0).getMass(), 131.04, 0.1);  // M-attached
        assertEquals(result.get(1).getMass(), 244.13, 0.1);  // L-attached
        assertEquals(result.get(2).getMass(), 331.16, 0.1);  // S-attached 
        assertEquals(result.get(3).getMass(), 446.19, 0.1);  // D-attached
        assertEquals(result.get(4).getMass(), 517.293, 0.1); // A-attached
    }

    public void testGetNTerminiMasses_Branching() {
        // TODO: Improve this testing with more options on the right...Also add CPeptideIon related features!!!
        System.out.println("getNTerminiMasses-Branching");
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        String peptideA_str = "MLSDAK",
                peptideB_str = "AIKNK";
        Peptide peptideA = new Peptide(peptideA_str, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideB_str, parent_proteins_test, modifications_test);
        //(Peptide linkedPeptide, int linker_position_on_linkedPeptide, boolean isLinkedPepA, double intensity, boolean isBranchingApproach) 
        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptideB, 2, false, 100, true);
        ArrayList<CPeptideIon> result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(4, result.size());

        assertEquals(128.09, result.get(0).getMass(), 0.1); // K-branching
        assertEquals(242.13, result.get(1).getMass(), 0.1); // KN-branching
        assertEquals(241.17, result.get(2).getMass(), 0.1); // KNK-ctermini-branching
        assertEquals(312.21, result.get(3).getMass(), 0.1); // N-branching

        instance = new LinkedPeptideFragmentIon(peptideA, 3, false, 100, true);
        result = instance.getNTerminiMasses(PeptideFragmentIon.B_ION);
        assertEquals(5, result.size());

        assertEquals(115.02, result.get(0).getMass(), 0.1);  // M-attached
        assertEquals(186.06, result.get(1).getMass(), 0.1);  // L-attached
        assertEquals(202.05, result.get(2).getMass(), 0.1);  // S-attached 
        assertEquals(315.14, result.get(3).getMass(), 0.1);  // D-attached
        assertEquals(446.18, result.get(4).getMass(), 0.1); // A-attached
    }

    @Test(expected = Exception.class)
    public void testGetTerminiMasses_err() throws Exception {
        // This one shows it will throw exception! 
        System.out.println("getCTerminiMasses_err-Exception control!");
        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);
        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2, true, 100, true);
        // X-Y or Z ions are needed to be in selection!!
        instance.getCTerminiMasses(PeptideFragmentIon.A_ION);
        instance.getNTerminiMasses(PeptideFragmentIon.X_ION);
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

        LinkedPeptideFragmentIon instance = new LinkedPeptideFragmentIon(peptide_beta, 2, false, 100, true);
        ArrayList<ArrayList<Character>> result = instance.getAALinkedPeptide(true);
        result.addAll(instance.getAALinkedPeptide(false));

        // Linked amino acids on the pepB!
        // N-TERMINI INCLUDING FRAGMENT IONS!
        //K - n termini
        assertEquals("K", result.get(0).get(0).toString());
        //IK  - n termini     
        assertEquals("I", result.get(1).get(0).toString());
        assertEquals("K", result.get(1).get(1).toString());
        //AIK - n termini
        assertEquals("A", result.get(2).get(0).toString());
        assertEquals("I", result.get(2).get(1).toString());
        assertEquals("K", result.get(2).get(2).toString());

        // C-TERMINI INCLUDING FRAGMENT IONS!
        //K - c termini
        assertEquals("K", result.get(3).get(0).toString());
        //IK - c termini
        assertEquals("I", result.get(4).get(0).toString());
        assertEquals("K", result.get(4).get(1).toString());
        //KN - c termini
        assertEquals("K", result.get(5).get(0).toString());
        assertEquals("N", result.get(5).get(1).toString());
        //IKN - c termini
        assertEquals("I", result.get(6).get(0).toString());
        assertEquals("K", result.get(6).get(1).toString());
        assertEquals("N", result.get(6).get(2).toString());
    }

}
