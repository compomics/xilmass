/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naming;

import java.util.ArrayList;
import theoretical.CPeptidePeak;

/**
 * This class is used to have an idea on how fragmentation pattern occurs on a
 * cross-linked peptide
 *
 * @author Sule
 */
public class DefineIdCPeptideFragmentationPattern {

    private ArrayList<CPeptidePeak> matchedCPepPeaks; // a given list containing found CPeptides
    private IdCPeptideFragmentationPatternName name;
    private int linkerPositionOnPepA,
            linkerPositionOnPepB,
            pepALen, // length of peptideA
            pepBLen, // length of peptideB
            ions_PepA_Left = 0, // number of found ions derived from PeptideB, left from linked position.. 
            ions_PepB_Left = 0, // number of found ions derived from PeptideB, left from linked position.. 
            ions_PepA_Right = 0, // number of found ions derived from PeptideB, right from linked position.. 
            ions_PepB_Right = 0, // number of found ions derived from PeptideB, right from linked position.. 
            ions_PepA_Node = 0,
            ions_PepB_Node = 0;
    private boolean isMonoLinked = false;

    /**
     * To construct a object to get name of fragmentation pattern
     *
     * @param matchedCPepPeaks a list of found CPeptidePeaks
     * @param linkerPositionOnPepA a linker position on PeptideA (start=1)
     * @param linkerPositionOnPepB a linker position on PeptideB (start=1)
     * @param pepALen length of PeptideA
     * @param pepBLen length of PeptideB
     */
    public DefineIdCPeptideFragmentationPattern(ArrayList<CPeptidePeak> matchedCPepPeaks, int linkerPositionOnPepA, int linkerPositionOnPepB, int pepALen, int pepBLen) {
        this.matchedCPepPeaks = matchedCPepPeaks;
        this.linkerPositionOnPepA = linkerPositionOnPepA;
        this.linkerPositionOnPepB = linkerPositionOnPepB;
        this.pepALen = pepALen;
        this.pepBLen = pepBLen;
    }

    /**
     * To return a list of matched CPeptidePeaks
     *
     * @return
     */
    public ArrayList<CPeptidePeak> getMatchedCPepPeaks() {
        return matchedCPepPeaks;
    }

    /**
     * To return a name defining fragmentation pattern
     *
     * @return
     */
    public IdCPeptideFragmentationPatternName getName() {
        if (name == null) {
            name();
        }
        return name;
    }

    /**
     * This method checks each assigned CPeptidePeak to find their status
     * (coming from which part of Peptide/right or left)
     *
     */
    private void name() {
        for (CPeptidePeak tmpCPPeak : matchedCPepPeaks) {
            String tmpPeakName = tmpCPPeak.getName();
            checkStatus(tmpPeakName);
        }
        defineStatus();
    }

    @Override
    public String toString() {
        String toString = name + "_"
                + linkerPositionOnPepA + "_" + linkerPositionOnPepB + "_"
                + ions_PepA_Left + "_" + ions_PepA_Right + "_"
                + ions_PepB_Left + "_" + ions_PepB_Right;
        return toString;
    }

