/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.validated;

import analyse.shared.Information;
import analyse.shared.retrieving.RetrieveValidatedList;
import analyse.shared.retrieving.RetrieveXilmass;
import analyse.shared.retrieving.RetrievepLinkValidateds;
import static analyse.validated.AnalyseXPSMs.getSharedInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class AnalyseXPSMsTest {

    public AnalyseXPSMsTest() {
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
     * Test of main method, of class AnalyseXPSMs.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        AnalyseXPSMs.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printXLinkingSites method, of class AnalyseXPSMs.
     */
    @Test
    public void testPrintXLinkingSites() {
        System.out.println("printXLinkingSites");
        HashMap<CrossLinkingSite, Integer> xLinkingSitesAndXPSMs = null;
        AnalyseXPSMs.printXLinkingSites(xLinkingSitesAndXPSMs);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printFDRs method, of class AnalyseXPSMs.
     */
    @Test
    public void testPrintFDRs() throws Exception {
        System.out.println("printFDRs");
        HashMap<Integer, Double> rankAnDfdr = null;
        AnalyseXPSMs.printFDRs(rankAnDfdr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sameMSMSdiffXLinking method, of class AnalyseXPSMs.
     */
    @Test
    public void testSameMSMSdiffXLinking() throws Exception {
        System.out.println("sameMSMSdiffXLinking");
        ArrayList<Information> first = null;
        ArrayList<Information> second = null;
        AnalyseXPSMs.sameMSMSdiffXLinking(first, second);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSharedInfo method, of class AnalyseXPSMs.
     */
    @Test
    public void testGetSharedInfo() throws IOException {
        System.out.println("getSharedInfo");
        File xilmassInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_part_shared.txt"),
                validatedPLinkInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/valPlink_part_shared.txt");
        RetrieveValidatedList first = new RetrieveXilmass(xilmassInput), // the first information list..
                second = new RetrievepLinkValidateds(validatedPLinkInput); // the second information list...
        ArrayList<Information> firstValidateds = first.getValidateds(0.0500, true),
                secondValidateds = second.getRetrievedInfo(); // Here there are only validated hits 
        ArrayList<Information> result = getSharedInfo(firstValidateds, secondValidateds);

        assertEquals(2, result.size());
    }

    /**
     * Test of getSharedXLinkingSites method, of class AnalyseXPSMs.
     */
    @Test
    public void testGetSharedXLinkingSites() throws IOException {
        System.out.println("getSharedXLinkingSites");
        File xilmassInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_part_shared.txt"),
                validatedPLinkInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/valPlink_part_shared.txt");
        RetrieveValidatedList first = new RetrieveXilmass(xilmassInput), // the first information list..
                second = new RetrievepLinkValidateds(validatedPLinkInput); // the second information list...
        ArrayList<Information> firstValidateds = first.getValidateds(0.0500, true),
                secondValidateds = second.getRetrievedInfo(); // Here there are only validated hits 
        HashMap<CrossLinkingSite, Integer> result = AnalyseXPSMs.getSharedXLinkingSites(firstValidateds, secondValidateds);
        assertEquals(1, result.size());
    }

    /**
     * Test of findOnly method, of class AnalyseXPSMs.
     */
    @Test
    public void testFindOnly() throws IOException {
        System.out.println("findOnly");
        File xilmassInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/xilmass_part_shared.txt"),
                validatedPLinkInput = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze/valPlink_part_shared.txt");
        RetrieveValidatedList first = new RetrieveXilmass(xilmassInput), // the first information list..
                second = new RetrievepLinkValidateds(validatedPLinkInput); // the second information list...
        ArrayList<Information> firstValidateds = first.getValidateds(0.0500, true),
                secondValidateds = second.getRetrievedInfo(); // Here there are only validated hits 
        HashMap<CrossLinkingSite, Integer> sharedXLinkingSites = AnalyseXPSMs.getSharedXLinkingSites(firstValidateds, secondValidateds);
        ArrayList<Information> validateds = first.getValidateds(0.05000, true);
        HashMap<CrossLinkingSite, Integer> firstXlinkingSite = RetrieveValidatedList.getXLinkingSites(validateds);

        ArrayList<Information> secondvalidateds = second.getValidateds(0.05000, true);
        HashMap<CrossLinkingSite, Integer> secondXlinkingSite = RetrieveValidatedList.getXLinkingSites(secondvalidateds);

        HashMap<CrossLinkingSite, Integer> result = AnalyseXPSMs.findOnly(firstXlinkingSite, sharedXLinkingSites);
        assertEquals(8, firstXlinkingSite.size());
        assertEquals(7, result.size());

        result = AnalyseXPSMs.findOnly(secondXlinkingSite, sharedXLinkingSites);
        assertEquals(6, secondXlinkingSite.size());
        assertEquals(5, result.size());
    }

}
