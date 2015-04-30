/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class calculates cumulative binominal probability based scores with
 * considering intensities from experimental spectra to
 * cumulativeBinomialProbability these.
 *
 * n: number of matched peaks
 *
 * N: number of theoretical peaks
 *
 * p: probability,topN/windowSize from a Filter object. Note that topN is [1-10]
 *
 * Note that cumulative binominal probability function calculates the score as
 * inclusive (not exclusive)
 *
 *
 * For each filtered peakList with a given topN parameter, p is calculated as
 * explained and cumulative binominal probability based scoring function part is
 * calculated as:
 *
 * Probability_Part (PP) = -10*[-log(P)]
 *
 *
 * @author Sule
 */
public class Andromeda_derived extends CumulativeBinomialProbabilityBasedScoring {

    public Andromeda_derived(double p, int N, int n) {
        super.p = p; // probability=m/windowSize (windowSize=100Da default)m=[1- 10] peaks
        super.N = N; // N: All theoretical peaks at a theoretical spectrum (on Andromeda_derived)
        super.n = n; // n: Matched peaks is number of matched peaks on theoretical spectrum  
    }
   

    @Override
    protected void calculateScore() {
        try {
            double probability_based_score = super.calculateCumulativeBinominalProbability();
            score = - 10 * (Math.log10(probability_based_score));
            isCalculated = true;
        } catch (Exception ex) {
            Logger.getLogger(Andromeda_derived.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
