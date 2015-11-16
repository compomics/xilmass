/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import static start.lucene.CPeptidesIndexerTest.deleteDirectory;

/**
 *
 * @author Sule
 */
public class CPeptideSearcherTest {

    HashSet<StringBuilder> cPeptideEntries;
    CPeptidesIndexer indexInstance;
    File folder, indexFile;

    public CPeptideSearcherTest() throws FileNotFoundException, IOException, XmlPullParserException {
        folder = new File("Data\\Test\\database\\index");
        indexFile = new File("Data\\Test\\database\\test_mhcproteins_R_cxm_both_org_partial.index");
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
     * Test of performMassRangeSearch method, of class CPeptideSearcher. Note
     * that all calculated values have 11 digits.
     */
    @Test
    public void testPerformMassRangeSearch() throws Exception {
        System.out.println("performMassRangeSearch");
        // first prepare index..

        CPeptidesIndexer indexInstance = new CPeptidesIndexer(cPeptideEntries, folder);
        indexInstance.index();

        // calculated ones.. 5689.94879692721 and 5705.94371192722
        // one is very close to the lower limit and the other one is slightly bigger than the upper limit
        double from = 5689.94879,
                to = 5705.944;
        int hits = 1;
        CPeptideSearcher instance = new CPeptideSearcher(folder);
        TopDocs result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(6, result.totalHits);

        // exactly border values...
        from = 5689.94879692721;
        to = 5705.94371192722;
        hits = 1;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(6, result.totalHits);

        from = 5690.94879692721;
        to = 5705.94371192722;
        hits = 1;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(4, result.totalHits);

        // lower limit has more digit.. (and bigger)
        from = 5689.9487969272155;
        to = 5705.94371192722;
        hits = 1;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(4, result.totalHits);

        // lower limit has more digit.. (and smaller)
        from = 5689.94879692721000;
        to = 5705.94371192722;
        hits = 1;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(6, result.totalHits);

        // lower limit has more digit.. (and smaller) and upper limit has also more digit (and bigger)
        from = 5689.94879692721000;
        to = 5705.94371192722222;
        hits = 1;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(6, result.totalHits);
        ScoreDoc[] res = result.scoreDocs;
        assertEquals(6, res.length);
        boolean isFirstDocFound = false,
                isSecondDocFound = false,
                isThirdFound = false,
                isFourthFound = false,
                isFifthFound = false,
                isSixthFound = false;
        for (ScoreDoc re : res) {
            Document doc = instance.getDocument(re.doc);
            String pepA = doc.getValues(FieldName.PEPTIDEA)[0].toString(),
                    pepB = doc.getValues(FieldName.PEPTIDEB)[0].toString(),
                    proA = doc.getValues(FieldName.PROTEINA)[0].toString(),
                    proB = doc.getValues(FieldName.PROTEINB)[0].toString(),
                    linkA = doc.getValues(FieldName.LINKA)[0].toString(),
                    linkB = doc.getValues(FieldName.LINKB)[0].toString(),
                    varA = doc.getValues(FieldName.VARMODA)[0].toString(),
                    varB = doc.getValues(FieldName.VARMODB)[0].toString(),
                    mass = doc.getValues(FieldName.MASS)[0].toString();
            System.out.println(pepA + "\t" + pepB + "\t" + proA + "\t" + proB + "\t" + linkA + "\t" + linkB + "\t" + varA + "\t" + varB + "\t" + mass);
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("0")
                    && linkB.equals("0")
                    && mass.equals("5689.94879692721")) {
                isFirstDocFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("8")
                    && linkB.equals("0")
                    && mass.equals("5689.94879692721")) {
                isSecondDocFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("0")
                    && linkB.equals("0")
                    && varA.equals("[oxidation of m_3]")
                    && mass.equals("5705.94371192722")) {
                isThirdFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("0")
                    && linkB.equals("0")
                    && varB.equals("[oxidation of m_3]")
                    && mass.equals("5705.94371192722")) {
                isFourthFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("8")
                    && linkB.equals("0")
                    && varA.equals("[oxidation of m_3]")
                    && mass.equals("5705.94371192722")) {
                isFifthFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("8")
                    && linkB.equals("0")
                    && varB.equals("[oxidation of m_3]")
                    && mass.equals("5705.94371192722")) {
                isSixthFound = true;
            }
        }
        assertTrue(isFirstDocFound);
        assertTrue(isSecondDocFound);
        assertTrue(isThirdFound);
        assertTrue(isFourthFound);
        assertTrue(isFifthFound);
        assertTrue(isSixthFound);

        // lower limit has more digit.. (and smaller) and upper limit has also more digit (and smaller)
        from = 5689.94879692721000;
        to = 5690.94879692721000;;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(2, result.totalHits);
        res = result.scoreDocs;
        isFirstDocFound = false;
        isSecondDocFound = false;
        for (ScoreDoc re : res) {
            Document doc = instance.getDocument(re.doc);
            String pepA = doc.getValues(FieldName.PEPTIDEA)[0].toString(),
                    pepB = doc.getValues(FieldName.PEPTIDEB)[0].toString(),
                    proA = doc.getValues(FieldName.PROTEINA)[0].toString(),
                    proB = doc.getValues(FieldName.PROTEINB)[0].toString(),
                    linkA = doc.getValues(FieldName.LINKA)[0].toString(),
                    linkB = doc.getValues(FieldName.LINKB)[0].toString(),
                    mass = doc.getValues(FieldName.MASS)[0].toString();
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("0")
                    && linkB.equals("0")
                    && mass.equals("5689.94879692721")) {
                isFirstDocFound = true;
            }
            if (pepA.equals("KTMIELNAKDVAINALAGQAEFSAFR")
                    && pepB.equals("KTMIELNAKDVAINALAGQFEASAR")
                    && proA.equals("P01903REVERSED(158-183)")
                    && proB.equals("P01903REVERSED(158-183)")
                    && linkA.equals("8")
                    && linkB.equals("0")
                    && mass.equals("5689.94879692721")) {
                isSecondDocFound = true;
            }
        }
        assertTrue(isFirstDocFound);
        assertTrue(isSecondDocFound);

