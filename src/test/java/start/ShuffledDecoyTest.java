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
public class ShuffledDecoyTest {

    public ShuffledDecoyTest() {
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
     * Test of getTarget method, of class ShuffledDecoy.
     */
    @Test
    public void testGetTarget() {
        System.out.println("getTarget");
        ShuffledDecoy instance = new ShuffledDecoy("RGKD");
        String expResult = "";
        String result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        assertNotSame(expResult, result);
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        assertNotSame(expResult, result);
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

        instance = new ShuffledDecoy("RGKD");
        expResult = "";
        result = instance.getShuffled().toString();
        System.out.println(result);

    }

    /**
     * Test of setTarget method, of class ShuffledDecoy.
     */
    @Test
    public void testSetTarget() {
        System.out.println("setTarget");
        String target = "";
        ShuffledDecoy instance = null;
        instance.setTarget(target);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }



}
