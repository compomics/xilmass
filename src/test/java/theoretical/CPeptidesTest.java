/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author Sule
 */
public class CPeptidesTest extends TestCase {

    public CPeptidesTest(String testName) {
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
     * Test of getPeptide_alpha method, of class CPeptides.
     */
    public void testGetPeptide_alpha() {
        System.out.println("getPeptide_alpha");
        CPeptides instance = null;
        Peptide expResult = null;
        Peptide result = instance.getPeptide_alpha();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPeptide_alpha method, of class CPeptides.
     */
    public void testSetPeptide_alpha() {
        System.out.println("setPeptide_alpha");
        Peptide peptide_alpha = null;
        CPeptides instance = null;
        instance.setPeptide_alpha(peptide_alpha);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPeptide_beta method, of class CPeptides.
     */
    public void testGetPeptide_beta() {
        System.out.println("getPeptide_beta");
        CPeptides instance = null;
        Peptide expResult = null;
        Peptide result = instance.getPeptide_beta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPeptide_beta method, of class CPeptides.
     */
    public void testSetPeptide_beta() {
        System.out.println("setPeptide_beta");
        Peptide peptide_beta = null;
        CPeptides instance = null;
        instance.setPeptide_beta(peptide_beta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinker method, of class CPeptides.
     */
    public void testGetLinker() {
        System.out.println("getLinker");
        CPeptides instance = null;
        CrossLinker expResult = null;
        CrossLinker result = instance.getLinker();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLinker method, of class CPeptides.
     */
    public void testSetLinker() {
        System.out.println("setLinker");
        CrossLinker linker = null;
        CPeptides instance = null;
        instance.setLinker(linker);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinker_position_on_alpha method, of class CPeptides.
     */
    public void testGetLinker_position_on_alpha() {
        System.out.println("getLinker_position_on_alpha");
        CPeptides instance = null;
        int expResult = 0;
        int result = instance.getLinker_position_on_alpha();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLinker_position_on_alpha method, of class CPeptides.
     */
    public void testSetLinker_position_on_alpha() {
        System.out.println("setLinker_position_on_alpha");
        int linker_position_on_alpha = 0;
        CPeptides instance = null;
        instance.setLinker_position_on_alpha(linker_position_on_alpha);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinker_position_on_beta method, of class CPeptides.
     */
    public void testGetLinker_position_on_beta() {
        System.out.println("getLinker_position_on_beta");
        CPeptides instance = null;
        int expResult = 0;
        int result = instance.getLinker_position_on_beta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLinker_position_on_beta method, of class CPeptides.
     */
    public void testSetLinker_position_on_beta() {
        System.out.println("setLinker_position_on_beta");
        int linker_position_on_beta = 0;
        CPeptides instance = null;
        instance.setLinker_position_on_beta(linker_position_on_beta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentation_mode method, of class CPeptides.
     */
    public void testGetFragmentation_mode() {
        System.out.println("getFragmentation_mode");
        CPeptides instance = null;
        FragmentationMode expResult = null;
        FragmentationMode result = instance.getFragmentation_mode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFragmentation_mode method, of class CPeptides.
     */
    public void testSetFragmentation_mode() {
        System.out.println("setFragmentation_mode");
        FragmentationMode fragmentation_mode = null;
        CPeptides instance = null;
        instance.setFragmentation_mode(fragmentation_mode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragment_ion_charge method, of class CPeptides.
     */
    public void testGetFragment_ion_charge() {
        System.out.println("getFragment_ion_charge");
        CPeptides instance = null;
        int expResult = 0;
        int result = instance.getFragment_ion_charge();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFragment_ion_charge method, of class CPeptides.
     */
    public void testSetFragment_ion_charge() {
        System.out.println("setFragment_ion_charge");
        int fragment_ion_charge = 0;
        CPeptides instance = null;
        instance.setFragment_ion_charge(fragment_ion_charge);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTheoterical_ions method, of class CPeptides.
     */
    public void testGetTheoterical_ions() throws FileNotFoundException, IOException {
        System.out.println("getTheoterical_ions");
        CPeptides instance = null;

        String peptide_alpha_str = "MLSDA",
                peptide_beta_str = "AIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();

        CPeptides o = new CPeptides(peptide_alpha, peptide_beta, linker, 3, 2, FragmentationMode.CID, 1);
        ArrayList<CPeptideIon> result = o.getTheoterical_ions();
        
        System.out.println("Start");
        for(CPeptideIon i : result){
            System.out.println(i.getMass());
        }

        System.out.println("End");
        
        ArrayList<CPeptideIon> expResult = null;
        assertEquals(59, result.size());

        File test_theoSpec = new File("Data/Test/theoretical/test_MassTheoSpec.txt");
        BufferedReader br = new BufferedReader(new FileReader(test_theoSpec));
        String line = "";
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Mass")) {
                double tmp_mass = Double.parseDouble(line.split("\t")[0]);
                assertEquals(tmp_mass, result.get(count).getMass(), 0.05);
                
                count++;
            }
        }

    }

    /**
     * Test of setTheoterical_ions method, of class CPeptides.
     */
    public void testSetTheoterical_ions() {
        System.out.println("setTheoterical_ions");
        ArrayList<CPeptideIon> theoterical_ions = null;
        CPeptides instance = null;
        instance.setTheoterical_ions(theoterical_ions);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentFactory method, of class CPeptides.
     */
    public void testGetFragmentFactory() {
        System.out.println("getFragmentFactory");
        CPeptides instance = null;
        IonFactory expResult = null;
        IonFactory result = instance.getFragmentFactory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFragmentFactory method, of class CPeptides.
     */
    public void testSetFragmentFactory() {
        System.out.println("setFragmentFactory");
        IonFactory fragmentFactory = null;
        CPeptides instance = null;
        instance.setFragmentFactory(fragmentFactory);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIons_alpha_peptide method, of class CPeptides.
     */
    public void testGetIons_alpha_peptide() {
        System.out.println("getIons_alpha_peptide");
        CPeptides instance = null;
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> expResult = null;
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> result = instance.getIons_alpha_peptide();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIons_alpha_peptide method, of class CPeptides.
     */
    public void testSetIons_alpha_peptide() {
        System.out.println("setIons_alpha_peptide");
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> ions_alpha_peptide = null;
        CPeptides instance = null;
        instance.setIons_alpha_peptide(ions_alpha_peptide);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIons_beta_peptide method, of class CPeptides.
     */
    public void testGetIons_beta_peptide() {
        System.out.println("getIons_beta_peptide");
        CPeptides instance = null;
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> expResult = null;
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> result = instance.getIons_beta_peptide();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIons_beta_peptide method, of class CPeptides.
     */
    public void testSetIons_beta_peptide() {
        System.out.println("setIons_beta_peptide");
        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> ions_beta_peptide = null;
        CPeptides instance = null;
        instance.setIons_beta_peptide(ions_beta_peptide);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getC_termini_type method, of class CPeptides.
     */
    public void testGetC_termini_type() {
        System.out.println("getC_termini_type");
        CPeptides instance = null;
        int expResult = 0;
        int result = instance.getC_termini_type();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setC_termini_type method, of class CPeptides.
     */
    public void testSetC_termini_type() {
        System.out.println("setC_termini_type");
        int c_termini_type = 0;
        CPeptides instance = null;
        instance.setC_termini_type(c_termini_type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getN_termini_type method, of class CPeptides.
     */
    public void testGetN_termini_type() {
        System.out.println("getN_termini_type");
        CPeptides instance = null;
        int expResult = 0;
        int result = instance.getN_termini_type();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setN_termini_type method, of class CPeptides.
     */
    public void testSetN_termini_type() {
        System.out.println("setN_termini_type");
        int n_termini_type = 0;
        CPeptides instance = null;
        instance.setN_termini_type(n_termini_type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProduct_ions_alpha method, of class CPeptides.
     */
    public void testGetProduct_ions_alpha() {
        System.out.println("getProduct_ions_alpha");
        CPeptides instance = null;
        HashMap<Integer, ArrayList<Ion>> expResult = null;
        HashMap<Integer, ArrayList<Ion>> result = instance.getProduct_ions_alpha();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProduct_ions_alpha method, of class CPeptides.
     */
    public void testSetProduct_ions_alpha() {
        System.out.println("setProduct_ions_alpha");
        HashMap<Integer, ArrayList<Ion>> product_ions_alpha = null;
        CPeptides instance = null;
        instance.setProduct_ions_alpha(product_ions_alpha);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProduct_ions_beta method, of class CPeptides.
     */
    public void testGetProduct_ions_beta() {
        System.out.println("getProduct_ions_beta");
        CPeptides instance = null;
        HashMap<Integer, ArrayList<Ion>> expResult = null;
        HashMap<Integer, ArrayList<Ion>> result = instance.getProduct_ions_beta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProduct_ions_beta method, of class CPeptides.
     */
    public void testSetProduct_ions_beta() {
        System.out.println("setProduct_ions_beta");
        HashMap<Integer, ArrayList<Ion>> product_ions_beta = null;
        CPeptides instance = null;
        instance.setProduct_ions_beta(product_ions_beta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIs_monoisotopic_mass method, of class CPeptides.
     */
    public void testIsIs_monoisotopic_mass() {
        System.out.println("isIs_monoisotopic_mass");
        CPeptides instance = null;
        boolean expResult = false;
        boolean result = instance.isIs_monoisotopic_mass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIs_monoisotopic_mass method, of class CPeptides.
     */
    public void testSetIs_monoisotopic_mass() {
        System.out.println("setIs_monoisotopic_mass");
        boolean is_monoisotopic_mass = false;
        CPeptides instance = null;
        instance.setIs_monoisotopic_mass(is_monoisotopic_mass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntensity method, of class CPeptides.
     */
    public void testGetIntensity() {
        System.out.println("getIntensity");
        CPeptides instance = null;
        double expResult = 0.0;
        double result = instance.getIntensity();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIntensity method, of class CPeptides.
     */
    public void testSetIntensity() {
        System.out.println("setIntensity");
        double intensity = 0.0;
        CPeptides instance = null;
        instance.setIntensity(intensity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepare_theoretical_spectrum method, of class CPeptides.
     */
    public void testPrepare_theoretical_spectrum() {
        System.out.println("prepare_theoretical_spectrum");
        CPeptides instance = null;
        instance.prepare_theoretical_spectrum();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
