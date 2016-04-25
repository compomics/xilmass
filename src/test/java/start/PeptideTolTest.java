/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

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
public class PeptideTolTest {

    public PeptideTolTest() {
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
     * Test of getUpper_limit method, of class PeptideTol.
     */
    @Test
    public void testGetUpper_limit() {
        System.out.println("getUpper_limit");
        PeptideTol instance = new PeptideTol(true, 20, 1.00782503, "peptide_tol1");
        double result = instance.getUpper_limit();
        assertEquals(1.02, result, 0.01);

        PeptideTol instance2 = new PeptideTol(true, 40, 1.00782503, "peptide_tol1");
        result = instance2.getUpper_limit();
        assertEquals(1.047, result, 0.01);

        PeptideTol instance3 = new PeptideTol(false, 20, 1.00782503, "peptide_tol1");
        result = instance3.getUpper_limit();
        assertEquals(21.00, result, 0.01);
    }

    /**
     * Test of getLower_limit method, of class PeptideTol.
     */
    @Test
    public void testGetLower_limit() {
        System.out.println("getLower_limit");
        PeptideTol instance = new PeptideTol(true, 20, 1.00782503, "peptide_tol1");
        double result = instance.getLower_limit();
        assertEquals(0.987, result, 0.01);

        PeptideTol instance2 = new PeptideTol(true, 40, 1.00782503, "peptide_tol1");
        result = instance2.getLower_limit();
        assertEquals(0.967, result, 0.01);

        PeptideTol instance3 = new PeptideTol(false, 20, 1.00782503, "peptide_tol1");
        result = instance3.getLower_limit();
        assertEquals(-19.00, result, 0.01);
    }
}
