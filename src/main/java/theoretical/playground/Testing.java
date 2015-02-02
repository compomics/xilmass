/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical.playground;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;
import start.GetFixedPTM;
import theoretical.CPeptideIon;
import theoretical.CPeptides;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws XmlPullParserException, IOException {
        // PeptideSequence
        String peptide_alpha_str = "MLCSDA";

        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);

        // Getting one fixed PTMs
        PTM testPTM = ptmFactory.getPTM("carbamidomethyl c");
        String theoreticPTM = testPTM.getName();

        // Generate ModificationMatch Arraylist to construct a Peptide. 
        ModificationMatch m = new ModificationMatch(theoreticPTM, false, peptide_alpha_str.indexOf("C") + 1);
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        modifications_test.add(m);

        // Construct a Peptide object with only this filled arraylist - not working
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, modifications_test);
        double mass = peptide_alpha.getMass();
        System.out.println("mas=" + mass);
        GetFixedPTM.getPTM(ptmFactory, theoreticPTM, peptide_alpha_str);

        // Construct a Peptide object the same PTM with this filled arraylist and then add modification - working
        Peptide peptide_alpha2 = new Peptide(peptide_alpha_str, modifications_test);
        peptide_alpha2.addModificationMatch(m);
        mass = peptide_alpha2.getMass();
        System.out.println("mas=" + mass);
//
//        // Construct a Peptide object the same PTM with an empty arraylist and then add modification - working
//        Peptide peptide_alpha3 = new Peptide(peptide_alpha_str, new ArrayList<ModificationMatch>());
//        peptide_alpha3.addModificationMatch(m);
//        mass = peptide_alpha3.getMass();
//        System.out.println("mas=" + mass);

    }
    /*
     Peptide      peptide_beta = new Peptide(peptide_beta_str, modifications_test);
     CrossLinker linker = new DSS();
     CPeptides o = new CPeptides(peptide_alpha, peptide_beta, linker, 3, 1, FragmentationMode.CID, 1);
     System.out.println(o.getTheoretical_mass());
        
                
     ArrayList<CPeptideIon> theoterical_ions = o.getTheoterical_ions();
     System.out.println("Mass \t Mz+1 \t  Mz+2 \t Intensity");
     for (CPeptideIon ion : theoterical_ions) {
     System.out.println(ion.getMass() + "\t" + ion.get_theoretical_mz(1) + "\t" +ion.get_theoretical_mz(2)+"\t" + ion.getIntensity());
     }
     //        String peptide_str = "MAGK";
     //        ArrayList<String> parent_proteins = new ArrayList<String>();
     //        parent_proteins.add("Pro1");
     //        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
     //        Peptide peptide = new Peptide(peptide_str, parent_proteins, modifications);
     //
     //        // Load the fragment factory
     //        IonFactory fragmentFactory = IonFactory.getInstance();
     //
     //        // estimate theoretic fragment ion masses of the desired peptide
     //        HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> fragments = fragmentFactory.getFragmentIons(peptide);
     //        System.out.println("key" + "\t" + "key_2" + "\t" + "i.getName()" + "\t" + "i.getTheoreticMass()" + "\t" + "i.getTheoreticMz(3)" + "\t" + "i.getType()");
     //
     //        for (Integer key : fragments.keySet()) {
     //            HashMap<Integer, ArrayList<Ion>> get = fragments.get(key);
     //            for (Integer key_2 : get.keySet()) {
     //                ArrayList<Ion> get1 = get.get(key_2);
     //                for (Ion i : get1) {
     ////                    i.getTheoreticMz(chargeValue)
     //                    System.out.println(key + "\t" + key_2 + "\t" + i.getName() + "\t" + i.getTheoreticMass() + "\t" + i.getTheoreticMz(1) + "\t" + i.getType());
     //                }
     //            }
     //        }
     //        // Create a spectrum annotator
     //        SpectrumAnnotator spectrumAnnotator =  SpectrumAnnotator();
     //
     //        // Annotate the spectrum with the desired m/z tolerance (mzTolerance)
     //        // and with the desired minimal peak intensity (intensityMin)
     //        spectrumAnnotator.annotateSpectrum(peptide, spectrum, mzTolerance, intensityMin);    
     /*
     // inputs
     String peptide_str1 = "LYMLSDAEDK",
     peptide_str2 = "KVIKNVAEVK";
     int cross_linker_pep1 = 5,
     cross_linker_pep2 = 3;
     ArrayList<String> parent_proteins_1 = new ArrayList<String>(),
     parent_proteins_2 = new ArrayList<String>();
     parent_proteins_1.add("Pro1");
     parent_proteins_2.add("Pro2");
     ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
     Peptide peptide_1 = new Peptide(peptide_str1, parent_proteins_1, modifications),
     peptide_2 = new Peptide(peptide_str2, parent_proteins_2, modifications);
     // Load the fragment factory
     IonFactory instance = IonFactory.getInstance();
     // estimate theoretic fragment ion masses of the desired peptide
     FragmentFac
     HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> fragments = fragmentFactory.getFragmentIons(peptide_1);
     //        // Create a spectrum annotator
     SpectrumAnnotator spectrumAnnotator =  new PeptideSpectrumAnnotator();
     spectrumAnnotator.getCurrentAnnotation(ms, fragments, neutral_losses, charges, true);
     //
     //        // Annotate the spectrum with the desired m/z tolerance (mzTolerance)
     //        // and with the desired minimal peak intensity (intensityMin)
     //        spectrumAnnotator.annotateSpectrum(peptide, spectrum, mzTolerance, intensityMin);
     }
     */
}
