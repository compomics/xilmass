/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;

/**
 *
 * @author Sule
 */
public class CrossLinkedPeptidesTest {
    
    public CrossLinkedPeptidesTest() {
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
     * Test of getLinker method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetLinker() {
        System.out.println("getLinker");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        CrossLinker expResult = null;
        CrossLinker result = instance.getLinker();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentation_mode method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetFragmentation_mode() {
        System.out.println("getFragmentation_mode");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        FragmentationMode expResult = null;
        FragmentationMode result = instance.getFragmentation_mode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTheoretical_ions method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetTheoretical_ions() {
        System.out.println("getTheoretical_ions");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        HashSet<CPeptideIon> expResult = null;
        HashSet<CPeptideIon> result = instance.getTheoretical_ions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentFactory method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetFragmentFactory() {
        System.out.println("getFragmentFactory");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        IonFactory expResult = null;
        IonFactory result = instance.getFragmentFactory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIs_monoisotopic_mass method, of class CrossLinkedPeptides.
     */
    @Test
    public void testIsIs_monoisotopic_mass() {
        System.out.println("isIs_monoisotopic_mass");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        boolean expResult = false;
        boolean result = instance.isIs_monoisotopic_mass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIs_Branching method, of class CrossLinkedPeptides.
     */
    @Test
    public void testIsIs_Branching() {
        System.out.println("isIs_Branching");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        boolean expResult = false;
        boolean result = instance.isIs_Branching();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIsMassCalculated method, of class CrossLinkedPeptides.
     */
    @Test
    public void testIsIsMassCalculated() {
        System.out.println("isIsMassCalculated");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        boolean expResult = false;
        boolean result = instance.isIsMassCalculated();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntensity method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetIntensity() {
        System.out.println("getIntensity");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        double expResult = 0.0;
        double result = instance.getIntensity();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTheoretical_xlinked_mass method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetTheoretical_xlinked_mass() {
        System.out.println("getTheoretical_xlinked_mass");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        double expResult = 0.0;
        double result = instance.getTheoretical_xlinked_mass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinkingType method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetLinkingType() {
        System.out.println("getLinkingType");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        CrossLinkingType expResult = null;
        CrossLinkingType result = instance.getLinkingType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of prepareBackbone method, of class CrossLinkedPeptides.
     */
    @Test
    public void testPrepareBackbone() {
        System.out.println("prepareBackbone");
        HashMap<Integer, ArrayList<Ion>> product_ions = null;
        int ion_type = 0;
        int linked_index = 0;
        double mass_shift = 0.0;
        String pepName = "";
        CPeptideIonType cPepIonType = null;
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        HashSet<CPeptideIon> expResult = null;
        HashSet<CPeptideIon> result = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, pepName, cPepIonType);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toPrint method, of class CrossLinkedPeptides.
     */
    @Test
    public void testToPrint() {
        System.out.println("toPrint");
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        String expResult = "";
        String result = instance.toPrint();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModificationInfo method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetModificationInfo() {
        System.out.println("getModificationInfo");
        Peptide peptide = null;
        String expResult = "";
        String result = CrossLinkedPeptides.getModificationInfo(peptide);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSequenceWithPtms method, of class CrossLinkedPeptides.
     */
    @Test
    public void testGetSequenceWithPtms() throws XmlPullParserException, IOException {
        System.out.println("getSequenceWithPtms");    
        String peptideSequence = "MLCSDAIK";
        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("oxidation of m");
//        theoreticPTMs.add("propionamide c");
//        theoreticPTMs.add("pyro-cmc");
//        theoreticPTMs.add("oxidation of m");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);
        Peptide p = new Peptide(peptideSequence,result);
        
        CrossLinkedPeptides instance = new CrossLinkedPeptidesImpl();
        String expResult = "MLCSDAIK";
        String r = instance.getSequenceWithPtms(p, ptmFactory);
        assertEquals(expResult, r);
        
    }

    public class CrossLinkedPeptidesImpl extends CrossLinkedPeptides {

        public String toPrint() {
            return "";
        }
    }
    
}
