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
import static start.Start.check;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;

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
     * Test of main method, of class Start.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        Start.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isSameDBSetting method, of class Start.
     */
    @Test
    public void testIsSameDBSetting() throws Exception {
        System.out.println("isSameDBSetting");
        File paramFile = null;
        boolean expResult = false;
        boolean result = Start.isSameDBSetting(paramFile);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInfo method, of class Start.
     */
    @Test
    public void testGetInfo() {
        System.out.println("getInfo");
        CPeptides cPeptide = null;
        StringBuilder expResult = null;
        StringBuilder result = Start.getInfo(cPeptide);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of check method, of class Start.
     */
    @Test
    public void testCheck() {
        System.out.println("check");
        ArrayList<String> tmps = new ArrayList<String>();
        tmps.add("doublyCharged_pepA_a1_lepB_monolink_a3_mz=129.0966");
        tmps.add("singlyCharged_pepA_b1_mz=129.1022");
        tmps.add("singlyCharged_pepA_y1_pepB_y1_mz=147.1128");
        tmps.add("doublyCharged_pepB_a3_lepA_monolink_a1_mz=242.6601");
        tmps.add("singlyCharged_pepA_a1_lepB_monolink_a3_mz=257.1859");
        tmps.add("doublyCharged_pepA_b1_lepB_b4_mz=368.2418");
        tmps.add("doublyCharged_pepA_y5_lepB_y4_mz=576.8655");

        int[] result = {0, 0};
        for (String tmp : tmps) {
            result = Start.check(tmp, result[0], result[1]);
        }
        assertEquals(6, result[0]);
        assertEquals(4, result[1]);

    }

}
