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
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinking;
import theoretical.MonoLinkedPeptides;

/**
 * This class is used for multithreading, for matching and scoring every
 * spectrum with given properties.
 *
 * @author Sule
 */
public class ScorePSM implements Callable<ArrayList<Result>> {

    private ArrayList<CrossLinking> selectedCPeptides; // 
    private MSnSpectrum ms; // a MSnSpectrum compared to CPeptides object
    private ScoreName scoreName; // a ScoreName
    private double fragTol, // fragment tolerance, requiring for MatchAndScore instantiation.
            massWindow; // mass window, requiring for MatchAndScore instantiation.
    private int intensityOptionForMSAmanda, // fragment tolerance, requiring for MatchAndScore instantiation.
            minFilteredPeakNumber, // min number of filtered peak per window,, requiring for MatchAndScore instantiation.
            maxFilteredPeakNumber, // max number of filtered peak per window, requiring for MatchAndScore instantiation.  
            peakRequiredForImprovedSearch;
    private boolean doesFindAllMatchedPeaks,
            isPPM,
            doesKeepPattern,
            doesKeepWeight;

    public ScorePSM(ArrayList<CrossLinking> selectedCPeptides, MSnSpectrum ms, ScoreName scoreName,
            double fragTol, double massWindow, int intensityOptionForMSAmanda, int minFilteredPeakNumber, int maxFilteredPeakNumber,
            boolean doesFindAllMatchedPeaks, boolean doesKeepPattern, boolean doesKeepWeight, boolean isPPM, int peakRequiredForImprovedSearch) {
        this.peakRequiredForImprovedSearch = peakRequiredForImprovedSearch;
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
        InnerIteratorSync<CrossLinking> iteratorCPeptides = new InnerIteratorSync(selectedCPeptides.iterator());
        while (iteratorCPeptides.iter.hasNext()) {
            CrossLinking tmpCPeptide = (CrossLinking) iteratorCPeptides.iter.next();
            synchronized (tmpCPeptide) {
                MatchAndScore obj = new MatchAndScore(ms, scoreName, tmpCPeptide, fragTol, intensityOptionForMSAmanda, minFilteredPeakNumber, maxFilteredPeakNumber, massWindow, doesFindAllMatchedPeaks, isPPM);
                double tmpScore = obj.getXPSMScore(),
                        weight = obj.getWeight(),
                        fracIonPeptideAlpha = obj.getFracIonTheoPepAs(),
                        fracIonPeptideBeta = obj.getFracIonTheoPepBs(),
                        observedMass = obj.getObservedMass(),
                        deltaMass = obj.getMs1Err(),
                        absDeltaMass = obj.getAbsMS1Err();
                HashSet<Peak> matchedPeaks = obj.getMatchedPeaks();

                // check if there is enough peaks from both peptides here
                HashSet<CPeptidePeak> matchedTheoreticalCPeaks = obj.getMatchedTheoreticalXLPeaks();

                boolean control = hasEnoughPeaks(new ArrayList<CPeptidePeak>(matchedTheoreticalCPeaks), peakRequiredForImprovedSearch);
                if ((control && tmpCPeptide instanceof CPeptides) || tmpCPeptide instanceof MonoLinkedPeptides) {
                    int matchedTheoA = obj.getMatchedTheoPeaksPepA(),
                            matchedTheoB = obj.getMatchedTheoPeaksPepB();
                    Result r = new Result(ms, tmpCPeptide, scoreName, tmpScore, 0, matchedPeaks, matchedTheoreticalCPeaks, weight, fracIonPeptideAlpha, fracIonPeptideBeta,
                            observedMass, deltaMass, absDeltaMass, 0, 0, matchedTheoA, matchedTheoB, doesKeepPattern, doesKeepWeight);
                    results.add(r);
                }
            }
        }
        // natural log of #matched peptides in DB for this selected MSnSpectrum
        if (!results.isEmpty()) {
            double lnNumSp = getLnNumSp(selectedCPeptides);
            updateResults(results, lnNumSp);
        }
        return results;
    }

