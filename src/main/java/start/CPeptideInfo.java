/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.identification.matches.ModificationMatch;
import java.util.ArrayList;
import theoretical.CPeptides;
import theoretical.CrossLinking;
import theoretical.CrossLinkingType;
import theoretical.MonoLinkedPeptides;

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
     * @param isReversed True; first B then A (in reversed order of peptide)
     * @return
     */
    public static StringBuilder getInfo(CrossLinking cPeptide, boolean isReversed) {
        if (cPeptide.getLinkingType() == CrossLinkingType.CROSSLINK) {
            CPeptides cp = (CPeptides) cPeptide;
            String proteinA = cp.getProteinA(),
                    proteinB = cp.getProteinB(),
                    peptideA = cp.getPeptideA().getSequence(),
                    peptideB = cp.getPeptideB().getSequence(),
                    fixedModPepA = getPTMName(cp.getPeptideA().getModificationMatches(), false),
                    fixedModPepB = getPTMName(cp.getPeptideB().getModificationMatches(), false),
                    varModPepA = getPTMName(cp.getPeptideA().getModificationMatches(), true),
                    varModPepB = getPTMName(cp.getPeptideB().getModificationMatches(), true);
            int linkerPosPepA = cp.getLinker_position_on_peptideA(),
                    linkerPosPepB = cp.getLinker_position_on_peptideB();
            double mass = cp.getTheoretical_xlinked_mass();
            StringBuilder sb = new StringBuilder(proteinA + "\t" + proteinB + "\t" + peptideA + "\t" + peptideB + "\t"
                    + linkerPosPepA + "\t" + linkerPosPepB + "\t" + fixedModPepA + "\t" + fixedModPepB + "\t" + varModPepA + "\t" + varModPepB + "\t" + mass + "\t" + "CROSSLINK");
            if(isReversed){
                sb = new StringBuilder(proteinB + "\t" + proteinA + "\t" + peptideB + "\t" + peptideA + "\t"
                    + linkerPosPepB + "\t" + linkerPosPepA + "\t" + fixedModPepB + "\t" + fixedModPepA + "\t" + varModPepB + "\t" + varModPepA + "\t" + mass + "\t" + "CROSSLINK");
            }            
            return sb;
        } else {
            MonoLinkedPeptides mp = (MonoLinkedPeptides) cPeptide;
            String proteinA = mp.getProtein(),
                    proteinB = "-",
                    peptideA = mp.getPeptide().getSequence(),
                    peptideB = "-",
                    fixedModPepA = getPTMName(mp.getPeptide().getModificationMatches(), false),
                    fixedModPepB = "-",
                    varModPepA = getPTMName(mp.getPeptide().getModificationMatches(), true),
                    varModPepB = "-";
            int linkerPosPepA = mp.getLinker_position();
            double mass = cPeptide.getTheoretical_xlinked_mass();
            StringBuilder sb = new StringBuilder(proteinA + "\t" + proteinB + "\t" + peptideA + "\t" + peptideB + "\t"
                    + linkerPosPepA + "\t" + "-" + "\t" + fixedModPepA + "\t" + fixedModPepB + "\t" + varModPepA + "\t" + varModPepB + "\t" + mass);
            if (cPeptide.getLinkingType() == CrossLinkingType.MONOLINK) {
                sb.append("\t").append("MONOLINK");
            } else if (cPeptide.getLinkingType() == CrossLinkingType.CONTAMINANT) {
                sb.append("\t").append("CONTAMINANT");
            }
            return sb;
        }
    }

    /**
     * This method returns a PTM name from a given ModificationMatches from a
     * peptide
     *
     * @param ptms_peptide a list of Modification matches
     * @param isVariable true:Variable/false:Fixed
     * @return
     */
    public static String getPTMName(ArrayList<ModificationMatch> ptms_peptide, boolean isVariable) {
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