    /**
     * To define status.
     *
     */
    private void defineStatus() {
        // ions only comes from PeptideA (either the left of the right or both does not matter) but it must not be monolinked
        if ((ions_PepA_Right > 0 || ions_PepA_Left > 0 || ions_PepA_Node > 0) && ((ions_PepB_Right + ions_PepB_Left) == 0) && !isMonoLinked) {
            name = IdCPeptideFragmentationPatternName.LINEAR_PEPA;
            // ions only from PeptideB (either the left of the right or both does not matter) but it must not be monolinked
        } else if ((ions_PepA_Right + ions_PepA_Left) == 0 && (ions_PepB_Right > 0 || ions_PepB_Left > 0 || ions_PepB_Node > 0) && !isMonoLinked) {
            name = IdCPeptideFragmentationPatternName.LINEAR_PEPB;
            // ions only from PeptideA (either the left of the right or both does not matter) and this time it MUST BE monolinked
        } else if ((ions_PepA_Right + ions_PepA_Left) > 0 && ((ions_PepB_Right + ions_PepB_Left) == 0) && isMonoLinked) {
            name = IdCPeptideFragmentationPatternName.MONOLINKED_PEPA;
            // ions only from PeptideA (either the left of the right or both does not matter) and this time it MUST BE monolinked
        } else if ((ions_PepA_Right + ions_PepA_Left) == 0 && ((ions_PepB_Right + ions_PepB_Left) > 0) && isMonoLinked) {
            name = IdCPeptideFragmentationPatternName.MONOLINKED_PEPB;
            // ions from the left side of PeptideA and the right side of PeptideB // None from the other arms..
        } else if (ions_PepA_Right == 0 && ions_PepA_Left > 0 && ions_PepB_Right > 0 && ions_PepB_Left == 0) {
            name = IdCPeptideFragmentationPatternName.LINEAR_NPEPA_CPEPB;
            // ions from the right side of PeptideA and the left side of PeptideB // None from the other arms...
        } else if (ions_PepA_Right > 0 && ions_PepA_Left == 0 && ions_PepB_Right == 0 && ions_PepB_Left > 0) {
            name = IdCPeptideFragmentationPatternName.LINEAR_NPEPB_CPEPA;
            // ions from ONLY the left side of both peptides, NONE from the RIGHT... 
        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && (ions_PepA_Right + ions_PepB_Right) == 0) {
            name = IdCPeptideFragmentationPatternName.LEFT_U;
            // ions from ONLY the RIGHT side of both peptides, NONE from the LEFT... 
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && (ions_PepA_Left + ions_PepB_Left) == 0) {
            name = IdCPeptideFragmentationPatternName.RIGHT_U;
            // ions from both Left side and right side of PeptideA
        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && ions_PepA_Right > 0 && ions_PepB_Right == 0) {
            name = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPA;
            // ions from both Left side and right side of PeptideB
        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && ions_PepA_Right == 0 && ions_PepB_Right > 0) {
            name = IdCPeptideFragmentationPatternName.LEFT_CHAIR_PEPB;
            // ions from both Right side and Left side of PeptideA
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left > 0 && ions_PepB_Left == 0) {
            name = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPA;
            // ions from both Right side and Left side of PeptideA
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left == 0 && ions_PepB_Left > 0) {
            name = IdCPeptideFragmentationPatternName.RIGHT_CHAIR_PEPB;
            // There are ions FROM EVERY ARMS
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left > 0 && ions_PepB_Left > 0) {
            name = IdCPeptideFragmentationPatternName.ALLOVER;
        } else if (ions_PepA_Right == 0 && ions_PepB_Right == 0 && ions_PepA_Left == 0 && ions_PepB_Left == 0 && ions_PepA_Node > 0 && ions_PepB_Node > 0) {
            name = IdCPeptideFragmentationPatternName.LINK;
        }
    }

