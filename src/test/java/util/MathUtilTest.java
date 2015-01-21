/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import static junit.framework.Assert.*;
import org.junit.Test;

/**
 *
 * @author Sule
 */
public class MathUtilTest {

    /**
     * Test of calculateCombination method, of class CMathUtil.
     */
    @Test
    public void testCalculateCombination() throws Exception{
        System.out.println("calculateCombination");
        int n = 24;
        int r = 2;
        double expResult = 276;
        double result = CMathUtil.calculateCombination(n, r);
        assertEquals(expResult, result, 0.0);

        n = 50;
        r = 8;
        expResult = 536878650;
        result = CMathUtil.calculateCombination(n, r);
        assertEquals(expResult, result, 0.0);

        n = 80;
        r = 70;
        expResult = 1646492110120.0;
        result = CMathUtil.calculateCombination(n, r);
        assertEquals(expResult, result, 0.5);

        n = 20;
        r = 6;
        expResult = 38760.0;
        result = CMathUtil.calculateCombination(n, r);
        assertEquals(expResult, result, 0.5);
    }

    @Test(expected = Exception.class)
    public void testCalculateCombination_err() throws Exception {
        System.out.println("calculateCombination_err");
        // This one shows it will throw exception! 
        int r = 50;
        int n = 8;
        CMathUtil.calculateCombination(n, r);
    }

}
