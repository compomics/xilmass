/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package naming;

import java.util.ArrayList;
import theoretical.CPeptidePeak;

/**
 *
 * @author Sule
 */
public class IdCPepName {

    private ArrayList<CPeptidePeak> matchedCPepPeaks;
    private IdCPepType name;
    private int linkerPositionOnPepA,// needs to start counting from 0 
            linkerPositionOnPepB, // needs to start counting from 0 
            pepALen,
            pepBLen,
            ions_PepA_Left = 0,
            ions_PepB_Left = 0,
            ions_PepA_Right = 0,
            ions_PepB_Right = 0;

    private boolean isMonoLinked = false;

    public IdCPepName(ArrayList<CPeptidePeak> matchedCPepPeaks, int linkerPositionOnPepA, int linkerPositionOnPepB, int pepALen, int pepBLen) {
        this.matchedCPepPeaks = matchedCPepPeaks;
        this.linkerPositionOnPepA = linkerPositionOnPepA;
        this.linkerPositionOnPepB = linkerPositionOnPepB;
        this.pepALen = pepALen;
        this.pepBLen = pepBLen;
    }

    public ArrayList<CPeptidePeak> getMatchedCPepPeaks() {
        if (name == null) {
            name();
        }
        return matchedCPepPeaks;
    }

    public void setMatchedCPepPeaks(ArrayList<CPeptidePeak> matchedCPepPeaks) {
        this.matchedCPepPeaks = matchedCPepPeaks;
        name();
    }

    public IdCPepType getName() {
        if (name == null) {
            name();
        }
        return name;
    }

    private void name() {
        for (CPeptidePeak tmpCPPeak : matchedCPepPeaks) {
            String tmpPeakName = tmpCPPeak.getName();
            checkStatus(tmpPeakName);
        }
        defineStatus();
    }

//    @Override
//    public String toString() {
//        String toString = linkerPositionOnPepA+"_"+linkerPositionOnPepB+"_"+
//        return "IdCPepName{" + "name=" + name + ", linkerPositionOnPepA=" + linkerPositionOnPepA + ", linkerPositionOnPepB=" + linkerPositionOnPepB + '}';
//    }
    private void defineStatus() {
        if ((ions_PepA_Right > 0 && ions_PepA_Left > 0) && ((ions_PepB_Right + ions_PepB_Left) == 0) && !isMonoLinked) {
            name = IdCPepType.LINEAR_PEPA;
        } else if ((ions_PepA_Right + ions_PepA_Left) == 0 && (ions_PepB_Right > 0 && ions_PepB_Left > 0) && !isMonoLinked) {
            name = IdCPepType.LINEAR_PEPB;

        } else if ((ions_PepA_Right + ions_PepA_Left) > 0 && ((ions_PepB_Right + ions_PepB_Left) == 0) && isMonoLinked) {
            name = IdCPepType.MONOLINKED_PEPA;
        } else if ((ions_PepA_Right + ions_PepA_Left) == 0 && ((ions_PepB_Right + ions_PepB_Left) > 0) && isMonoLinked) {
            name = IdCPepType.MONOLINKED_PEPB;

        } else if (ions_PepA_Right == 0 && ions_PepA_Left > 0 && ions_PepB_Right > 0 && ions_PepB_Left == 0) {
            name = IdCPepType.LINEAR_NPEPA_CPEPB;
        } else if (ions_PepA_Right > 0 && ions_PepA_Left == 0 && ions_PepB_Right == 0 && ions_PepB_Left > 0) {
            name = IdCPepType.LINEAR_NPEPB_CPEPA;

        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && (ions_PepA_Right + ions_PepB_Right) == 0) {
            name = IdCPepType.LEFT_U;
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && (ions_PepA_Left + ions_PepB_Left) == 0) {
            name = IdCPepType.RIGHT_U;

        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && ions_PepA_Right > 0 && ions_PepB_Right == 0) {
            name = IdCPepType.LEFT_CHAIR_PEPA;
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left > 0 && ions_PepB_Left == 0) {
            name = IdCPepType.RIGHT_CHAIR_PEPA;

        } else if (ions_PepA_Left > 0 && ions_PepB_Left > 0 && ions_PepA_Right == 0 && ions_PepB_Right > 0) {
            name = IdCPepType.LEFT_CHAIR_PEPB;
        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left == 0 && ions_PepB_Left > 0) {
            name = IdCPepType.RIGHT_CHAIR_PEPB;

        } else if (ions_PepA_Right > 0 && ions_PepB_Right > 0 && ions_PepA_Left > 0 && ions_PepB_Left > 0) {
            name = IdCPepType.INTACT;
        }
    }

    private void checkStatus(String tmpPeakName) {
        String[] split = tmpPeakName.split("_");
        // only ion coming from peptidebackbone
        if (split.length == 4) {
            String pep = split[1],
                    ion = split[2];
            int index = Integer.parseInt(ion.substring(1));
            String ionName = ion.substring(0, 1);
            check(pep, ionName, index);
        } // ion coming from a peptidebackbone linked to another if contains lep
        else if (split.length == 6 && split[3].contains("lep")) {
            String lep = split[3],
                    ion = split[4];
            Integer index = Integer.parseInt(ion.substring(1));
            String ionName = ion.substring(0, 1);
            check(lep, ionName, index);
        } // two ions are collapsed into one - so consider as two peptides.. THINK MORE TRICKY CASES????
        // i.e. singlyCharged_pepA_y1_pepB_y1_mz=175.1189 
        else if (split.length == 6 && split[1].contains("pep") && split[2].contains("pep")) {

        } // this is monolink
        else if (split.length == 7) {
            isMonoLinked = true;
        } // this is exactly linked one..         
        else if (split.length == 10 && (split[3].contains("lep")) && split[7].contains("lep")) {

        }
    }

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
        }
    }

}
