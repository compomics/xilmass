/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package theoretical;

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
public class CPeptideIonTest {
    
    public CPeptideIonTest() {
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
     * Test of get_theoretical_mz method, of class CPeptideIon.
     */
    @Test
    public void testGet_theoretical_mz() {
        System.out.println("get_theoretical_mz");
        int chargeValue = 0;
        CPeptideIon instance = new CPeptideIon(10, 100, CPeptideIonType.Backbone_PepA, 1, "test");
        double expResult = 101.007;
        double result = instance.get_theoretical_mz(1);
        assertEquals(expResult, result, 0.001);
        
        expResult = 51.007;
        result = instance.get_theoretical_mz(2);
        assertEquals(expResult, result, 0.001);
    }

}
