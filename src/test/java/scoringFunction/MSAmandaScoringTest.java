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
public class MSAmandaScoringTest extends TestCase {

    public MSAmandaScoringTest(String testName) {
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
     * Test of getScore method, of class MSAmandaScoring.
     */
    public void testGetScore() {
        System.out.println("getScore");
        double p = 0.01,
                intensity = 220,
                explainedIntensity = 220;
        int N = 2,
                n = 2;
        MSAmandaScoring instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        double expResult = 40;
        double result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.02;
        intensity = 430;
        explainedIntensity = 351;
        N = 4;
        n = 3;
        instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        expResult = 44.13;
        result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.03;
        intensity = 638;
        explainedIntensity = 351;
        N = 6;
        n = 3;
        instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        expResult = 30.37;
        result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.04;
        intensity = 835;
        explainedIntensity = 351;
        N = 8;
        n = 3;
        instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        expResult = 21.35;
        result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.05;
        intensity = 1026;
        explainedIntensity = 351;
        N = 10;
        n = 3;
        instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        expResult = 14.73;
        result = instance.getScore();
        assertEquals(expResult, result, 0.01);

        p = 0.1;
        intensity = 1841;
        explainedIntensity = 630;
        N = 20;
        n = 6;
        instance = new MSAmandaScoring(p, N, n, intensity, explainedIntensity);
        expResult = 14.83;
        result = instance.getScore();
        assertEquals(expResult, result, 0.01);
    }

}
