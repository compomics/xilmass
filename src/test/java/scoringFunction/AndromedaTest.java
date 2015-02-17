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
public class AndromedaTest extends TestCase {

    public AndromedaTest(String testName) {
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
     * Test of calculateScore method, of class Andromeda.
     */
    public void testCalculateScore() {
        System.out.println("calculateScore");
        int N = 24;
        Andromeda instance = new Andromeda(0.01, N, 2);
        double score = instance.getScore();
        assertEquals(16.2, score,0.1);

        instance = new Andromeda(0.02, N, 2);
        score = instance.getScore();
        assertEquals(10.8, score, 0.1);

    }

}
