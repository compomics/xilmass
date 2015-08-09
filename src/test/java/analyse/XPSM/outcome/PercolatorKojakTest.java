/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import analyse.XPSM.NameTargetDecoy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class PercolatorKojakTest {

    public PercolatorKojakTest() {
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

    @Test
    public void checkAll() throws IOException, FileNotFoundException, IllegalArgumentException, InterruptedException, ClassNotFoundException {
        System.out.println("All attributes...");

        String proteins = ">sp|P62158|CALM_HUMAN_Calmodulin_OS=Homo_sapiens_GN=CALM1_PE=1_SV=2	>sp|Q15149|175-400PlectinABDisoform1a(FromJakeSong)",
                peptides = "-.EAFSLFDKDGDGTITTK(8)--ASEGKKDER(6).-",
                psmID = "T-4909-2";
        File db = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases/cam_plectin.fasta");

        HashMap<String, String> accs = NameTargetDecoy.getAccs(db);

        double score = 0.00666738,
                qvalue = 0,
                posterior_error = 0.0967988;

        PercolatorKojak o = new PercolatorKojak("name", psmID, peptides, proteins, score, qvalue, posterior_error, accs);

        assertEquals("P62158", o.getProteinA());
        assertEquals("Q15149", o.getProteinB());
        assertEquals("EAFSLFDKDGDGTITTK", o.getPeptideA());
        assertEquals("ASEGKKDER", o.getPeptideB());
        assertEquals(22, o.getLinkA());
        assertEquals(16, o.getLinkB());
        assertEquals("name", o.getMgfName());
        assertEquals(0.0967988, o.getPosterior_error(), 0.001);
        assertEquals(0.00666738, o.getScore(), 0.001);
        assertEquals(0, o.getQvalue(), 0.001);
        assertEquals(4909, o.getScan());

        psmID = "T-6522-2";
        score = -0.61326;
        qvalue = 0.18879;
        posterior_error = 0.415834;
        peptides = "-.KMKDTDSEEEIR(1)--HRQVKLVNIR(5).-";
        proteins = ">sp|P62158|CALM_HUMAN_Calmodulin_OS=Homo_sapiens_GN=CALM1_PE=1_SV=2	>sp|Q15149|175-400PlectinABDisoform1a(FromJakeSong)";
        o = new PercolatorKojak("name", psmID, peptides, proteins, score, qvalue, posterior_error, accs);

        assertEquals(score, o.getScore(), 0.001);
        assertEquals(6522, o.getScan());
        assertEquals("P62158", o.getProteinA());
        assertEquals("Q15149", o.getProteinB());
        assertEquals("KMKDTDSEEEIR", o.getPeptideA());
        assertEquals("HRQVKLVNIR", o.getPeptideB());
        assertEquals(76, o.getLinkA());
        assertEquals(95, o.getLinkB());
        assertEquals("name", o.getMgfName());
        assertEquals(posterior_error, o.getPosterior_error(), 0.001);
        assertEquals(score, o.getScore(), 0.001);
        assertEquals(qvalue, o.getQvalue(), 0.001);

        proteins = ">sp|Q15149|175-400PlectinABDisoform1a(FromJakeSong)";
        o = new PercolatorKojak("name", psmID, peptides, proteins, score, qvalue, posterior_error, accs);
        assertEquals("Q15149", o.getProteinA());
        assertEquals("Q15149", o.getProteinA());

    }

}
