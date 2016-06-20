/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specprocessing;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class DeisotopingAndDeconvolutingTest {

    public DeisotopingAndDeconvolutingTest() {
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
     * Test of deisotope_and_deconvolute method, of class
     * DeisotopingAndDeconvoluting.
     */
    @Test
    public void testDeisotopedeisotopeDeconvolute() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("deisotope_and_deconvolute");
        File deconvolute = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\deconvolute/");
        MSnSpectrum test = null,
                test2 = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();

        for (File mgf : deconvolute.listFiles()) {
            if (mgf.getName().equals("test3_deconv.mgf")) {
                System.out.println(mgf.getName());
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    test = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            } else if (mgf.getName().equals("test2.mgf")) {
                System.out.println(mgf.getName());
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    test2 = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            }
        }
        DeisotopingAndDeconvoluting instance = new DeisotopingAndDeconvoluting(test, 0.01, 0.003),
                instance2 = new DeisotopingAndDeconvoluting(test2, 0.01, 0.003);
        instance.deisotope_and_deconvolute();
        instance2.deisotope_and_deconvolute();

        MSnSpectrum result = instance.getDeisotopedDeconvolutedExpMSnSpectrum(),
                result2 = instance2.getDeisotopedDeconvolutedExpMSnSpectrum();
        ArrayList<Double> peakMZs2 = new ArrayList<Double>(result2.getPeakMap().keySet()),
                ints2 = new ArrayList<Double>();
        Collections.sort(peakMZs2);
        for (Double mz : peakMZs2) {
            ints2.add(result.getPeakMap().get(mz).intensity);
        }
        assertEquals(6, result2.getPeakList().size());
        // check m/z values
        assertEquals(141.83815, peakMZs2.get(0), 0.01);
        assertEquals(147.1042023, peakMZs2.get(1), 0.01);
        assertEquals(147.1128, peakMZs2.get(2), 0.01);
        assertEquals(147.1226, peakMZs2.get(3), 0.01);
        assertEquals(157.0968, peakMZs2.get(4), 0.01);
        assertEquals(160.0423584, peakMZs2.get(5), 0.01);
        // now check intensities
        assertEquals(2690.345214, ints2.get(0), 0.01);
        assertEquals(2244.64453, ints2.get(1), 0.01);
        assertEquals(33297.35937, ints2.get(2), 0.01);
        assertEquals(3842.2458496, ints2.get(3), 0.01);
        assertEquals((2935.53417 + 16198.2031 + 98345.375 + 3316.72119), ints2.get(4), 0.01);
        assertEquals(4620.1030, ints2.get(5), 0.01);

        // this mgf contains: 91 peaks (test.mgf has a MS/MS spectrum with 212 peaks)
        ArrayList<Double> peakMZs = new ArrayList<Double>(result.getPeakMap().keySet()),
                ints = new ArrayList<Double>();
        Collections.sort(peakMZs);
        for (Double mz : peakMZs) {
            ints.add(result.getPeakMap().get(mz).intensity);
        }
//        System.out.println("DEISOTOPED PEAK LIST");
//        for (Double mz : peakMZs) {
//            System.out.println(mz + "\t" + result.getPeakMap().get(mz).intensity);
//        }
        // orj ms/ms contains 139 peaks...
        assertEquals(56, result.getPeakList().size());

        // check m/z values
        assertEquals(141.83815, peakMZs.get(0), 0.01);
        assertEquals(157.0968018, peakMZs.get(4), 0.01);
        assertFalse(peakMZs.contains(158.0957947));
        assertFalse(peakMZs.contains(159.0913696));
        assertEquals(160.0423584, peakMZs.get(5), 0.01);
        assertEquals(175.1082611, peakMZs.get(9), 0.01);
        assertFalse(peakMZs.contains(559.6685181)); // this is triply charged and converted into singly charged peak
    }

    /**
     * Test of deconvolute method, of class
     * DeisotopingAndDeconvoluting.
     */
    @Test
    public void testDeconvoluting() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("deconvolute");
        File deconvolute = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\deconvolute/");
        MSnSpectrum test = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();
        for (File mgf : deconvolute.listFiles()) {
            if (mgf.getName().equals("test3_deconv.mgf")) {
                System.out.println(mgf.getName());
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    test = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            }
        }
        DeisotopingAndDeconvoluting instance = new DeisotopingAndDeconvoluting(test, 0.01, 0.003);
        HashMap<Double, Peak> deconvoluteds = new HashMap<Double, Peak>();
        Peak toAdd = new Peak(587.6838379, 8909.59375);
        Peak deconvoluted = instance.deconvolute(toAdd, 3, deconvoluteds);
        assertEquals(1761.05, deconvoluted.mz, 0.05);

        toAdd = new Peak(594.0211792, 8909.59375);
        deconvoluted = instance.deconvolute(toAdd, 3, deconvoluteds);
        assertEquals(1780.062, deconvoluted.mz, 0.05);
    }

}
