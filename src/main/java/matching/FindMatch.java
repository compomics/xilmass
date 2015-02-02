/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import scoringFunction.AndromedaScoring;
import scoringFunction.MSAmandaScoring;
import theoretical.CPeptideIon;
import theoretical.CPeptides;

/**
 *
 * This class find the matched peaks between an experimental and theoretical
 * spectrum
 *
 * @author Sule
 */
public class FindMatch {

    private MSnSpectrum expMS2; // experimental MS2 spectrum
    private ArrayList<CPeptideIon> theoCMS2ions; // theoretical ions for MS2 spectrum on cross linked peptide 
    private CPeptides cPeptides;
    private int charge;
    private ArrayList<Peak> matchedPeaks = new ArrayList<Peak>(); // found peak on an experimental MS2 spectrum with the same mz from theoCMS2ion
    private double fragTol; // fragment tolerance, to show mz interval 
    private double[] diffs = null;
    private int situation = 0; // 0-the closest, 1-the highest (like applying noisefilter before and selecting), 2-combination, weighting option
    private int scoring = 0; // 0-MSAmanda, 1-Andromeda


    /* Constructor */
    public FindMatch(MSnSpectrum expMS2, int scoring, CPeptides cPeptides, double fragTol, int charge) {
        this.scoring = scoring;
        this.expMS2 = expMS2;
        this.charge = charge;
        this.cPeptides = cPeptides;
        theoCMS2ions = cPeptides.getTheoterical_ions(charge);
        this.fragTol = fragTol;
        diffs = new double[theoCMS2ions.size()];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = Double.MIN_VALUE;
        }
    }

    /* getters and setters */
    public MSnSpectrum getExpMS2() {
        return expMS2;
    }

    public ArrayList<CPeptideIon> getTheoCMS2ions() {
        return theoCMS2ions;
    }

    public CPeptides getcPeptides() {
        return cPeptides;
    }

    public void setcPeptides(CPeptides cPeptides) {
        this.cPeptides = cPeptides;
    }

    public void setTheoCMS2ions(ArrayList<CPeptideIon> theoCMS2ions) {
        this.theoCMS2ions = theoCMS2ions;
    }

    public double getFragTol() {
        return fragTol;
    }

    public int getSituation() {
        return situation;
    }

    public void setSituation(int situation) {
        this.situation = situation;
    }

    public int getScoring() {
        return scoring;
    }

    public void setScoring(int scoring) {
        this.scoring = scoring;
    }

    public double getPSMscore() {
        double score = 0;
        switch (situation) {
            case (0):
                return (score = getPSMScore(scoring));
            case 1:
                // to do for highest intensity.. return! Maybe.. For different scoring..
                break;
            case 2:
                // to do for weigthining.. return! Maybe.. For different scoring..
                break;
        }
        return score;
    }

    /**
     * This method find an experimental peaks which are closed to a theoretical
     * peak TODO: change fragment tolerance to ppm as well!
     *
     * @return
     */
    private double getPSMScore(int scoring) {
        int startIndexTheo = 0,
                totalN = getTheoCMS2ions().size();
        ArrayList<Double> scores = new ArrayList<Double>();
        for (int numHighestPeak = 1; numHighestPeak < 11; numHighestPeak++) {
            Filtering f = new Filtering(expMS2, numHighestPeak);
            ArrayList<Peak> finalCPeaks = f.getFinalCPeaks();
            double probability = (double) numHighestPeak / (double) 100;
            int n = 0;
            double intensities = 0,
                    explainedIntensities = 0;
            for (int i = startIndexTheo; i < theoCMS2ions.size(); i++) {
                double theoMz = theoCMS2ions.get(i).get_theoretical_mz(charge);
                double diff = fragTol; // Based on Da.. not ppm...
                for (Peak cpeak : finalCPeaks) {
                    double tmpMz = cpeak.getMz(),
                            tmpIntensity = cpeak.getIntensity();
                    intensities += tmpIntensity;
                    double tmp_diff = (tmpMz - theoMz);
                    if (Math.abs(tmp_diff) < diff) {
                        diff = Math.abs(tmp_diff);
                        n++;
                        explainedIntensities += tmpIntensity;
                    } else if (tmp_diff == diff) {
                        // so this experimental mz is indeed in the middle
                        // So, just the one on the left side is being chosen..
                    }
                    // stop here and leave the loop
                    if (tmp_diff > fragTol && Math.abs(tmp_diff) > diff) {
                        startIndexTheo = i;
                        break;
                    }
                }
            }
            if (scoring == 0) {
                MSAmandaScoring object = new MSAmandaScoring(probability, totalN, n, intensities, explainedIntensities);
                double score = object.getScore();
                scores.add(score);

            } else if (scoring == 1) {
                AndromedaScoring object = new AndromedaScoring(probability, totalN, n);
                double score = object.getScore();
                scores.add(score);
            }
        }
        double finalScore = Collections.max(scores);
        return finalScore;
    }

      
}