        // to test in Da..
        from = 1690.0501;
        to = 1700.0501;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(1, result.totalHits);
        res = result.scoreDocs;
        isFirstDocFound = false;
        Document doc = instance.getDocument(res[0].doc);
        String pepA = doc.getValues(FieldName.PEPTIDEA)[0].toString(),
                pepB = doc.getValues(FieldName.PEPTIDEB)[0].toString(),
                proA = doc.getValues(FieldName.PROTEINA)[0].toString(),
                proB = doc.getValues(FieldName.PROTEINB)[0].toString(),
                linkA = doc.getValues(FieldName.LINKA)[0].toString(),
                linkB = doc.getValues(FieldName.LINKB)[0].toString(),
                mass = doc.getValues(FieldName.MASS)[0].toString();
        if (pepA.equals("LAQLKK")
                && pepB.equals("KLRLDK")
                && proA.equals("P01911REVERSED(25-30)")
                && proB.equals("P04233(58-63)")
                && linkA.equals("4")
                && linkB.equals("0")
                && mass.equals("1693.05019292722")) {
            isFirstDocFound = true;
        }
        assertTrue(isFirstDocFound);

        from = 1693.05019292720;
        to = 1700.0501;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(1, result.totalHits);
        res = result.scoreDocs;
        isFirstDocFound = false;
        doc = instance.getDocument(res[0].doc);
        pepA = doc.getValues(FieldName.PEPTIDEA)[0].toString();
        pepB = doc.getValues(FieldName.PEPTIDEB)[0].toString();
        proA = doc.getValues(FieldName.PROTEINA)[0].toString();
        proB = doc.getValues(FieldName.PROTEINB)[0].toString();
        linkA = doc.getValues(FieldName.LINKA)[0].toString();
        linkB = doc.getValues(FieldName.LINKB)[0].toString();
        mass = doc.getValues(FieldName.MASS)[0].toString();
        if (pepA.equals("LAQLKK")
                && pepB.equals("KLRLDK")
                && proA.equals("P01911REVERSED(25-30)")
                && proB.equals("P04233(58-63)")
                && linkA.equals("4")
                && linkB.equals("0")
                && mass.equals("1693.05019292722")) {
            isFirstDocFound = true;
        }
        assertTrue(isFirstDocFound);

        from = 1693.050192927222;
        to = 1700.0501;
        result = instance.performMassRangeSearch(from, to, hits);
        assertEquals(0, result.totalHits);
    }

}
