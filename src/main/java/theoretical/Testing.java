/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.EDC;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         String peptide_alpha_str = "LYMLSDAE",
                 peptide_beta_str = "KVIKN";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptide_alpha = new Peptide(peptide_alpha_str, parent_proteins_test, modifications_test),
                         peptide_beta = new Peptide(peptide_beta_str, parent_proteins_test, modifications_test);

        CrossLinker linker = new EDC();
        
        CrossLinkedPeptides o = new CrossLinkedPeptides(peptide_alpha, peptide_beta,linker, 5,3, FragmentationMode.CID, 1);
        ArrayList<CrossLinkedPeptideIon> theoterical_ions = o.getTheoterical_ions();
        System.out.println("Mz \t Intensity");
        for(CrossLinkedPeptideIon ion: theoterical_ions){
            System.out.println(ion.getMz()+"\t"+ion.getIntensity());
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
}
