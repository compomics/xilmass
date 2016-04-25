/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.outcome;

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
public class PercolatorResultTest {

    public PercolatorResultTest() {
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
     * Test of checkPossibility method, of class PercolatorResult.
     */
    @Test
    public void testCheckPossibility() {
        System.out.println("checkPossibility");
        //-.DNLYLAVLRASEGK(1)--DNLYLAVLRASEGK(1).-

        String peptideA = "DNLYLAVLRASEGK",
                peptideB = "DNLYLAVLRASEGK";
        int linkA = 1,
                linkB = 1;
        boolean expResult = false;
        boolean result = PercolatorResult.checkPossibility(peptideA, linkA, peptideB, linkB, true);
        assertEquals(expResult, result);

        peptideA = "DKNLYLAVLRASEGK";
        peptideB = "DKNLYLAVLRASEGK";
        linkA = 1;
        linkB = 1;
        expResult = true;
        result = PercolatorResult.checkPossibility(peptideA, linkA, peptideB, linkB, true);
        assertEquals(expResult, result);

        peptideA = "DNLYLAVLRASEGK";
        peptideB = "DKNLYLAVLRASEGK";
        linkA = 1;
        linkB = 1;
        expResult = false;
        result = PercolatorResult.checkPossibility(peptideA, linkA, peptideB, linkB, true);
        assertEquals(expResult, result);
    }

    
}
