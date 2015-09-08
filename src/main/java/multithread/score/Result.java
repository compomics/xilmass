/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.score;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import naming.DefineIdCPeptideFragmentationPattern;
import naming.IdCPeptideFragmentationPatternName;
import scoringFunction.ScoreName;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.CrossLinkedPeptides;

/**
 * This method keeps results spectrum-cross linked peptide score calculation via
 * multi-threading
 *
 * @author Sule
 */
public class Result {

    private MSnSpectrum msms; // a selected MSnSpectrum object
    private CrossLinkedPeptides cp; // a cross linked peptide object
    private double score; // score how likely that cross linked peptide spectra looks like MSnSpectrum object
    private ScoreName scoreName; // a name of scoring function
    private HashSet<Peak> matchedPeaks; // list of matched peaks on an experimental spectrum 
    private HashSet<CPeptidePeak> matchedCTheoPeaks; // list of theoretical peaks matched on a theoretical spectrum
    private double ionFrac, // ionFrac for scoring...
            lnNumSpec, // natural logarithm of number of matched peptides on DB for a selected MSnSpectrum
            deltaScore, // difference in score between the best ranked score and next best match for a selected MSnSpectrum
            ionFracA, // fraction of found theoretical ion over all theoretical ions for PeptideAlpha
            ionFracB, // fraction of found theoretical ion over all theoretical ions for PeptideBeta
            observedMass, // singly charged precursor ion of a selected MSnSpectrum
            deltaMass, // difference in mass between the calculated and observed spectra in ppm
            absDeltaMass; // absolute difference in mass between the calculated and observed spectra in ppm
    private int matchedTheoA, // matched theoretical peaks from peptideA
            matchedTheoB; // matched theoretical peaks from peptideB
    private boolean doesContainCPeptidePattern,
            doesContainIonFract;
    private String scanNum = "-",
            charge = "";

    /**
     * @param msms MSnSpectrum object
     * @param cp CPeptides
     * @param scoreName which scoring function
     * @param score calculated score
     * @param deltaScore
     * @param matchedPeaks matched experimental peaks
     * @param matchedCTheoPeaks matched theoretical peaks
     * @param weight
     * @param ionFracA
     * @param ionFacB
     * @param observedMass
     * @param deltaMass
     * @param absDeltaMass
     * @param lnNumSpec
     * @param matchedTheoA matched theoretical peaks from peptideA
     * @param matchedTheoB matched theoretical peaks from peptideB
     * @param doesContainCPeptidePattern true: there is a CPeptidePattern
     * @param doesContainIonFrac - true: there is ion fraction
     */
    public Result(MSnSpectrum msms, CrossLinkedPeptides cp, ScoreName scoreName, double score, double deltaScore, HashSet<Peak> matchedPeaks, HashSet<CPeptidePeak> matchedCTheoPeaks,
            double weight, double ionFracA, double ionFacB, double observedMass, double deltaMass, double absDeltaMass, double lnNumSpec, int matchedTheoA, int matchedTheoB,
            boolean doesContainCPeptidePattern, boolean doesContainIonFrac) {
        this.msms = msms;
        this.cp = cp;
        this.score = score;
        this.deltaScore = deltaScore;
        this.lnNumSpec = lnNumSpec;
        this.scoreName = scoreName;
        this.matchedPeaks = matchedPeaks;
        this.matchedCTheoPeaks = matchedCTheoPeaks;
        this.ionFrac = weight;
        this.ionFracA = ionFracA;
        this.ionFracB = ionFacB;
        this.observedMass = observedMass;
        this.deltaMass = deltaMass;
        this.absDeltaMass = absDeltaMass;
        this.matchedTheoA = matchedTheoA;
        this.matchedTheoB = matchedTheoB;
        this.doesContainCPeptidePattern = doesContainCPeptidePattern;
        this.doesContainIonFract = doesContainIonFrac;
        if (!msms.getScanNumber().isEmpty()) {
            scanNum = msms.getScanNumber();
        } else if (msms.getSpectrumTitle().contains("scan")) {
            scanNum = msms.getSpectrumTitle().substring(msms.getSpectrumTitle().indexOf("scan=") + 5, msms.getSpectrumTitle().length() - 1);
        }
        charge = msms.getPrecursor().getPossibleChargesAsString().replace("+", "");
    }

    /* Getter and setter method for Result information */
    public MSnSpectrum getMsms() {
        return msms;
    }

    public void setMsms(MSnSpectrum msms) {
        this.msms = msms;
    }

    public String getScanNum() {
        return scanNum;
    }

    public String getCharge() {
        return charge;
    }

    public double getWeight() {
        return ionFrac;
    }

    public void setWeight(double weight) {
        this.ionFrac = weight;
    }

    public CrossLinkedPeptides getCp() {
        return cp;
    }

