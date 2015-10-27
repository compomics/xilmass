/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.CXPSM.outcome;

import analyse.CXPSM.outcome.PersonComparator;
import analyse.CXPSM.outcome.KojakResult;
import java.util.Comparator;
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
public class PersonComparatorTest {
    
    public PersonComparatorTest() {
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
     * Test of values method, of class PersonComparator.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        PersonComparator[] expResult = null;
        PersonComparator[] result = PersonComparator.values();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of valueOf method, of class PersonComparator.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String name = "";
        PersonComparator expResult = null;
        PersonComparator result = PersonComparator.valueOf(name);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decending method, of class PersonComparator.
     */
    @Test
    public void testDecending() {
        System.out.println("decending");
        Comparator<KojakResult> other = null;
        Comparator<KojakResult> expResult = null;
        Comparator<KojakResult> result = PersonComparator.decending(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getComparator method, of class PersonComparator.
     */
    @Test
    public void testGetComparator() {
        System.out.println("getComparator");
        PersonComparator[] multipleOptions = null;
        Comparator<KojakResult> expResult = null;
        Comparator<KojakResult> result = PersonComparator.getComparator(multipleOptions);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
