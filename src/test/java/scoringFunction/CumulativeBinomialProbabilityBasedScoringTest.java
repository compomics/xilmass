/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

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
public class CumulativeBinomialProbabilityBasedScoringTest {

    public CumulativeBinomialProbabilityBasedScoringTest() {
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
     * Test of calculateScore method, of class
     * CumulativeBinomialProbabilityBasedScoring.
     */
    @Test
    public void testCalculateScore() {
        System.out.println("calculateScore");
        CumulativeBinomialProbabilityBasedScoring instance = new CumulativeBinomialProbabilityBasedScoringImpl();
        instance.calculateScore();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScore method, of class
     * CumulativeBinomialProbabilityBasedScoring.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        CumulativeBinomialProbabilityBasedScoring instance = new CumulativeBinomialProbabilityBasedScoringImpl();
        double expResult = 0.0;
        double result = instance.getScore();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateCumulativeBinominalProbability method, of class
     * CumulativeBinomialProbabilityBasedScoring.
     */
    @Test
    public void testCalculateCumulativeBinominalProbability() throws Exception {
        System.out.println("calculateCumulativeBinominalProbability");
        Andromeda_derived instance = new Andromeda_derived(0.2, 10, 2);
        double expResult = 0.62419036;
        double result = instance.calculateCumulativeBinominalProbability();
        assertEquals(expResult, result, 0.0002);

        instance = new Andromeda_derived(0.2, 10, 0);
        expResult = 1.0;
        result = instance.calculateCumulativeBinominalProbability();
        assertEquals(expResult, result, 0.0002);

        instance = new Andromeda_derived(0.02, 200, 0);
        expResult = 1.0;
        result = instance.calculateCumulativeBinominalProbability();
        assertEquals(expResult, result, 0.0002);

        instance = new Andromeda_derived(0.02, 200, 5);
        expResult = 0.37115642;
        result = instance.calculateCumulativeBinominalProbability();
        assertEquals(expResult, result, 0.0002);
    }

    public class CumulativeBinomialProbabilityBasedScoringImpl extends CumulativeBinomialProbabilityBasedScoring {

        public void calculateScore() {
        }
    }

}
