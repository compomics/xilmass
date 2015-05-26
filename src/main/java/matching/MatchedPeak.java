/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.Peak;
import theoretical.CPeptidePeak;

/**
 * This class holds information for a matched peak while selecting number of
 * peaks
 *
 * @author Sule
 */
public class MatchedPeak {

    private Peak matchedPeak;
    private CPeptidePeak matchedXLPeak;
    private double diff;

    public MatchedPeak(Peak matchedPeak, CPeptidePeak matchedXLPeak, double diff) {
        this.matchedPeak = matchedPeak;
        this.matchedXLPeak = matchedXLPeak;
        this.diff = diff;
    }

    public Peak getMatchedPeak() {
        return matchedPeak;
    }

    public void setMatchedPeak(Peak matchedPeak) {
        this.matchedPeak = matchedPeak;
    }

    public CPeptidePeak getMatchedXLPeak() {
        return matchedXLPeak;
    }

    public void setMatchedXLPeak(CPeptidePeak matchedXLPeak) {
        this.matchedXLPeak = matchedXLPeak;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

}
