/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import config.ConfigHolder;
import java.io.File;
import java.util.ArrayList;
import org.apache.tools.ant.ExitException;
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
        PeptideTol pepTol = new PeptideTol(isPPM, precTol, 0, "peptide_tol1");
        double[] result = Start.getRange(precMass, pepTol);
        assertEquals(90, result[0], 0.01);
        assertEquals(110, result[1], 0.01);

        isPPM = true;
        pepTol = new PeptideTol(isPPM, precTol, 0, "peptide_tol1");
        result = Start.getRange(precMass, pepTol);
        assertEquals(99.99, result[0], 0.01);
        assertEquals(100.01, result[1], 0.01);

        isPPM = true;
        pepTol = new PeptideTol(isPPM, 20, 0, "peptide_tol1");
        result = Start.getRange(200, pepTol);
        assertEquals(199.996, result[0], 0.01);
        assertEquals(200.004, result[1], 0.01);

        isPPM = true;
        pepTol = new PeptideTol(isPPM, 40, 0, "peptide_tol1");
        result = Start.getRange(200, pepTol);
        assertEquals(199.994, result[0], 0.01);
        assertEquals(200.004, result[1], 0.01);
    }

    /**
     * Test of getPepTols method, of class Start.
     */
    @Test
    public void testGetPepTols() {
        System.out.println("getPepTols");
        ConfigHolder instance = ConfigHolder.getInstance();
        ArrayList<PeptideTol> result = Start.getPepTols(instance);
        // currently used properties file..
        int pep_tol_total = instance.getInt("peptide_tol_total");
        assertEquals(4, pep_tol_total);

        assertEquals(-0.015, result.get(0).getLower_limit(), 0.001);
        assertEquals(0.015, result.get(0).getUpper_limit(), 0.001);
        assertTrue(result.get(0).isPPM());
        assertEquals("peptide_tol1", result.get(0).getPeptide_tol_name());

        assertEquals(0.985, result.get(1).getLower_limit(), 0.01);
        assertEquals(1.015, result.get(1).getUpper_limit(), 0.01);
        assertTrue(result.get(1).isPPM());
        assertEquals("peptide_tol2", result.get(1).getPeptide_tol_name());

        assertEquals(1.985, result.get(2).getLower_limit(), 0.01);
        assertEquals(2.030, result.get(2).getUpper_limit(), 0.01);
        assertTrue(result.get(2).isPPM());
        assertEquals("peptide_tol3", result.get(2).getPeptide_tol_name());

        assertEquals(3.0015, result.get(3).getLower_limit(), 0.01);
        assertEquals(3.0354, result.get(3).getUpper_limit(), 0.01);
        assertTrue(result.get(3).isPPM());
        assertEquals("peptide_tol4", result.get(3).getPeptide_tol_name());

    }
}
