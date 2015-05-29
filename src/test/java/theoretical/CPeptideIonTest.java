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
     * Test of getDiff method, of class CPeptideIon.
     */
    @Test
    public void testGetDiff() {
        System.out.println("getDiff");
        CPeptideIon instance = null;
        double expResult = 0.0;
        double result = instance.getDiff();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDiff method, of class CPeptideIon.
     */
    @Test
    public void testSetDiff() {
        System.out.println("setDiff");
        double diff = 0.0;
        CPeptideIon instance = null;
        instance.setDiff(diff);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isFound method, of class CPeptideIon.
     */
    @Test
    public void testIsFound() {
        System.out.println("isFound");
        CPeptideIon instance = null;
        boolean expResult = false;
        boolean result = instance.isFound();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIsFound method, of class CPeptideIon.
     */
    @Test
    public void testSetIsFound() {
        System.out.println("setIsFound");
        boolean isFound = false;
        CPeptideIon instance = null;
        instance.setIsFound(isFound);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIdentification_charge method, of class CPeptideIon.
     */
    @Test
    public void testGetIdentification_charge() {
        System.out.println("getIdentification_charge");
        CPeptideIon instance = null;
        int expResult = 0;
        int result = instance.getIdentification_charge();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIdentification_charge method, of class CPeptideIon.
     */
    @Test
    public void testSetIdentification_charge() {
        System.out.println("setIdentification_charge");
        int identification_charge = 0;
        CPeptideIon instance = null;
        instance.setIdentification_charge(identification_charge);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMass method, of class CPeptideIon.
     */
    @Test
    public void testGetMass() {
        System.out.println("getMass");
        CPeptideIon instance = null;
        double expResult = 0.0;
        double result = instance.getMass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setMass method, of class CPeptideIon.
     */
    @Test
    public void testSetMass() {
        System.out.println("setMass");
        double mass = 0.0;
        CPeptideIon instance = null;
        instance.setMass(mass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getIntensity method, of class CPeptideIon.
     */
    @Test
    public void testGetIntensity() {
        System.out.println("getIntensity");
        CPeptideIon instance = null;
        double expResult = 0.0;
        double result = instance.getIntensity();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIntensity method, of class CPeptideIon.
     */
    @Test
    public void testSetIntensity() {
        System.out.println("setIntensity");
        double intensity = 0.0;
        CPeptideIon instance = null;
        instance.setIntensity(intensity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class CPeptideIon.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        CPeptideIon instance = null;
        CPeptideIonType expResult = null;
        CPeptideIonType result = instance.getType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setType method, of class CPeptideIon.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        CPeptideIonType type = null;
        CPeptideIon instance = null;
        instance.setType(type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFragmentIonType method, of class CPeptideIon.
     */
    @Test
    public void testGetFragmentIonType() {
        System.out.println("getFragmentIonType");
        CPeptideIon instance = null;
        int expResult = 0;
        int result = instance.getFragmentIonType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFragmentIonType method, of class CPeptideIon.
     */
    @Test
    public void testSetFragmentIonType() {
        System.out.println("setFragmentIonType");
        int fragmentIonType = 0;
        CPeptideIon instance = null;
        instance.setFragmentIonType(fragmentIonType);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class CPeptideIon.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        CPeptideIon instance = null;
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setName method, of class CPeptideIon.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        CPeptideIon instance = null;
        instance.setName(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Test of hashCode method, of class CPeptideIon.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        CPeptideIon instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class CPeptideIon.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        CPeptideIon instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class CPeptideIon.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        CPeptideIon instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
