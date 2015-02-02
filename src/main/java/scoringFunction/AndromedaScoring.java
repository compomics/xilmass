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
 *
 * @author Sule
 */
public class AndromedaScoring extends CumulativeBionominalProbabilityBasedScoring {

    public AndromedaScoring(double p, int N, int n) {
        super.p = p; // probability=m/100 because they pick the top m peaks in a 100 Da window. They pick [1- 10] peaks
        super.N = N; // N: All theoretical peaks at a theoretical spectrum (on Andromeda)
        super.n = n;// n: Matched peaks is number of matched peaks on theoretical spectrum  
    }

    private double calculateProbabilty() throws Exception {
        double probability = 0;
        if (n == N) {
            double factorial_part = CMathUtil.calculateCombination(N, n);
            double tmp_probability = factorial_part * (Math.pow(p, n)) * (Math.pow((1 - p), (N - n)));
            probability += tmp_probability;
        } else {
            for (int k = n; k < N - 1; k++) {
                double factorial_part = CMathUtil.calculateCombination(N, k);
                double tmp_probability = factorial_part * (Math.pow(p, k)) * (Math.pow((1 - p), (N - k)));
                probability += tmp_probability;
            }
        }
        return probability;
    }

    @Override
    protected void calculateScore() {
        try {
            double probability_based_score = calculateProbabilty();
            score = - 10 * (Math.log10(probability_based_score));
            isCalculated = true;

        } catch (Exception ex) {
            Logger.getLogger(AndromedaScoring.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
