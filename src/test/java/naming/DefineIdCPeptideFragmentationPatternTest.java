/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naming;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import theoretical.CPeptidePeak;

/**
 *
 * @author Sule
 */
public class DefineIdCPeptideFragmentationPatternTest {

    public DefineIdCPeptideFragmentationPatternTest() {
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
     * Test of getName method, of class DefineIdCPeptideFragmentationPattern.
     */
    @Test
    public void testGetName_LINEAR() {
        System.out.println("getName_LINEAR");
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(173.092, 100, "singlyCharged_pepA_x1")); //1
        matchedCPepPeaks.add(new CPeptidePeak(276.1553, 100, "singlyCharged_pepA_y2"));//3
        matchedCPepPeaks.add(new CPeptidePeak(472.2401, 100, "singlyCharged_pepA_x4"));//5
        matchedCPepPeaks.add(new CPeptidePeak(849.4174, 100, "doublyCharged_pepA_x5"));//14

//        matchedCPepPeaks.add(new CPeptidePeak(529.2818, 100, 2, "doublyCharged_pepA_a4_lepB_a4"));//8
        matchedCPepPeaks.add(new CPeptidePeak(486.2194, 100, "singlyCharged_pepB_b4"));//6
        matchedCPepPeaks.add(new CPeptidePeak(526.7378, 100, "doublyCharged_pepB_b9"));//7
        matchedCPepPeaks.add(new CPeptidePeak(633.2878, 100, "singlyCharged_pepB_b5"));//10
        matchedCPepPeaks.add(new CPeptidePeak(733.3515, 100, "singlyCharged_pepB_a6"));//12

        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 4, 12, 25, 19); //score=0.138399306
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.LINEAR_NPEPB_CPEPA;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(131.0815, 100, "singlyCharged_pepA_a2"));
        matchedCPepPeaks.add(new CPeptidePeak(130.5656, 100, "doublyCharged_pepA_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(620.8666, 100, "doublyCharged_pepA_b6"));
        matchedCPepPeaks.add(new CPeptidePeak(1240.725, 100, "singlyCharged_pepA_b6"));
        matchedCPepPeaks.add(new CPeptidePeak(573.2627, 100, "singlyCharged_pepA_x4"));
        matchedCPepPeaks.add(new CPeptidePeak(287.1349, 100, "doublyCharged_pepA_x4"));
        matchedCPepPeaks.add(new CPeptidePeak(175.1189, 100, "singlyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(304.1615, 100, "singlyCharged_pepA_y2"));
        matchedCPepPeaks.add(new CPeptidePeak(419.1884, 100, "singlyCharged_pepA_y3"));
        matchedCPepPeaks.add(new CPeptidePeak(547.2834, 100, "singlyCharged_pepA_y4"));
        matchedCPepPeaks.add(new CPeptidePeak(274.1453, 100, "doublyCharged_pepA_y4"));
        matchedCPepPeaks.add(new CPeptidePeak(686.4013, 100, "doublyCharged_pepA_y6"));
        matchedCPepPeaks.add(new CPeptidePeak(750.9226, 100, "doublyCharged_pepA_y7"));
        matchedCPepPeaks.add(new CPeptidePeak(794.4386, 100, "doublyCharged_pepA_y8"));

        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LINEAR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a4"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LINEAR_PEPB;
        result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class DefineIdCPeptideFragmentationPattern.
     */
    @Test
    public void testGetName_MONOLINK() {
        System.out.println("getName_MONOLINK");
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(131.0815, 100, "singlyCharged_pepA_a2"));
        matchedCPepPeaks.add(new CPeptidePeak(130.5656, 100, "doublyCharged_pepA_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(620.8666, 100, "doublyCharged_pepA_b6"));
        matchedCPepPeaks.add(new CPeptidePeak(1240.725, 100, "singlyCharged_pepA_b6"));
        matchedCPepPeaks.add(new CPeptidePeak(573.2627, 100, "singlyCharged_pepA_x4"));
        matchedCPepPeaks.add(new CPeptidePeak(287.1349, 100, "doublyCharged_pepA_x4"));
        matchedCPepPeaks.add(new CPeptidePeak(175.1189, 100, "singlyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepA_b5_lepB_monolink_b3"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.MONOLINKED_PEPA;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.MONOLINKED_PEPB;
        result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetName_LEFTU() {
        System.out.println("getName_LEFTU");
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.LEFT_U;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a7"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b3_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_U;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a7"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b3_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetName_LEFT_CHAIR() {
        System.out.println("getName_LEFT_CHAIR");
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a8"));

        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_U;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a7"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b3_lepA_monolink_b3"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a8"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPB;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a2")); // only one ion on the other arm..       
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPB;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a2"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPB;
        result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testINTACT() {
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_b4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_y2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y4"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a3"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_y5"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_x5"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x3"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_a2"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.ALLOVER,
                result = instance.getName();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetName_RIGHTU() {
        System.out.println("getName_RIGHTU");
        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.RIGHT_U;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_U;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class DefineIdCPeptideFragmentationPattern..
     */
    @Test
    public void testGetName_RIGHTCHAIR() {
        System.out.println("getName_RIGHTCHAIR");

        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_a4"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a1"));
        matchedCPepPeaks.add(new CPeptidePeak(629.314, 100, "singlyCharged_pepB_b5_lepA_monolink_b3"));

        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_a6"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepA_x1"));

        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepB_a4"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepB_y1"));
        matchedCPepPeaks.add(new CPeptidePeak(159.0764, 100, "doublyCharged_pepB_x1"));
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 5, 3, 9, 4);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_U;
        result = instance.getName();
        assertEquals(expResult, result);

//        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
//        matchedCPepPeaks.add(new CPeptidePeak(173.092, 100, 1, "singlyCharged_pepA_x1")); //1
//        matchedCPepPeaks.add(new CPeptidePeak(276.1553, 100, 1, "singlyCharged_pepA_y2"));//3
//        matchedCPepPeaks.add(new CPeptidePeak(472.2401, 100, 1, "singlyCharged_pepA_x4"));//5
//        matchedCPepPeaks.add(new CPeptidePeak(849.4174, 100, 2, "doublyCharged_pepA_x15"));//14
//
//        matchedCPepPeaks.add(new CPeptidePeak(529.2818, 100, 2, "doublyCharged_pepA_a4_lepB_a4"));//8
//        matchedCPepPeaks.add(new CPeptidePeak(754.8928, 100, 2, "doublyCharged_pepB_a10_lepA_a2"));//13
//
//        matchedCPepPeaks.add(new CPeptidePeak(486.2194, 100, 1, "singlyCharged_pepB_b4"));//6
//        matchedCPepPeaks.add(new CPeptidePeak(526.7378, 100, 2, "doublyCharged_pepB_b9"));//7
//        matchedCPepPeaks.add(new CPeptidePeak(633.2878, 100, 1, "singlyCharged_pepB_b5"));//10
//        matchedCPepPeaks.add(new CPeptidePeak(733.3515, 100, 1, "singlyCharged_pepB_a6"));//12
//        matchedCPepPeaks.add(new CPeptidePeak(257.6528, 100, 2, "doublyCharged_pepB_x4"));//2
//        matchedCPepPeaks.add(new CPeptidePeak(387.2714, 100, 1, "singlyCharged_pepB_y3"));//4
//        matchedCPepPeaks.add(new CPeptidePeak(558.3504, 100, 2, "doublyCharged_pepB_y9"));//9
//        matchedCPepPeaks.add(new CPeptidePeak(674.3984, 100, 1, "singlyCharged_pepB_y5"));//11    
//        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, 9, 4);
//
//        doublyCharged_pepA_a5_lepB_a3_pepB_a3_lepA_a5_mz = 455.7714 
//        doublyCharged_pepA_b5_lepB_b2_mz = 419.7188 
//        singlyCharged_pepA_y5_lepB_y2_pepB_y2_lepA_y5_mz = 1087.6469
//         
//                
//        doublyCharged_pepB_y2_lepA_y7_mz = 637.3591 
//        doublyCharged_pepB_y2_lepA_y8_mz = 680.8751                 
//        
//                
//        singlyCharged_pepB_y2_lepA_y2_mz = 716.4301                 
//        singlyCharged_pepB_y2_lepA_y4_mz = 959.552                 
//        singlyCharged_pepB_y2_lepA_y6_mz = 1144.6684
//        doublyCharged_pepB_y2_lepA_y6_mz = 572.8378
//        singlyCharged_pepB_y2_lepA_y7_mz = 1273.711
//        singlyCharged_pepB_y2_lepA_y8_mz = 1360.743
//                
//        singlyCharged_pepB_x2_lepA_x2_mz = 768.3886  
//        doublyCharged_pepB_x2_lepA_x2_mz = 384.6979 
//        singlyCharged_pepB_x2_lepA_x3_mz = 883.4155
    }

    /**
     * Test of problematics ones, of class
     * DefineIdCPeptideFragmentationPattern..
     */
    @Test
    public void testGetName_Problem() {
        System.out.println("getName_Problem");

        ArrayList<CPeptidePeak> matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "doublyCharged_pepA_b2"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepA_y5_lepB_monolink_y3"));
        int linkerPosA = 2,
                linkerPosB = 40,
                pepALen = 6,
                pepBLen = 42;
        DefineIdCPeptideFragmentationPattern instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        IdCPeptideFragmentationPatternName expResult = IdCPeptideFragmentationPatternName.NODE_PEPA;
        IdCPeptideFragmentationPatternName result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "doublyCharged_pepA_b1_lepB_monolink_b7"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepB_y9"));
        linkerPosA = 0;
        linkerPosB = 6;
        pepALen = 12;
        pepBLen = 12;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.LINEAR_NPEPB_CPEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "doublyCharged_pepA_b1_lepB_monolink_b7"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepB_y9"));
        linkerPosA = 0;
        linkerPosB = 6;
        pepALen = 12;
        pepBLen = 12;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.LINEAR_NPEPB_CPEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_b1_pepB_b1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_x3_lepB_monolink_x3"));
        linkerPosA = 1;
        linkerPosB = 1;
        pepALen = 3;
        pepBLen = 13;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.LINK;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_b1_pepB_b1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_x3_lepB_monolink_x13"));
        linkerPosA = 1;
        linkerPosB = 1;
        pepALen = 3;
        pepBLen = 13;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.LINK;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "singlyCharged_pepA_b1_pepB_b1"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "singlyCharged_pepA_y1_pepB_y1"));
        linkerPosA = 1;
        linkerPosB = 1;
        pepALen = 7;
        pepBLen = 4;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.RIGHT_U;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "doublyCharged_pepA_x10"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepA_y11"));
        linkerPosA = 10;
        linkerPosB = 8;
        pepALen = 13;
        pepBLen = 12;
        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.LEFT_LINEAR_PEPA;
        result = instance.getName();
        assertEquals(expResult, result);

        matchedCPepPeaks = new ArrayList<CPeptidePeak>();
        matchedCPepPeaks.add(new CPeptidePeak(228.1342, 100, "doublyCharged_pepA_a7"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepB_b9_lepA_b8"));
        matchedCPepPeaks.add(new CPeptidePeak(147.1128, 100, "doublyCharged_pepB_a9_lepA_a10"));

        linkerPosA = 3;
        linkerPosB = 9;
        pepALen = ("GLKLANLEEKTIEELR").length();
        pepBLen = ("KNMDILMPKH").length();

        instance = new DefineIdCPeptideFragmentationPattern(matchedCPepPeaks, linkerPosA, linkerPosB, pepALen, pepBLen);
        expResult = IdCPeptideFragmentationPatternName.ATTACHEDTOPEPA_FROM_NODEPEPB;
        result = instance.getName();
        assertEquals(expResult, result);

    }
}