    private void checkStatus(String tmpPeakName) {
        String[] split = tmpPeakName.split("_");
        // only ion coming from peptidebackbone
        // singlyCharged_pepB_a5
        if (split.length == 3 && split[1].startsWith("pep")) {
            String pep = split[1],
                    ion = split[2];
            int index = Integer.parseInt(ion.substring(1));
            String ionName = ion.substring(0, 1);
            check(pep, ionName, index);
        // ion coming from a peptidebackbone linked to another if contains lep
        // doublyCharged_pepA_b1_lepB_b2
        } else if (split.length == 5 && split[3].contains("lep")) {
            String lep = split[3],
                    ion = split[4],
                    pep = split[1],
                    ionP = split[2];
            Integer index = Integer.parseInt(ion.substring(1)),
                    indexP = Integer.parseInt(ionP.substring(1));
            String ionName = ion.substring(0, 1),
                    ionPName = ionP.substring(0, 1);
            check(pep, ionPName, indexP);
            check(lep, ionName, index);
        //doublyCharged_pepA_a3_lepB_monolink_a17
        //singlyCharged_pepA_b5_lepB_monolink_b3
        } else if (split.length == 6 && split[3].contains("lep") && split[4].equals("monolink")) {
            isMonoLinked = true;
            String pep = split[1],
                    ion = split[2];
            Integer index = Integer.parseInt(ion.substring(1));
            String ionName = ion.substring(0, 1);
            check(pep, ionName, index);
        } // two ions are collapsed into one - so consider as two peptides.. 
        // i.e. singlyCharged_pepA_y1_pepB_y1_mz=175.1189 
        else if (split.length == 5 && split[1].contains("pep") && split[2].contains("pep")) {

        } // this is exactly linked one..    
        //doublyCharged_pepA_a1_lepB_a9_pepB_a9_lepA_a1
        else if (split.length == 9 && (split[3].contains("lep")) && split[7].contains("lep")) {
            ions_PepA_Node++;
            ions_PepB_Node++;
        }
    }

    /**
     * This method count how many ions are on the left side/right side of both
     * peptides
     *
     * @param peptide
     * @param ionName
     * @param ionIndex
     */
    private void check(String peptide, String ionName, int ionIndex) {
        if ((peptide.equals("pepA") || peptide.equals("lepA"))
                && (((ionName.equals("x") || ionName.equals("y") || ionName.equals("z")))
                && (ionIndex < (pepALen - linkerPositionOnPepA + 1)))) {
            ions_PepA_Right++;
        } else if ((peptide.equals("pepA") || peptide.equals("lepA"))
                && (((ionName.equals("x") || ionName.equals("y") || ionName.equals("z")))
                && (ionIndex < (linkerPositionOnPepA)))) {
            ions_PepA_Left++;
        } else if ((peptide.equals("pepA") || peptide.equals("lepA"))
                && (((ionName.equals("a") || ionName.equals("b") || ionName.equals("c")))
                && (ionIndex < linkerPositionOnPepA))) {
            ions_PepA_Left++;
        } else if ((peptide.equals("pepA") || peptide.equals("lepA"))
                && (((ionName.equals("a") || ionName.equals("b") || ionName.equals("c")))
                && (ionIndex > linkerPositionOnPepA))) {
            ions_PepA_Right++;
        } else if ((peptide.equals("pepB") || peptide.equals("lepB"))
                && (((ionName.equals("x") || ionName.equals("y") || ionName.equals("z")))
                && (ionIndex < (pepBLen - linkerPositionOnPepB + 1)))) {
            ions_PepB_Right++;
        } else if ((peptide.equals("pepB") || peptide.equals("lepB"))
                && (((ionName.equals("x") || ionName.equals("y") || ionName.equals("z")))
                && (ionIndex < (linkerPositionOnPepB)))) {
            ions_PepB_Left++;
        } else if ((peptide.equals("pepB") || peptide.equals("lepB"))
                && (((ionName.equals("a") || ionName.equals("b") || ionName.equals("c")))
                && (ionIndex < linkerPositionOnPepB))) {
            ions_PepB_Left++;
        } else if ((peptide.equals("pepB") || peptide.equals("lepB"))
                && (((ionName.equals("a") || ionName.equals("b") || ionName.equals("c")))
                && (ionIndex > linkerPositionOnPepB))) {
            ions_PepB_Right++;
        } else if ((peptide.equals("pepA") || peptide.equals("lepA"))
                && (ionIndex == linkerPositionOnPepA)) {
            ions_PepA_Node++;
        } else if ((peptide.equals("pepB") || peptide.equals("lepB"))
                && (ionIndex == linkerPositionOnPepB)) {
            ions_PepB_Node++;
        }
    }

}
