/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import theoretical.CPeptideIon;

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
    private ArrayList<Peak> matchedPeaks = new ArrayList<Peak>(); // found peak on an experimental MS2 spectrum with the same mz from theoCMS2ion
    private double fragTol; // fragment tolerance, to show mz interval 
    private int charge = 1;
    double[] diffs = null;
    private int situation = 0; // 0-the closest, 1-the highest (like applying noisefilter before and selecting), 2-combination, weighting option


    /* Constructor */
    public FindMatch(MSnSpectrum expMS2, ArrayList<CPeptideIon> theoCMS2ions, double fragTol) {
        this.expMS2 = expMS2;
        this.theoCMS2ions = theoCMS2ions;
        this.fragTol = fragTol;
        diffs = new double[theoCMS2ions.size()];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = Double.MIN_VALUE;
        }
    }

    public FindMatch(MSnSpectrum expMS2, ArrayList<CPeptideIon> theoCMS2ions, double fragTol, int charge) {
        this.expMS2 = expMS2;
        this.theoCMS2ions = theoCMS2ions;
        this.fragTol = fragTol;
        this.charge = charge;
        diffs = new double[theoCMS2ions.size()];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = Double.MIN_VALUE;
        }
    }

    public FindMatch(int situation, MSnSpectrum expMS2, ArrayList<CPeptideIon> theoCMS2ions, double fragTol) {
        this.expMS2 = expMS2;
        this.theoCMS2ions = theoCMS2ions;
        this.fragTol = fragTol;
        diffs = new double[theoCMS2ions.size()];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = Double.MIN_VALUE;
        }
        this.situation = situation;
    }

    public FindMatch(int situation, MSnSpectrum expMS2, ArrayList<CPeptideIon> theoCMS2ions, double fragTol, int charge) {
        this.expMS2 = expMS2;
        this.theoCMS2ions = theoCMS2ions;
        this.fragTol = fragTol;
        this.charge = charge;
        diffs = new double[theoCMS2ions.size()];
        for (int i = 0; i < diffs.length; i++) {
            diffs[i] = Double.MIN_VALUE;
        }
        this.situation = situation;
    }

    /* getters and setters */
    public MSnSpectrum getExpMS2() {
        return expMS2;
    }

    public ArrayList<CPeptideIon> getTheoCMS2ions() {
        return theoCMS2ions;
    }

    public void setTheoCMS2ions(ArrayList<CPeptideIon> theoCMS2ions) {
        this.theoCMS2ions = theoCMS2ions;
    }

    public ArrayList<Peak> getMatchedPeak() {
        if (matchedPeaks.isEmpty()) {
            findMatchedPeak();
        }
        return matchedPeaks;
    }

    public double getFragTol() {
        return fragTol;
    }

    public void findMatchedPeak() {
        switch (situation) {
            case (0):
                findMatchedPeak_closest();
                break;
            case 1:
                // to do for highest intensity..
                break;
            case 2:
                // to do for weigthining..
                break;
        }
    }

    /**
     * This method find an experimental peaks which are closed to a theoretical peak 
     * 
     */
    public void findMatchedPeak_closest() {
        int startIndexTheo = 0;
        for (int i = startIndexTheo; i < theoCMS2ions.size(); i++) {
            System.out.println("startIndexTheo="+startIndexTheo);

            double theoMz = theoCMS2ions.get(i).get_theoretical_mz(charge);
            double tmpPrevTheo = diffs[i];

            Peak sharedPeak = null;
            double diff = 0.5,
                    fragTolerance = 0.5;

            for (Double mz : expMS2.getOrderedMzValues()) {
                Peak tmpPeak = expMS2.getPeakMap().get(mz);

                double tmp_diff = (mz - theoMz);
                if (Math.abs(tmp_diff) < diff) {
                    diff = Math.abs(tmp_diff);
                    sharedPeak = tmpPeak;
                  
                } else if (tmp_diff == diff) {
                    // so this experimental mz is indeed in the middle
                    // So, just the one on the left side is being chosen..
                }
                // stop here and leave the loop
                if (tmp_diff > fragTolerance && Math.abs(tmp_diff) > diff) {
                    startIndexTheo = i;
                    break;
                }
            }
            if (sharedPeak != null) {
                matchedPeaks.add(sharedPeak);
            }
        }

    }

}
