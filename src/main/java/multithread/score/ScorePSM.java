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
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import matching.MatchAndScore;
import scoringFunction.ScoreName;
import theoretical.CPeptidePeak;
import theoretical.CrossLinkedPeptides;

/**
 *
 * @author Sule
 */
public class ScorePSM implements Callable<ArrayList<Result>> {

    private ArrayList<CrossLinkedPeptides> selectedCPeptides; // 
    private MSnSpectrum ms; // a MSnSpectrum compared to CPeptides object
    private ScoreName scoreName; // a ScoreName
    private double fragTol, // fragment tolerance, requiring for MatchAndScore instantiation.
            massWindow; // mass window, requiring for MatchAndScore instantiation.
    private int intensityOptionForMSAmanda, // fragment tolerance, requiring for MatchAndScore instantiation.
            minFilteredPeakNumber, // min number of filtered peak per window,, requiring for MatchAndScore instantiation.
            maxFilteredPeakNumber; // max number of filtered peak per window, requiring for MatchAndScore instantiation.  
    private boolean doesFindAllMatchedPeaks,
            isPPM,
            doesKeepPattern,
            doesKeepWeight;

    public ScorePSM(ArrayList<CrossLinkedPeptides> selectedCPeptides, MSnSpectrum ms, ScoreName scoreName, double fragTol, double massWindow, int intensityOptionForMSAmanda, int minFilteredPeakNumber, int maxFilteredPeakNumber,
            boolean doesFindAllMatchedPeaks, boolean isPPM, boolean doesKeepPattern, boolean doesKeepWeight) {
        this.selectedCPeptides = selectedCPeptides;
        this.ms = ms;
        this.scoreName = scoreName;
        this.fragTol = fragTol;
        this.massWindow = massWindow;
        this.intensityOptionForMSAmanda = intensityOptionForMSAmanda;
        this.minFilteredPeakNumber = minFilteredPeakNumber;
        this.maxFilteredPeakNumber = maxFilteredPeakNumber;
        this.doesFindAllMatchedPeaks = doesFindAllMatchedPeaks;
        this.isPPM = isPPM;
        this.doesKeepPattern = doesKeepPattern;
        this.doesKeepWeight = doesKeepWeight;
    }

    /**
     * A call method for multithreading. It calculates score for a given
     * MSnSpectrum against all CPeptides object
     *
     * @return a list of Result object containing MSMS/CPeptides/ScoreName/Score
     * @throws Exception
     */
    @Override
    public ArrayList<Result> call() throws Exception {
        ArrayList<Result> results = new ArrayList<Result>();
        InnerIteratorSync<CrossLinkedPeptides> iteratorCPeptides = new InnerIteratorSync(selectedCPeptides.iterator());
        while (iteratorCPeptides.iter.hasNext()) {
            CrossLinkedPeptides tmpCPeptide = (CrossLinkedPeptides) iteratorCPeptides.iter.next();
            synchronized (tmpCPeptide) {
                MatchAndScore obj = new MatchAndScore(ms, scoreName, tmpCPeptide, fragTol, intensityOptionForMSAmanda, minFilteredPeakNumber, maxFilteredPeakNumber, massWindow, doesFindAllMatchedPeaks, isPPM);
                double tmpScore = obj.getCXPSMScore(),
                        weight = obj.getWeight(),
                        fracIonPeptideAlpha = obj.getFracIonTheoPepAs(),
                        fracIonPeptideBeta = obj.getFracIonTheoPepBs(),
                        observedMass = obj.getObservedMass(),
                        deltaMass = obj.getMs1Err(),
                        absDeltaMass = obj.getAbsMS1Err();
                HashSet<Peak> matchedPeaks = obj.getMatchedPeaks();
                HashSet<CPeptidePeak> matchedTheoreticalCPeaks = obj.getMatchedTheoreticalCPeaks();
                int matchedTheoA = obj.getMatchedTheoPeaksPepA(),
                        matchedTheoB = obj.getMatchedTheoPeaksPepB();
                Result r = new Result(ms, tmpCPeptide, scoreName, tmpScore, 0, matchedPeaks, matchedTheoreticalCPeaks, weight, fracIonPeptideAlpha, fracIonPeptideBeta, observedMass, deltaMass, absDeltaMass, 0, matchedTheoA, matchedTheoB, doesKeepPattern, doesKeepWeight);
                results.add(r);
            }
        }
        // natural log of #matched peptides in DB for this selected MSnSpectrum
        double lnNumSp = Math.log(selectedCPeptides.size());
        updateResults(results, lnNumSp);
        return results;
    }

    /**
     * This method calculates delta score for the best result
     *
     * @param results
     * @param lnNumSp
     */
    private void updateResults(ArrayList<Result> results, double lnNumSp) {
        // if there is only one element on a results list, delta score equals to score!
        if (results.size() == 1) {
            results.get(0).setLnNumSpec(lnNumSp);
            results.get(0).setDeltaScore(results.get(0).getScore());
        }
        Collections.sort(results, Result.ScoreDESC);
        double deltaScore = 0;
        boolean isCalculated = false;
        int till = 0;
        for (int i = 0; i < results.size() - 2; i++) {
            Result r = results.get(i);
            double score = r.getScore();
            for (int j = i + 1; j < results.size() - 1; j++) {
                double nextBestScore = results.get(j).getScore();
                if (score != nextBestScore) {
                    deltaScore = score - nextBestScore;
                    isCalculated = true;
                    till = j;
                }
                if (isCalculated) {
                    j = results.size() - 1;
                }
            }
            if (isCalculated) {
                i = results.size() - 2;
            }
        }
        for (int i = 0; i < till; i++) {
            Result r = results.get(i);
            r.setDeltaScore(deltaScore);
            results.get(0).setLnNumSpec(lnNumSp);
        }
        for (int i = till + 1; i < results.size(); i++) {
            results.remove(i);
        }
    }

    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
    private class InnerIteratorSync<T> {

        private Iterator<T> iter = null;

        public InnerIteratorSync(Iterator<T> aIterator) {
            iter = aIterator;
        }

        public synchronized T next() {
            T result = null;
            if (iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }
    }

}
