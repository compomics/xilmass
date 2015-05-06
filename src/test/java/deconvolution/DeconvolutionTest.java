/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package deconvolution;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class DeconvolutionTest {
    
    public DeconvolutionTest() {
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
     * Test of getDeconvolutedExpMSnSpectrum method, of class Deconvolution.
     */
    @Test
    public void testGetDeconvolutedExpMSnSpectrum() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("getDeconvolutedExpMSnSpectrum");
        File deconvulate =new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\deconvulate/");
        MSnSpectrum ms=null ;
         for (File mgf : deconvulate.listFiles()) {
            if (mgf.getName().endsWith(".mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    System.out.println(title2);
                    ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            }
        }
        Deconvolution instance = new Deconvolution(ms);
        MSnSpectrum expResult = null;
        MSnSpectrum result = instance.getDeconvolutedExpMSnSpectrum();
        assertEquals(expResult, result);
    }

    /**
     * Test of setExpMSnSpectrum method, of class Deconvolution.
     */
    @Test
    public void testSetExpMSnSpectrum() {
        System.out.println("setExpMSnSpectrum");
        MSnSpectrum expMSnSpectrum = null;
        Deconvolution instance = null;
        instance.setExpMSnSpectrum(expMSnSpectrum);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isIsDeconvoluted method, of class Deconvolution.
     */
    @Test
    public void testIsIsDeconvoluted() {
        System.out.println("isIsDeconvoluted");
        Deconvolution instance = null;
        boolean expResult = false;
        boolean result = instance.isIsDeconvoluted();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setIsDeconvoluted method, of class Deconvolution.
     */
    @Test
    public void testSetIsDeconvoluted() {
        System.out.println("setIsDeconvoluted");
        boolean isDeconvoluted = false;
        Deconvolution instance = null;
        instance.setIsDeconvoluted(isDeconvoluted);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deconvolute method, of class Deconvolution.
     */
    @Test
    public void testDeconvolute() {
        System.out.println("deconvolute");
        Deconvolution instance = null;
        instance.deconvolute();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPrecurMass method, of class Deconvolution.
     */
    @Test
    public void testGetPrecurMass() {
        System.out.println("getPrecurMass");
        Deconvolution instance = null;
        double expResult = 0.0;
        double result = instance.getPrecurMass();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMZ method, of class Deconvolution.
     */
    @Test
    public void testGetMZ() {
        System.out.println("getMZ");
        double precursorMass = 0.0;
        int charge = 0;
        Deconvolution instance = null;
        double expResult = 0.0;
        double result = instance.getMZ(precursorMass, charge);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
