/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class Anything {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {

        String mgfs = "C:\\Users\\Sule\\Desktop\\masa_samples\\mgfs";
        int total = 0;
        for (File mgf : new File(mgfs).listFiles()) {

            if (mgf.getName().endsWith(".mgf")) {
                System.out.print(mgf.getName());
                int spectra = 0;
                // now check all spectra to collect all required calculations...
                SpectrumFactory fct = SpectrumFactory.getInstance();
                if (mgf.getName().endsWith("mgf")) {
                    fct.addSpectra(mgf);
                    for (String title : fct.getSpectrumTitles(mgf.getName())) {
                        MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                        if (!ms.getPeakList().isEmpty()) {
                            spectra++;
                            total++;
                        }
                    }
                }
                System.out.print("\t " + spectra +"\n");
            }
        }
        System.out.println("Total spectra" + "\t" + total);
    }
}