    public void setCp(CrossLinkedPeptides cp) {
        this.cp = cp;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ScoreName getScoreName() {
        return scoreName;
    }

    public void setScoreName(ScoreName scoreName) {
        this.scoreName = scoreName;
    }

    public HashSet<Peak> getMatchedPeaks() {
        return matchedPeaks;
    }

    public void setMatchedPeaks(HashSet<Peak> matchedPeaks) {
        this.matchedPeaks = matchedPeaks;
    }

    public HashSet<CPeptidePeak> getMatchedCTheoPeaks() {
        return matchedCTheoPeaks;
    }

    public void setMatchedCTheoPeaks(HashSet<CPeptidePeak> matchedCTheoPeaks) {
        this.matchedCTheoPeaks = matchedCTheoPeaks;
    }

    public int getMatchedTheoA() {
        return matchedTheoA;
    }

    public void setMatchedTheoA(int matchedTheoA) {
        this.matchedTheoA = matchedTheoA;
    }

    public int getMatchedTheoB() {
        return matchedTheoB;
    }

    public void setMatchedTheoB(int matchedTheoB) {
        this.matchedTheoB = matchedTheoB;
    }

    public double getLnNumSpec() {
        return lnNumSpec;
    }

    public void setLnNumSpec(double lnNumSpec) {
        this.lnNumSpec = lnNumSpec;
    }

    public double getDeltaScore() {
        return deltaScore;
    }

    public void setDeltaScore(double deltaScore) {
        this.deltaScore = deltaScore;
    }

    public double getIonFracA() {
        return ionFracA;
    }

    public void setIonFracA(double ionFracA) {
        this.ionFracA = ionFracA;
    }

    public double getIonFracB() {
        return ionFracB;
    }

    public void setIonFracB(double ionFracB) {
        this.ionFracB = ionFracB;
    }

    public double getObservedMass() {
        return observedMass;
    }

    public void setObservedMass(double observedMass) {
        this.observedMass = observedMass;
    }

    public double getDeltaMass() {
        return deltaMass;
    }

    public void setDeltaMass(double deltaMass) {
        this.deltaMass = deltaMass;
    }

    public double getAbsDeltaMass() {
        return absDeltaMass;
    }

    public void setAbsDeltaMass(double absDeltaMass) {
        this.absDeltaMass = absDeltaMass;
    }

    public boolean doesKeepPattern() {
        return doesContainCPeptidePattern;
    }

    public void setDoesKeepPattern(boolean doesKeepPattern) {
        this.doesContainCPeptidePattern = doesKeepPattern;
    }

    public String toPrint() {
        String specTitle = msms.getSpectrumTitle();
        double rt = msms.getPrecursor().getRt();
        // Sort them to write down on a result file
        ArrayList<Peak> matchedPLists = new ArrayList<Peak>(matchedPeaks);
        Collections.sort(matchedPLists, Peak.ASC_mz_order);
        ArrayList<CPeptidePeak> matchedCTheoPLists = new ArrayList<CPeptidePeak>(matchedCTheoPeaks);
        Collections.sort(matchedCTheoPLists, CPeptidePeak.Peak_ASC_mz_order);

        String result = msms.getFileName() + "\t" + specTitle + "\t" + scanNum + "\t" + rt + "\t"
                + observedMass + "\t" + charge + "\t" + deltaMass + "\t" + absDeltaMass + "\t"
                + cp.toPrint() + "\t"
                + score + "\t"
                // + deltaScore + "\t" 
                + scoreName + "\t"
                + lnNumSpec + "\t"
                + matchedPLists.size() + "\t" + matchedCTheoPLists.size() + "\t"
                + matchedTheoA + "\t" + matchedTheoB + "\t"
                + ionFracA + "\t" + ionFracB + "\t"
                + printPeaks(matchedPLists) + "\t"
                + printCPeaks(matchedCTheoPLists);

        if (cp instanceof CPeptides) {
            boolean isHeavyLabel = cp.getLinker().isIsLabeled();
            if (isHeavyLabel) {
                result += "\t" + "Heavy_Labeled_Linker";
            } else {
                result += "\t" + "Light_Labeled_Linker";
            }
        } else {
            result += "\t" + "-";
        }
        if (doesContainCPeptidePattern) {
            IdCPeptideFragmentationPatternName name = null;
            if (cp instanceof CPeptides) {
                CPeptides tmp = (CPeptides) cp;
                int linkerPositionPeptideA = tmp.getLinker_position_on_peptideA(),
                        linkerPositionPeptideB = tmp.getLinker_position_on_peptideB(),
                        peptideALen = tmp.getPeptideA().getSequence().length(),
                        peptideBLen = tmp.getPeptideB().getSequence().length();
                DefineIdCPeptideFragmentationPattern p = new DefineIdCPeptideFragmentationPattern(matchedCTheoPLists,
                        linkerPositionPeptideA, linkerPositionPeptideB,
                        peptideALen, peptideBLen);
                name = p.getName();
            } 
            result += "\t" + name;
        }
        if (doesContainIonFract) {
            result += "\t" + ionFrac;
        }
        return result;
    }

    private String printPeaks(ArrayList<Peak> matchedPeaks) {
        String info = "[";
        for (Peak p : matchedPeaks) {
            info += "mz=" + p.mz + "_intensity=" + p.intensity + ", ";
        }
        if (info.length() > 1) {
            info = info.substring(0, info.length() - 2) + "]";
        } else {
            info = "[]";
        }

        return info;
    }

    private String printCPeaks(ArrayList<CPeptidePeak> matchedPeaks) {
        String info = "[";
        for (CPeptidePeak p : matchedPeaks) {
            info += p.toString() + ", ";
        }
        if (info.length() > 1) {
            info = info.substring(0, info.length() - 2) + "]";
        } else {
            info = "[]";
        }

        return info;
    }

    /**
     * To compare Results with a score in a descending order
     */
    public static final Comparator<Result> ScoreDESC
            = new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return o2.getScore() < o1.getScore() ? -1 : o2.getScore() == o1.getScore() ? 0 : 1;
                }
            };

}
