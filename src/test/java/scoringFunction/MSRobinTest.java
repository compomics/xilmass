/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

import junit.framework.TestCase;

/**
 *
 * @author Sule
 */
public class MSRobinTest extends TestCase {

    public MSRobinTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getScore method, of class MSRobin.
     */
    public void testGetScore() {
        System.out.println("getScore");
        double p = 0.01,
                intensity = 220,
                explainedIntensity = 220;
        int N = 2,
                n = 2;
        MSRobin instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        double expResult = 40;
        double result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.02;
        intensity = 430;
        explainedIntensity = 351;
        N = 4;
        n = 3;
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        expResult = 40.66;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 1);
        expResult = 36.74;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        p = 0.03;
        intensity = 638;
        explainedIntensity = 351;
        N = 6;
        n = 3;
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        expResult = 24.45;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 1);
        expResult = 18.13;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        p = 0.04;
        intensity = 835;
        explainedIntensity = 351;
        N = 8;
        n = 3;
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        expResult = 16.28;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 1);
        expResult = 10.55;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        p = 0.05;
        intensity = 1026;
        explainedIntensity = 351;
        N = 10;
        n = 3;
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        expResult = 11.34;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);
        
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 1);
        expResult = 6.63;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);

        p = 0.1;
        intensity = 1841;
        explainedIntensity = 630;
        N = 20;
        n = 6;
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 0);
        expResult = 11.39;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);
        
        instance = new MSRobin(p, N, n, intensity, explainedIntensity, 1);
        expResult = 6.66;
        result = instance.getScore();
        assertEquals(expResult, result, 0.05);
    }

}
