/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Sule
 */
public class Andromeda_derivedTest extends TestCase {

    public Andromeda_derivedTest(String testName) {
        super(testName);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * Test of calculateScore method, of class Andromeda_derived.
     */
    public void testCalculateScore() {
        System.out.println("calculateScore");
        int N = 24;
        Andromeda_derived instance = new Andromeda_derived(0.01, N, 2);
        double score = instance.getScore();
        assertEquals(16.2, score,0.1);

        instance = new Andromeda_derived(0.02, N, 2);
        score = instance.getScore();
        assertEquals(10.8, score, 0.1);

    }

}
