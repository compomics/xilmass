/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.shared.retrieving;

import analyse.shared.Information;
import analyse.validated.CrossLinkingSite;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sule
 */
public class RetrieveValidatedListTest {

    public RetrieveValidatedListTest() {
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
     * Test of getInput method, of class RetrieveValidatedList.
     */
    @Test
    public void testGetInput() {
        System.out.println("getInput");
        RetrieveValidatedList instance = null;
        File expResult = null;
        File result = instance.getInput();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInput method, of class RetrieveValidatedList.
     */
    @Test
    public void testSetInput() {
        System.out.println("setInput");
        File input = null;
        RetrieveValidatedList instance = null;
        instance.setInput(input);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRetrievedInfo method, of class RetrieveValidatedList.
     */
    @Test
    public void testGetRetrievedInfo() throws Exception {
        System.out.println("getRetrievedInfo");
        RetrieveValidatedList instance = new RetrieveXilmass(new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_test.txt"));
        ArrayList<Information> result = instance.getRetrievedInfo();
        assertEquals(227, result.size());
        Information tmpInfo = result.get(15);
        assertEquals("Probe2_v_mp1_top15HCD-1.mgf", tmpInfo.getFileName());
        assertEquals("1047", tmpInfo.getScanNumber());
        assertEquals("xilmass", tmpInfo.getFoundBy());
        assertEquals("21.1", tmpInfo.getEuclidean_alpha());
        assertEquals("21.9", tmpInfo.getEuclidean_beta());
        assertEquals("light", tmpInfo.getLabel());
        assertEquals("37", tmpInfo.getLinkA()); // index starts from 1
        assertEquals("71", tmpInfo.getLinkB()); // index starts from 1
        assertEquals("HLIKAQR", tmpInfo.getPeptideA());
        assertEquals("EKGR", tmpInfo.getPeptideB());
        assertEquals("POSSIBLE", tmpInfo.getPredicted());
        assertEquals("Q15149", tmpInfo.getProteinA());
        assertEquals("Q15149", tmpInfo.getProteinB());
        assertEquals(139.9346362, tmpInfo.getScore(), 0.0001);
        assertEquals("target", tmpInfo.getTd());

        instance = new RetrievepLinkValidateds(new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/pLink_Native5FDRValidated_Elite_OnlyTarget.txt"));
        result = instance.getRetrievedInfo();
        assertEquals(93, result.size());

        tmpInfo = result.get(60);
        assertEquals("Probe2_v_x1_top15HCD-1.mgf", tmpInfo.getFileName());
        assertEquals("5153", tmpInfo.getScanNumber());
        assertEquals("plink", tmpInfo.getFoundBy());
        assertEquals("-", tmpInfo.getEuclidean_alpha());
        assertEquals("-", tmpInfo.getEuclidean_beta());
        assertEquals("heavy", tmpInfo.getLabel());
        assertEquals("78", tmpInfo.getLinkA()); // index starts from 1
        assertEquals("95", tmpInfo.getLinkB()); // index starts from 1
        assertEquals("FHKLQNVQIALDYLR", tmpInfo.getPeptideA());
        assertEquals("VFDKDGNGYISAAELR", tmpInfo.getPeptideB());
        assertEquals("Not-predicted", tmpInfo.getPredicted());
        assertEquals("Q15149", tmpInfo.getProteinA());
        assertEquals("P62158", tmpInfo.getProteinB());
        assertEquals(7.90E-06, tmpInfo.getScore(), 0.0001);
        assertEquals("target", tmpInfo.getTd());
    }

    /**
     * Test of getXLinkingSites method, of class AnalyseXPSMs.
     */
    @Test
    public void testGetXLinkingSites() throws IOException {
        System.out.println("getXLinkingSites");
        File xilmassXlinkingTest = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_test_xlinkings.txt");
        RetrieveXilmass obj = new RetrieveXilmass(xilmassXlinkingTest);
        obj.getRetrievedInfo();
        ArrayList<Information> validateds = obj.getValidateds(0.05000, true);
        HashMap<CrossLinkingSite, Integer> expResult = null;
        HashMap<CrossLinkingSite, Integer> result = RetrieveValidatedList.getXLinkingSites(validateds);
        int allXPSMs = 0;

        for (CrossLinkingSite c : result.keySet()) {
            System.out.println(c.toString());
            allXPSMs += result.get(c);
            if (c.getProteinA().equals("P62158")
                    && c.getProteinB().equals("P62158")
                    && c.getLinkA().equals("95")
                    && c.getLinkB().equals("78")) {
                assertEquals(4, result.get(c), 0.03);
                assertEquals("Not-predicted", c.getPrediction());
            } else if (c.getProteinA().equals("P62158")
                    && c.getProteinB().equals("P62158")
                    && c.getLinkA().equals("116")
                    && c.getLinkB().equals("95")) {
                assertEquals(2, result.get(c), 0.03);
                assertEquals("LIKELY POSSIBLE", c.getPrediction());
            } else if (c.getProteinA().equals("P62158")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("116")
                    && c.getLinkB().equals("71")) {
                assertEquals(1, result.get(c), 0.03);
                assertEquals("Not-predicted", c.getPrediction());
            } else if (c.getProteinA().equals("P62158")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("22")
                    && c.getLinkB().equals("16")) {
                assertEquals(3, result.get(c), 0.03);
                assertEquals("POSSIBLE", c.getPrediction());
            } else if (c.getProteinA().equals("P62158")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("95")
                    && c.getLinkB().equals("16")) {
                assertEquals(2, result.get(c), 0.03);
                assertEquals("Not-predicted", c.getPrediction());
            } else if (c.getProteinA().equals("Q15149")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("25")
                    && c.getLinkB().equals("16")) {
                assertEquals(2, result.get(c), 0.03);
                assertEquals("POSSIBLE", c.getPrediction());
            } else if (c.getProteinA().equals("Q15149")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("37")
                    && c.getLinkB().equals("71")) {
                assertEquals(4, result.get(c), 0.03);
                assertEquals("POSSIBLE", c.getPrediction());
            } else if (c.getProteinA().equals("Q15149")
                    && c.getProteinB().equals("Q15149")
                    && c.getLinkA().equals("78")
                    && c.getLinkB().equals("71")) {
                assertEquals(7, result.get(c), 0.03);
                assertEquals("POSSIBLE", c.getPrediction());
            } else {
                assertEquals(1, result.get(c), 0.003);
            }

        }
        assertEquals(12, result.size());
        assertEquals(29, allXPSMs);

    }

    /**
     * Test of getValidateds method, of class RetrieveValidatedList.
     */
    @Test
    public void testGetValidateds_int() throws Exception {
        System.out.println("getValidateds");
        RetrieveValidatedList instance = new RetrieveXilmass(new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_test.txt"));
        int numDecoy = 1;
        ArrayList<Information> expResult = null;
        HashSet<Information> result = instance.getValidateds(1);
        assertEquals(69, result.size());
        numDecoy = 2;
        assertEquals(78, instance.getValidateds(numDecoy).size());
        numDecoy = 3;
        assertEquals(132, instance.getValidateds(numDecoy).size());
    }

    /**
     * Test of getValidateds method, of class RetrieveValidatedList.
     */
    @Test
    public void testGetValidateds_double_boolean() throws Exception {
        System.out.println("getValidateds");
        RetrieveValidatedList instance = new RetrieveXilmass(new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_test.txt"));

        ArrayList<Information> result = instance.getValidateds(0.05, true);
        assertEquals(172, result.size());

        result = instance.getValidateds(0.05, false);
        assertEquals(199, result.size());
    }

    /**
     * Test of getRankAnDfdr method, of class RetrieveValidatedList.
     */
    @Test
    public void testGetRankAnDfdr() throws IOException {
        System.out.println("getRankAnDfdr");
        RetrieveValidatedList instance = new RetrieveXilmass(new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_test.txt"));

        boolean isTraditionalFDR = true;
        instance.getValidateds(0.05, isTraditionalFDR);
        HashMap<Integer, Double> result = instance.getRankAnDfdr(0.05);

        double tmpFdr = Math.floor(((double) 1 / (double) 69) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(70), 0.004);

        tmpFdr = Math.floor(((double) 2 / (double) 78) * 1000) / 1000;
        assertEquals(tmpFdr, result.get(80), 0.004);

        tmpFdr = Math.floor(((double) 3 / (double) 132) * 1000) / 1000;
        assertEquals(tmpFdr, result.get(135), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 135) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(139), 0.004);

        tmpFdr = Math.floor(((double) 5 / (double) 149) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(154), 0.004);

        tmpFdr = Math.floor(((double) 6 / (double) 154) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(160), 0.004);

        tmpFdr = Math.floor(((double) 7 / (double) 170) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(177), 0.004);

        tmpFdr = Math.floor(((double) 8 / (double) 172) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(180), 0.004);

        tmpFdr = Math.floor(((double) 9 / (double) 172) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(181), 0.004);

        // Rest gives bigger than 5%PSM FDR
//        tmpFdr = Math.floor(((double) 10 / (double) 174) * 1000000) / 1000000;
//        assertEquals(tmpFdr, result.get(184), 0.004);
//        tmpFdr = Math.floor(((double) 11 / (double) 174) * 1000000) / 1000000;
//        assertEquals(tmpFdr, result.get(185), 0.004);
        // PLINK FDR!
        instance.getValidateds(0.05, false);
        result = instance.getRankAnDfdr(0.05);

        tmpFdr = Math.floor(((double) 1 / (double) 69) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(70), 0.004);

        tmpFdr = Math.floor(((double) 2 / (double) 78) * 1000) / 1000;
        assertEquals(tmpFdr, result.get(80), 0.004);

        tmpFdr = Math.floor(((double) 3 / (double) 132) * 1000) / 1000;
        assertEquals(tmpFdr, result.get(135), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 135) * 1000) / 1000;
        assertEquals(tmpFdr, result.get(139), 0.004);

        tmpFdr = Math.floor(((double) 5 / (double) 149) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(154), 0.004);

        tmpFdr = Math.floor(((double) 6 / (double) 154) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(160), 0.004);

        tmpFdr = Math.floor(((double) 7 / (double) 170) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(177), 0.004);

        tmpFdr = Math.floor(((double) 6 / (double) 172) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(180), 0.004);

        tmpFdr = Math.floor(((double) 5 / (double) 172) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(181), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 174) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(184), 0.004);
        tmpFdr = Math.floor(((double) 5 / (double) 174) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(185), 0.004);

        tmpFdr = Math.floor(((double) 6 / (double) 175) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(187), 0.004);

        tmpFdr = Math.floor(((double) 5 / (double) 184) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(197), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 187) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(201), 0.004);

        tmpFdr = Math.floor(((double) 3 / (double) 187) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(202), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 195) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(211), 0.004);

        tmpFdr = Math.floor(((double) 5 / (double) 195) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(212), 0.004);

        tmpFdr = Math.floor(((double) 4 / (double) 195) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(213), 0.004);

        int rank = 215,
                target = 196,
                decoy = 5;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);

        rank = 218;
        target = 198;
        decoy = 6;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);

        rank = 219;
        target = 198;
        decoy = 7;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);

        rank = 220;
        target = 199;
        decoy = 8;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);

        rank = 222;
        target = 199;
        decoy = 9;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);

        rank = 223;
        target = 199;
        decoy = 10;
        tmpFdr = Math.floor(((double) decoy / (double) target) * 1000000) / 1000000;
        assertEquals(tmpFdr, result.get(rank), 0.004);
    }

    public class RetrieveValidatedListImpl extends RetrieveValidatedList {

        public RetrieveValidatedListImpl() throws IOException {
            super(null);
        }

        public ArrayList<Information> getRetrievedInfo() throws FileNotFoundException, IOException {
            return null;
        }
    }

}
