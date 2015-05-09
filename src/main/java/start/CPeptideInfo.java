/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import theoretical.CPeptides;

/**
 *
 * @author Sule
 */
public class CPeptideInfo {

    /**
     * This method returns all required information to construct a CPeptides
     * object and also to write them on a cache file with their masses
     *
     * @param cPeptide
     * @return
     */
    public static StringBuilder getInfo(CPeptides cPeptide) {
        String proteinA = cPeptide.getProteinA(),
                proteinB = cPeptide.getProteinB(),
                peptideA = cPeptide.getPeptideA().getSequence(),
                peptideB = cPeptide.getPeptideB().getSequence(),
                fixedModPepA = getPTMName(cPeptide.getPeptideA().getModificationMatches(), false),
                fixedModPepB = getPTMName(cPeptide.getPeptideB().getModificationMatches(), false),
                varModPepA = getPTMName(cPeptide.getPeptideA().getModificationMatches(), true),
                varModPepB = getPTMName(cPeptide.getPeptideB().getModificationMatches(), true);
        int linkerPosPepA = cPeptide.getLinker_position_on_peptideA(),
                linkerPosPepB = cPeptide.getLinker_position_on_peptideB();
        double mass = cPeptide.getTheoreticalXLinkedMass();
        StringBuilder sb = new StringBuilder(proteinA + "\t" + proteinB + "\t" + peptideA + "\t" + peptideB + "\t"
                + linkerPosPepA + "\t" + linkerPosPepB + "\t" + fixedModPepA + "\t" + fixedModPepB + "\t" + varModPepA + "\t" + varModPepB + "\t" + mass);
        return sb;
    }
    
    
    /**
     * This method returns a PTM name from a given ModificationMatches from a
     * peptide
     *
     * @param ptms_peptide a list of Modification matches
     * @param isVariable true:Variable/false:Fixed
     * @return
     */
    private static String getPTMName(ArrayList<ModificationMatch> ptms_peptide, boolean isVariable) {
        String tmp_ptm = "";
        for (ModificationMatch m : ptms_peptide) {
            if (m.isVariable() == isVariable) {
                String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                tmp_ptm += tmp;
            } else {
                if (m.isVariable() == isVariable) {
                    String tmp = "[" + m.getTheoreticPtm() + "_" + m.getModificationSite() + "]";
                    tmp_ptm += tmp;
                }
            }
        }
        return tmp_ptm;
    }

}
