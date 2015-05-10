/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import theoretical.LinkedPeptideFragmentIon;

/**
 *
 * @author Sule
 */
public class TestingLinkedPeptides {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String peptide_alpha_str = "MLSDAM";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide linkedPeptide = new Peptide(peptide_alpha_str, modifications_test);

//        LinkedPeptideFragmentIon obj = new LinkedPeptideFragmentIon(linkedPeptide, 3);
//                
//        obj.getNTerminiMasses(PeptideFragmentIon.B_ION);
//        obj.getCTerminiMasses(PeptideFragmentIon.Y_ION);

    }

}
