/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scoringFunction;

/**
 * This abstract class is used to write cumulative bionominal probability based
 * scoring function
 *
 * @author Sule
 */
public abstract class CumulativeBionominalProbabilityBasedScoring {

    protected double p; // probability of success on a single trial
    protected int N, // number of trials
            n;// number of successes
    protected double score = 0; // Cumulative bionominal probability based score for given N,n,p.
    protected boolean isCalculated = false; //  To make sure that a score is not calculated over and over while calling getter method

    /**
     * To calculate cumulative bionominal probability score Each scoring
     * function has different approach
     *
     */
    protected abstract void calculateScore();

    /**
     * To return cumulative bionominal probability based score for given N,n and
     * p and scoring function
     *
     * @return
     */
    public double getScore() {
        if (!isCalculated) {
            calculateScore();
        }
        return score;
    }

}
