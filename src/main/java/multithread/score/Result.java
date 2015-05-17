/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.score;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.HashSet;
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

    /**
     *
     * @param msms MSnSpectrum object
     * @param cp CPeptides
     * @param scoreName which scoring function
     * @param score calculated score
     * @param matchedPeaks matched experimental peaks
     * @param matchedCTheoPeaks matched theoretical peaks
     */
    public Result(MSnSpectrum msms, CrossLinkedPeptides cp, ScoreName scoreName, double score, HashSet<Peak> matchedPeaks, HashSet<CPeptidePeak> matchedCTheoPeaks) {
        this.msms = msms;
        this.cp = cp;
        this.score = score;
        this.scoreName = scoreName;
        this.matchedCTheoPeaks = matchedCTheoPeaks;
        this.matchedPeaks = matchedPeaks;
    }

    /* Getter and setter method for Result information */
    public MSnSpectrum getMsms() {
        return msms;
    }

    public void setMsms(MSnSpectrum msms) {
        this.msms = msms;
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

}