    /**
     * This method returns the natural logarithm of the number of selected
     * database peptides
     *
     * @param selectedCPeptides
     * @return
     */
    public static double getLnNumSp(ArrayList<CrossLinking> selectedCPeptides) {
        HashSet<String> peps = new HashSet<String>();
        for (CrossLinking s : selectedCPeptides) {
            if (s instanceof CPeptides) {
                CPeptides sC = (CPeptides) s;
                String pepA = sC.getPeptideA().getSequence(),
                        pepB = sC.getPeptideB().getSequence(),
                        pep = pepA + "_" + pepB;
                if (!peps.contains(pepB + "_" + pepA)) {
                    peps.add(pep);
                }
            } else if (s instanceof MonoLinkedPeptides) {
                MonoLinkedPeptides mC = (MonoLinkedPeptides) s;
                String pep = mC.getPeptide().getSequence();
                peps.add(pep);
            } else if (s instanceof Contaminant) {
                Contaminant cC = (Contaminant) s;
                String pep = cC.getPeptide().getSequence();
                peps.add(pep);
            }
        }
        return (Math.log(peps.size()));
    }

    /**
     * This method checks if there are enough peaks found from backbones of both
     * peptides in a pair
     *
     * @param matchedCTheoPLists
     * @param requiredPeaks
     * @return
     */
    private boolean hasEnoughPeaks(ArrayList<CPeptidePeak> matchedCTheoPLists, int requiredPeaks) {
        boolean hasEnoughPeaks = false;
        int theoPepA = 0,
                theoPepB = 0;
        for (CPeptidePeak cpP : matchedCTheoPLists) {
            String name = cpP.getName();
            if (name.contains("--")) {
                String[] names = name.split("--");
                for (String tmp : names) {
                    if (tmp.contains("pepA") && (!tmp.contains("lepB"))) {
                        theoPepA++;
                    }
                    if (tmp.contains("pepB") && (!tmp.contains("lepA"))) {
                        theoPepB++;
                    }
                }
            } else {
                if (cpP.getName().contains("pepA") && (!cpP.getName().contains("lepB"))) {
                    theoPepA++;
                }
                if (cpP.getName().contains("pepB") && (!cpP.getName().contains("lepA"))) {
                    theoPepB++;
                }
            }
        }
        if (theoPepA >= requiredPeaks && theoPepB >= requiredPeaks) {
            hasEnoughPeaks = true;
        }
        return hasEnoughPeaks;
    }

    /**
     * This method calculates delta score for the best result and set its score
     * and lnNumSpec. It also removes any other ranked results from a given list
     *
     * @param results
     * @param lnNumSp
     */
    public static void updateResults(ArrayList<Result> results, double lnNumSp) {
        // if there is only one element on a results list, delta score equals to score!
        if (results.size() == 1) {
            results.get(0).setLnNumSpec(lnNumSp);
            results.get(0).setDeltaScore(1);
        } else {
            Collections.sort(results, Result.ScoreDESC);
            double deltaScore = 0;
            int till = 0;
            Result best = results.get(0); // the first score is the best one..
            double score = best.getScore();
            for (int j = 1; j < results.size(); j++) {
                double nextBestScore = results.get(j).getScore();
                if (score != nextBestScore) {
                    till = j;
                    deltaScore = (score - nextBestScore) / score;
                    break;
                }
            }
            if (till == 0) {
                till = results.size(); // because apparently all elements have the same score..
                deltaScore = 1;
            }
            ArrayList<Result> toRemove = new ArrayList<Result>();
            for (int i = 0; i < results.size(); i++) {
                Result r = results.get(i);
                if (i < till) {
                    r.setDeltaScore(deltaScore);
                    r.setLnNumSpec(lnNumSp);
                    r.setLnNumXSpec(results.size());
                } else {
                    toRemove.add(r);
                }
            }
            results.removeAll(toRemove);
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
