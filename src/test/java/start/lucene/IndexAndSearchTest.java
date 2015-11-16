/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import com.compomics.util.experiment.biology.PTMFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import static start.lucene.CPeptidesIndexerTest.deleteDirectory;
import theoretical.CPeptides;
import theoretical.CrossLinking;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class IndexAndSearchTest {

    private HashSet<StringBuilder> cPeptideEntries;
    private CPeptidesIndexer indexInstance;
    private File folder,
            indexFile,
            modsFile;
    private PTMFactory ptmFactory;

    public IndexAndSearchTest() throws FileNotFoundException, IOException, XmlPullParserException {
        folder = new File("Data\\Test\\database\\index");
        indexFile = new File("Data\\Test\\database\\test_mhcproteins_R_cxm_both_org_partial.index");
        modsFile = new File("C:/Users/Sule/Documents/NetBeansProjects/CrossLinkedPeptides/src/main/resources/mods.xml");
        deleteDirectory(folder);
        folder = new File("Data\\Test\\database\\index");
        cPeptideEntries = new HashSet<StringBuilder>();
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            cPeptideEntries.add(new StringBuilder(line));
        }
        indexInstance = new CPeptidesIndexer(cPeptideEntries, folder);
        indexInstance.index();
        ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
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
     * Test of getCPeptidesFromGivenMassRange method, of class IndexAndSearch.
     */
    @Test
    public void testGetCPeptidesFromGivenMassRange() throws Exception {
        System.out.println("getCPeptidesFromGivenMassRange");
        FragmentationMode fragMode = FragmentationMode.HCD;
        String crossLinkerName = "BS3";
        IndexAndSearch instance = new IndexAndSearch(cPeptideEntries, folder, ptmFactory, fragMode, crossLinkerName);
        ArrayList<CrossLinking> result = instance.getCPeptidesFromGivenMassRange(5689.948796, 5862.080226);
        assertEquals(7, result.size());
        // the following boolean are due to selecting the first peptide might be different while construction of 
        int numFirst00Found = 0,
                numFirst80Found = 0;
        boolean is00Found = false,
                is80Found = false;
        for (CrossLinking q : result) {
            CPeptides cp = (CPeptides) q;
            double theoretical_xlinked_mass = cp.getTheoretical_xlinked_mass();
            if (Math.abs(theoretical_xlinked_mass - 5705.94371192722) < 0.01) {
                assertEquals("P01903REVERSED(158-183)", cp.getProteinA());
                assertEquals("P01903REVERSED(158-183)", cp.getProteinB());
                assertEquals("KTMIELNAKDVAINALAGQAEFSAFR", cp.getPeptideA().getSequence());
                assertEquals("KTMIELNAKDVAINALAGQFEASAR", cp.getPeptideB().getSequence());
                int linkA = cp.getLinker_position_on_peptideA(),
                        linkB = cp.getLinker_position_on_peptideB();
                if (linkA == 0 && linkB == 0) {
                    numFirst00Found++;
                }
                if ((linkA == 8 && linkB == 0)) {
                    numFirst80Found++;
                }
            } else if (Math.abs(theoretical_xlinked_mass - 5862.08022592722) < 0.01) {
                assertEquals("P04233REVERSED(86-118)", cp.getProteinA());
                assertEquals("P04233REVERSED(126-139)", cp.getProteinB());
                assertEquals("TLVKPPADTPKQELSHRSMEFLLWHHMWSEFVK", cp.getPeptideA().getSequence());
                assertEquals("MKSVPKPPKPLKMR", cp.getPeptideB().getSequence());
                int linkA = cp.getLinker_position_on_peptideA(),
                        linkB = cp.getLinker_position_on_peptideB();
                assertEquals(10, linkA);
                assertEquals(1, linkB);
            } else if (Math.abs(theoretical_xlinked_mass - 5689.94879692721) < 0.01) {
                assertEquals("P01903REVERSED(158-183)", cp.getProteinA());
                assertEquals("P01903REVERSED(158-183)", cp.getProteinB());
                assertEquals("KTMIELNAKDVAINALAGQAEFSAFR", cp.getPeptideA().getSequence());
                assertEquals("KTMIELNAKDVAINALAGQFEASAR", cp.getPeptideB().getSequence());
                int linkA = cp.getLinker_position_on_peptideA(),
                        linkB = cp.getLinker_position_on_peptideB();
                if (linkA == 0 && linkB == 0) {
                    is00Found = true;
                }
                if ((linkA == 8 && linkB == 0)) {
                    is80Found = true;
                }
            }
        }
        assertTrue(is00Found);
        assertTrue(is80Found);
        assertEquals(numFirst00Found, 2);
        assertEquals(numFirst80Found, 2);

        result = instance.getCPeptidesFromGivenMassRange(1693.05, 5926.06);
        assertEquals(26, result.size());

        result = instance.getCPeptidesFromGivenMassRange(1693, 5926.06);
        assertEquals(26, result.size());

        result = instance.getCPeptidesFromGivenMassRange(1603, 5927);
        assertEquals(26, result.size());

        result = instance.getCPeptidesFromGivenMassRange(160, 5927);
        assertEquals(26, result.size());

        // just to print how query looks..
        Query numeric_query = NumericRangeQuery.newDoubleRange("mass", 1, 110.0, 2000.0, false, false);
        System.out.println("numeric_query is \t" + numeric_query.toString());
    }

}
