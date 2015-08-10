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
public class CalculateMS1ErrTest {

    public CalculateMS1ErrTest() {
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
     * Test of getMS1Err method, of class CalculateMS1Err.
     */
    @Test
    public void testGetMS1Err() {
        System.out.println("getMS1Err");
        boolean isPPM = true;
        double theoreticalMass = 1000.000000;
        double precursorMass = 999.999876;
        double expResult = -0.1240000000279906;
        double result = CalculateMS1Err.getMS1Err(isPPM, theoreticalMass, precursorMass);
        assertEquals(expResult, result, 0.001);

        theoreticalMass = 2515.349347;
        precursorMass = 2515.36694;
        expResult = 6.99;
        result = CalculateMS1Err.getMS1Err(isPPM, theoreticalMass, precursorMass);
        assertEquals(expResult, result, 0.1);
    }

}
