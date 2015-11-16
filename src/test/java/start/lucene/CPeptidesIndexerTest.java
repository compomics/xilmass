/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import org.apache.lucene.document.Document;
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
public class CPeptidesIndexerTest {

    public CPeptidesIndexerTest() {
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
     * Test of getIndexWriter method, of class CPeptidesIndexer.
     */
    @Test
    public void testGetIndexWriter() throws Exception {
        System.out.println("getIndexWriter");
        File folder = new File("Data\\Test\\database\\index"),
                indexFile = new File("Data\\Test\\database\\test_mhcproteins_R_cxm_both_org_partial.index");
        deleteDirectory(folder);
        folder = new File("Data\\Test\\database\\index");
        HashSet<StringBuilder> cPeptideEntries = new HashSet<StringBuilder>();
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            cPeptideEntries.add(new StringBuilder(line));
        }
        CPeptidesIndexer instance = new CPeptidesIndexer(cPeptideEntries, folder);
        assertEquals(30, instance.getTotalDoc());
    }

    /**
     * Force deletion of directory
     *
     * @param path
     * @return
     */
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    System.out.println("file is being deleted...");
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Test of getDocument method, of class CPeptidesIndexer.
     */
    @Test
    public void testGetDocument() throws Exception {
        System.out.println("getDocument");
        File folder = new File("Data\\Test\\database\\index");
        deleteDirectory(folder);
        folder = new File("Data\\Test\\database\\index");
        HashSet<StringBuilder> cPeptideEntries = new HashSet<StringBuilder>();
        StringBuilder line = new StringBuilder("P01911REVERSED(25-30)	P04233(58-63)	LAQLKK	KLRLDK	4	0	[acetylation of protein n-term_1]	[acetylation of protein n-term_1]			1693.05019292722	CROSSLINK	lightLabeled");
        CPeptidesIndexer instance = new CPeptidesIndexer(cPeptideEntries, folder);
        Document result = instance.getDocument(line);
        assertEquals("P01911REVERSED(25-30)", result.getValues(FieldName.PROTEINA)[0]);
        assertEquals("P04233(58-63)", result.getValues(FieldName.PROTEINB)[0]);
        assertEquals("LAQLKK", result.getValues(FieldName.PEPTIDEA)[0]);
        assertEquals("KLRLDK", result.getValues(FieldName.PEPTIDEB)[0]);
        assertEquals("1693.05019292722", result.getValues(FieldName.MASS)[0]);
        assertNotSame(1693.05019292722, result.getValues(FieldName.MASS)[0]);
        assertEquals(1693.05019292722, Double.parseDouble(result.getValues(FieldName.MASS)[0]), 0);
        assertEquals(result.getFields().size(), 14); // also there is an index field.. 
        assertNotSame(1, result.getValues(FieldName.ID)[0]);
    }

}
