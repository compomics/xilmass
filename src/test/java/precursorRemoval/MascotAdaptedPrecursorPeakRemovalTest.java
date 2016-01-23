/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package precursorRemoval;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class MascotAdaptedPrecursorPeakRemovalTest {

    public MascotAdaptedPrecursorPeakRemovalTest() {
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
     * Test of removePrecursor method, of class
     * MascotAdaptedPrecursorPeakRemoval.
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.lang.ClassNotFoundException
     */
    @Test
    public void testRemovePrecursor() throws IOException, FileNotFoundException, MzMLUnmarshallerException, ClassNotFoundException {
        System.out.println("removePrecursor");
        File deconvolute = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\precursor_peak_removal/");
        MSnSpectrum first_case_msms = null,
                second_case_msms = null,
                third_case_msms = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();

        for (File mgf : deconvolute.listFiles()) {
            if (mgf.getName().equals("mascot_derived_precursor_peak_removal.mgf")) {
                System.out.println(mgf.getName());
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    if (title2.equals("first_case")) {
                        first_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    } else if (title2.equals("second_case")) {
                        second_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    } else if (title2.equals("third_case")) {
                        third_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    }
                }
            }
        }

        System.out.println("First instance");
        MascotAdaptedPrecursorPeakRemoval instance = new MascotAdaptedPrecursorPeakRemoval(first_case_msms, 0.10);
        instance.removePrecursor();
        assertEquals(15, first_case_msms.getPeakList().size());

        System.out.println("Second instance");
        instance = new MascotAdaptedPrecursorPeakRemoval(second_case_msms, 0.10);
        instance.removePrecursor();
        assertEquals(15, second_case_msms.getPeakList().size());

        System.out.println("Third instance");
        instance = new MascotAdaptedPrecursorPeakRemoval(third_case_msms, 0.10);
        instance.removePrecursor();
        assertEquals(17, third_case_msms.getPeakList().size());
    }

    /**
     * Test of getPrecursorPeaksRemovesExpMSnSpectrum method, of class MascotAdaptedPrecursorPeakRemoval.
     *
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.lang.ClassNotFoundException
     */
    @Test
    public void testGetMs() throws IOException, FileNotFoundException, MzMLUnmarshallerException, ClassNotFoundException {
        System.out.println("getMs");
        File deconvolute = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\precursor_peak_removal/");
        MSnSpectrum first_case_msms = null,
                second_case_msms = null,
                third_case_msms = null;
        SpectrumFactory fct = SpectrumFactory.getInstance();

        for (File mgf : deconvolute.listFiles()) {
            if (mgf.getName().equals("mascot_derived_precursor_peak_removal.mgf")) {
                System.out.println(mgf.getName());
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    if (title2.equals("first_case")) {
                        first_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    } else if (title2.equals("second_case")) {
                        second_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    } else if (title2.equals("third_case")) {
                        third_case_msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                    }
                }
            }
        }
        MascotAdaptedPrecursorPeakRemoval instance = new MascotAdaptedPrecursorPeakRemoval(first_case_msms, 0.10);
        assertEquals(false, instance.arePrecursorPeaksRemoved());
        instance.getPrecursorPeaksRemovesExpMSnSpectrum();
        assertEquals(true, instance.arePrecursorPeaksRemoved());
        assertEquals(15, first_case_msms.getPeakList().size());
    }

    /**
     * Test of removeNotch method, of class MascotAdaptedPrecursorPeakRemoval.
     */
    @Test
    public void testRemoveNotch() {
        System.out.println("removeNotch");
        Peak p1 = new Peak(700.001, 100),
                p2 = new Peak(700.01, 100),
                p3 = new Peak(701.001, 100),
                p4 = new Peak(702.001, 100),
                p5 = new Peak(703.001, 100),
                p6 = new Peak(704.1, 100),
                p7 = new Peak(704.31, 100),
                p8 = new Peak(704.5, 100);
        ArrayList<Peak> expPeaks = new ArrayList<Peak>();
        expPeaks.add(p1);
        expPeaks.add(p2);
        expPeaks.add(p3);
        expPeaks.add(p4);
        expPeaks.add(p5);
        expPeaks.add(p6);
        expPeaks.add(p7);
        expPeaks.add(p8);
        ArrayList<Peak> result = MascotAdaptedPrecursorPeakRemoval.removeNotch(expPeaks, 699.10, 700.10, 0.1);
        assertEquals(2, result.size());

        result = MascotAdaptedPrecursorPeakRemoval.removeNotch(expPeaks, 701.00, 704.10, 0.1);
        assertEquals(4, result.size());
    }

}
