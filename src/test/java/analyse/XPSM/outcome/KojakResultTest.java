/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.XPSM.outcome;

import com.compomics.util.protein.Protein;
import java.io.File;
import java.util.ArrayList;
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
public class KojakResultTest {
    
    public KojakResultTest() {
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
     * Test of getModPeptide1 method, of class KojakResult.
     */
    @Test
    public void testGetModPeptide1() {
        System.out.println("getModPeptide1");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getModPeptide1();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setModPeptide1 method, of class KojakResult.
     */
    @Test
    public void testSetModPeptide1() {
        System.out.println("setModPeptide1");
        String modPeptide1 = "";
        KojakResult instance = null;
        instance.setModPeptide1(modPeptide1);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModPeptide2 method, of class KojakResult.
     */
    @Test
    public void testGetModPeptide2() {
        System.out.println("getModPeptide2");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getModPeptide2();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setModPeptide2 method, of class KojakResult.
     */
    @Test
    public void testSetModPeptide2() {
        System.out.println("setModPeptide2");
        String modPeptide2 = "";
        KojakResult instance = null;
        instance.setModPeptide2(modPeptide2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSeqNoMod method, of class KojakResult.
     */
    @Test
    public void testGetSeqNoMod() {
        System.out.println("getSeqNoMod");
        String pep = "";
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getSeqNoMod(pep);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMod method, of class KojakResult.
     */
    @Test
    public void testGetMod() {
        System.out.println("getMod");
        String pep = "";
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getMod(pep);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCharge method, of class KojakResult.
     */
    @Test
    public void testGetCharge() {
        System.out.println("getCharge");
        KojakResult instance = null;
        int expResult = 0;
        int result = instance.getCharge();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCharge method, of class KojakResult.
     */
    @Test
    public void testSetCharge() {
        System.out.println("setCharge");
        int charge = 0;
        KojakResult instance = null;
        instance.setCharge(charge);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getObsMass method, of class KojakResult.
     */
    @Test
    public void testGetObsMass() {
        System.out.println("getObsMass");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getObsMass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setObsMass method, of class KojakResult.
     */
    @Test
    public void testSetObsMass() {
        System.out.println("setObsMass");
        double obsMass = 0.0;
        KojakResult instance = null;
        instance.setObsMass(obsMass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPsms_mass method, of class KojakResult.
     */
    @Test
    public void testGetPsms_mass() {
        System.out.println("getPsms_mass");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getPsms_mass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPsms_mass method, of class KojakResult.
     */
    @Test
    public void testSetPsms_mass() {
        System.out.println("setPsms_mass");
        double psms_mass = 0.0;
        KojakResult instance = null;
        instance.setPsms_mass(psms_mass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPpmErr method, of class KojakResult.
     */
    @Test
    public void testGetPpmErr() {
        System.out.println("getPpmErr");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getPpmErr();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPpmErr method, of class KojakResult.
     */
    @Test
    public void testSetPpmErr() {
        System.out.println("setPpmErr");
        double ppmErr = 0.0;
        KojakResult instance = null;
        instance.setPpmErr(ppmErr);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScore method, of class KojakResult.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getScore();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setScore method, of class KojakResult.
     */
    @Test
    public void testSetScore() {
        System.out.println("setScore");
        double score = 0.0;
        KojakResult instance = null;
        instance.setScore(score);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getdScore method, of class KojakResult.
     */
    @Test
    public void testGetdScore() {
        System.out.println("getdScore");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getdScore();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setdScore method, of class KojakResult.
     */
    @Test
    public void testSetdScore() {
        System.out.println("setdScore");
        double dScore = 0.0;
        KojakResult instance = null;
        instance.setdScore(dScore);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLinkerMass method, of class KojakResult.
     */
    @Test
    public void testGetLinkerMass() {
        System.out.println("getLinkerMass");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getLinkerMass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLinkerMass method, of class KojakResult.
     */
    @Test
    public void testSetLinkerMass() {
        System.out.println("setLinkerMass");
        double linkerMass = 0.0;
        KojakResult instance = null;
        instance.setLinkerMass(linkerMass);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPepDiff method, of class KojakResult.
     */
    @Test
    public void testGetPepDiff() {
        System.out.println("getPepDiff");
        KojakResult instance = null;
        double expResult = 0.0;
        double result = instance.getPepDiff();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPepDiff method, of class KojakResult.
     */
    @Test
    public void testSetPepDiff() {
        System.out.println("setPepDiff");
        double pepDiff = 0.0;
        KojakResult instance = null;
        instance.setPepDiff(pepDiff);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScanNumber method, of class KojakResult.
     */
    @Test
    public void testGetScanNumber() {
        System.out.println("getScanNumber");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getScanNumber();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setScanNumber method, of class KojakResult.
     */
    @Test
    public void testSetScanNumber() {
        System.out.println("setScanNumber");
        String scanNumber = "";
        KojakResult instance = null;
        instance.setScanNumber(scanNumber);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpectrumFileName method, of class KojakResult.
     */
    @Test
    public void testGetSpectrumFileName() {
        System.out.println("getSpectrumFileName");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getSpectrumFileName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSpectrumFileName method, of class KojakResult.
     */
    @Test
    public void testSetSpectrumFileName() {
        System.out.println("setSpectrumFileName");
        String spectrumFileName = "";
        KojakResult instance = null;
        instance.setSpectrumFileName(spectrumFileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPeptide1 method, of class KojakResult.
     */
    @Test
    public void testGetPeptide1() {
        System.out.println("getPeptide1");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getPeptide1();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPeptide1 method, of class KojakResult.
     */
    @Test
    public void testSetPeptide1() {
        System.out.println("setPeptide1");
        String peptide1 = "";
        KojakResult instance = null;
        instance.setPeptide1(peptide1);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPeptide2 method, of class KojakResult.
     */
    @Test
    public void testGetPeptide2() {
        System.out.println("getPeptide2");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.getPeptide2();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPeptide2 method, of class KojakResult.
     */
    @Test
    public void testSetPeptide2() {
        System.out.println("setPeptide2");
        String peptide2 = "";
        KojakResult instance = null;
        instance.setPeptide2(peptide2);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFasta method, of class KojakResult.
     */
    @Test
    public void testGetFasta() {
        System.out.println("getFasta");
        KojakResult instance = null;
        File expResult = null;
        File result = instance.getFasta();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFasta method, of class KojakResult.
     */
    @Test
    public void testSetFasta() {
        System.out.println("setFasta");
        File fasta = null;
        KojakResult instance = null;
        instance.setFasta(fasta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProteins method, of class KojakResult.
     */
    @Test
    public void testGetProteins() {
        System.out.println("getProteins");
        KojakResult instance = null;
        ArrayList<Protein> expResult = null;
        ArrayList<Protein> result = instance.getProteins();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProteins method, of class KojakResult.
     */
    @Test
    public void testSetProteins() {
        System.out.println("setProteins");
        ArrayList<Protein> proteins = null;
        KojakResult instance = null;
        instance.setProteins(proteins);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toPrint method, of class KojakResult.
     */
    @Test
    public void testToPrint() {
        System.out.println("toPrint");
        KojakResult instance = null;
        String expResult = "";
        String result = instance.toPrint();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class KojakResult.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        KojakResult instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class KojakResult.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        KojakResult instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
