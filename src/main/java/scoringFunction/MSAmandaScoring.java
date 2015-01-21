/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

import java.util.logging.Level;
import java.util.logging.Logger;
import util.CMathUtil;

/**
 * This class calculates MSAmanda scoring for each n matched peaks from N picked
 * peaks with a given probability (which is indeed roughly m/100) The difference
 * between this class and Andromeda is that here intensities are also taken into
 * account
 *
 * @author Sule
 */
public class MSAmandaScoring extends BionominalProbabilityBasedScoring {

    private double intensity, // sum of all intensities from every picked peak
            explainedIntensity; // sum of all intensities from matched picked peak

    /**
     *
     * @param p probability=m/100 because they pick the top m peaks in a 100 Da
     * window. They pick [1- 10] peaks
     * @param N Picked peaks-is number of all peaks on an experimental spectrum
     * (on MSAmanda but this equals to all peaks at a theoretical spectrum (on
     * Andromeda)
     * @param n Matched peaks-is number of matched peaks against a theoretical
     * spectrum
     * @param intensity sum of all intensities from every picked peak
     * @param explainedIntensity sum of all intensities from matched picked peak
     */
    public MSAmandaScoring(double p, int N, int n, double intensity, double explainedIntensity) {
        super.p = p;
        super.N = N;
        super.n = n;
        this.intensity = intensity;
        this.explainedIntensity = explainedIntensity;
    }

    private double calculateProbabilty() throws Exception {
        double probability = 0;
        for (int k = n; k < N + 1; k++) {
            double factorial_part = CMathUtil.calculateCombination(N, k);
            double tmp_probability = factorial_part * (Math.pow(p, k)) * (Math.pow((1 - p), (N - k)));
            probability += tmp_probability;
        }
        return probability;
    }

    @Override
    protected void calculateScore() {
        try {
            double probability_based_score = calculateProbabilty();
            double intensity_based_score = explainedIntensity / intensity;
            score = - 10 * (Math.log10(probability_based_score / intensity_based_score));
            isCalculated = true;
        } catch (Exception ex) {
            Logger.getLogger(MSAmandaScoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setScore(double score) {
        this.score = score;
    }

}
