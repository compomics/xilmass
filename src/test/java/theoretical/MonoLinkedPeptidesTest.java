/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.type.DSS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
public class MonoLinkedPeptidesTest {

    public MonoLinkedPeptidesTest() {
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
     * Test of getTheoretical_xlinked_mass method, of class MonoLinkedPeptides.
     */
    @Test
    public void testGetTheoretical_xlinked_mass() {
        System.out.println("getTheoretical_xlinked_mass");
        Peptide peptide = new Peptide("AKLMR", new ArrayList<ModificationMatch>());
        MonoLinkedPeptides instance = new MonoLinkedPeptides(peptide, "test(1-10)", 1, new DSS(), FragmentationMode.HCD_all);
        double expResult = 773.38;
        double result = instance.getTheoretical_xlinked_mass();
        assertEquals(expResult, result, 0.1);
    }

    /**
     * Test of getTheoretical_ions method, of class MonoLinkedPeptides.
     */
    @Test
    public void testGetTheoretical_ions() {
        System.out.println("getTheoretical_ions");
        Peptide peptide = new Peptide("AKLMR", new ArrayList<ModificationMatch>());
        MonoLinkedPeptides instance = new MonoLinkedPeptides(peptide, "test(1-10)", 1, new DSS(), FragmentationMode.CID);
        HashSet<CPeptideIon> result = instance.getTheoretical_ions();
        assertEquals(8, result.size());

        ArrayList<CPeptideIon> resultAL = new ArrayList<CPeptideIon>(result);

        Collections.sort(resultAL, CPeptideIon.Ion_ASC_mass_order);

        assertEquals(71.04, resultAL.get(0).getMass(), 0.05);  //b1
        assertEquals(174.11, resultAL.get(1).getMass(), 0.05); //y1
        assertEquals(305.16, resultAL.get(2).getMass(), 0.05); //y2
        assertEquals(355.20, resultAL.get(3).getMass(), 0.05); //b2
        assertEquals(418.24, resultAL.get(4).getMass(), 0.05); //y3
        assertEquals(468.29, resultAL.get(5).getMass(), 0.05); //b3
        assertEquals(599.33, resultAL.get(6).getMass(), 0.05); //b4
        assertEquals(702.4, resultAL.get(7).getMass(), 0.05);  //y4

    }

}
