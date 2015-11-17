/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import java.io.File;
import java.util.ArrayList;
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
public class StartTest {

    public StartTest() {
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
     * Test of getRange method, of class Start.
     */
    @Test
    public void testGetRange() {
        System.out.println("getRange");
        double precMass = 100;
        boolean isPPM = false;
        double precTol = 10;
        double[] expResult = {90, 110};
        double[] result = Start.getRange(precMass, precTol, isPPM);
        assertEquals(90, result[0], 0.01);
        assertEquals(110, result[1], 0.01);
        
        isPPM=true;
        result = Start.getRange(precMass, precTol, isPPM);
        assertEquals(99.99, result[0], 0.01);
        assertEquals(100.01, result[1], 0.01);
    }

}
