/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.protein.Protein;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sule
 */
public class LinkedResidueTest {

    public LinkedResidueTest() {
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
     * Test of getSeqMass method, of class LinkedResidue.
     */
    @Test
    public void testGetSeqMass() {
        System.out.println("getSeqMass");
        Protein protein = new Protein("header", "VDKPSNETVTDWIDR");
        LinkedResidue instance = new LinkedResidue(protein, 0, LinkedResidueType.K, true, false);
        double expResult = 1773.8606;
        double result = instance.getSeqMass();
        assertEquals(expResult, result, 0.05);
    }

    /**
     * Test of getSeqLen method, of class LinkedResidue.
     */
    @Test
    public void testGetSeqLen() {
        System.out.println("getSeqLen");
        Protein protein = new Protein("header", "VDKPSNETVTDWIDR");
        LinkedResidue instance = new LinkedResidue(protein, 0, LinkedResidueType.K, true, false);
        int expResult = 15;
        int result = instance.getSeqLen();
        assertEquals(expResult, result);
    }

}
