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
import java.util.HashSet;
import scoringFunction.Andromeda;
import scoringFunction.MSRobin;
import theoretical.CPeptideIon;
import theoretical.CPeptides;

/**
 *
 * This class finds matched peaks between an experimental and theoretical
 * spectrum. Then, it calculates a score!
 *
 * @author Sule
 */
public class FindMatch {

    private MSnSpectrum expMS2; // experimental MS2 spectrum
    private ArrayList<CPeptideIon> theoCMS2ions; // theoretical ions for MS2 spectrum on cross linked peptide 
    private CPeptides cPeptides;
    private int charge;
    private HashSet<Peak> matchedPeaks = new HashSet<Peak>(); // found peak on an experimental MS2 spectrum with the same mz from theoCMS2ion
    private double fragTol; // fragment tolerance, to show mz interval 
//    private int situation = 0; // 0-the closest, 1-the highest (like applying noisefilter before and selecting), 2-combination, weighting option
    private int scoring = 0; // 0-MSRobin (MSRobin with N=AllPickedPeaks), 1-Andromeda, 2-TheoMSRobin (MSRobin with N=AllTheoPeaks)
    private int intensity_option = 0;

    /* Constructor */
    public FindMatch(MSnSpectrum expMS2, int scoring, CPeptides cPeptides, double fragTol, int charge, int intensity_option) {
        this.scoring = scoring;
        this.expMS2 = expMS2;
        this.charge = charge;
        this.cPeptides = cPeptides;
        if (cPeptides != null) {
            theoCMS2ions = cPeptides.getTheoterical_ions(charge);
        }
        this.fragTol = fragTol;
        this.intensity_option = intensity_option;
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
        setTheoCMS2ions(cPeptides.getTheoterical_ions(charge));
    }

    public void setTheoCMS2ions(ArrayList<CPeptideIon> theoCMS2ions) {
        this.theoCMS2ions = theoCMS2ions;
    }

    public double getFragTol() {
        return fragTol;
    }

    public int getScoring() {
        return scoring;
    }

    public void setScoring(int scoring) {
        this.scoring = scoring;
    }

//    public double getPSMscore() {
//        double score = 0;
//        switch (situation) {
//            case (0):
//                return (score = getPSMScore());
//            case 1:
//                // to do for highest intensity.. return! Maybe.. For different scoring..
//                break;
//            case 2:
//                // to do for weigthining.. return! Maybe.. For different scoring..
//                break;
//        }
//        return score;
//    }
    /**
     * This method find an experimental peaks which are closed to a theoretical
     * peak TODO: change fragment tolerance to ppm as well!
     *
     * @return
     */
    public double getPSMScore() {
        int totalN = getTheoCMS2ions().size();
        ArrayList<Double> scores = new ArrayList<Double>();
        for (int numHighestPeak = 1; numHighestPeak < 11; numHighestPeak++) {
            matchedPeaks = new HashSet<Peak>();
            Filter filter = new Filter(expMS2, numHighestPeak);
            ArrayList<Peak> filteredPeaks = filter.getFilteredCPeaks();
//            System.out.println("FilteredSpec size="+filteredPeaks.size());
            double probability = (double) numHighestPeak / (double) (filter.getWindowSize());
            int n = 0;
            double intensities = 0,
                    explainedIntensities = 0;
            boolean are_intensities_ready = false;
            for (int i = 0; i < theoCMS2ions.size(); i++) {
                double theoMz = theoCMS2ions.get(i).get_theoretical_mz(charge);
                double diff = fragTol; // Based on Da.. not ppm...
                Peak matchedPeak = null;
                for (Peak p : filteredPeaks) {
                    double tmpMz = p.getMz(),
                            tmpIntensity = p.getIntensity();
                    if (!are_intensities_ready) {
                        intensities += tmpIntensity;
                    }
                    double tmp_diff = (tmpMz - theoMz);
                    if (Math.abs(tmp_diff) < diff) {
                        diff = Math.abs(tmp_diff);
                        matchedPeak = p;
                    } else if (tmp_diff == diff) {
                        // so this experimental mz is indeed in the middle
                        // So, just the one on the left side is being chosen..
                    }
                }
                are_intensities_ready = true;
                if (matchedPeak != null) {
                    matchedPeaks.add(matchedPeak);
//                    System.out.println("\t \t"+ matchedPeak.mz+"\t"+matchedPeak.intensity);
                    explainedIntensities += matchedPeak.intensity;
                }
            }
            n = matchedPeaks.size();
//            System.out.println("MatchedPeaks="+n);
            // MSRobin with expertimentatl spectrum
            if (scoring == 0) {
                MSRobin object = new MSRobin(probability, filter.getFilteredCPeaks().size(), n, intensities, explainedIntensities, intensity_option);
                double score = object.getScore();
                scores.add(score);
                // Andromeda with theoretical spectra size
            } else if (scoring == 1) {
                Andromeda object = new Andromeda(probability, totalN, n);
                double score = object.getScore();
                scores.add(score);
                // MSRobin with theoretical spectra size
            } else if (scoring == 2) {
                MSRobin object = new MSRobin(probability, totalN, n, intensities, explainedIntensities, intensity_option);
                double score = object.getScore();
                scores.add(score);
            }
        }
        double finalScore = Collections.max(scores);
        return finalScore;
    }

    public HashSet<Peak> getMatchedPeaks() {
        if (matchedPeaks.isEmpty()) {
            getPSMScore();
        }
        return matchedPeaks;
    }

}
