/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import scoringFunction.Andromeda_derived;
import scoringFunction.MSAmanda_derived;
import scoringFunction.ScoreName;
import start.CalculateMS1Err;
import start.CalculatePrecursorMass;
import theoretical.CPeptideIon;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.CrossLinking;

/**
 *
 * This class finds matched peaks between a pair of an experimental and
 * theoretical spectrum. Then, it calculates a score for a XPSM!
 *
 * @author Sule
 */
public class MatchAndScore {

    private MSnSpectrum expMS2; // experimental MS2 spectrum
    private HashSet<Peak> matchedPeaks = new HashSet<Peak>(); // matched peaks on on an experimental MS2 spectrum with the certain fragment tolerance against theoretical spectrum
    private CrossLinking cPeptides; // cross linked peptide object (containing peptideA, peptideB and crosslinker)
    private HashSet<CPeptideIon> theoXLMS2ions = new HashSet<CPeptideIon>(); // theoretical crosslinked ions derived from a cross linked peptide (they have an attribute called mass)
    private HashSet<CPeptidePeak> theoXLPeaks = new HashSet<CPeptidePeak>(), // theoretical crosslinked peaks derived from a cross linked peptide (they have an attribute called MZ, not MASS)
            // TODO: Need to see the performance in terms of object generation!
            matchedTheoXLPeaks = new HashSet<CPeptidePeak>(); // Matched theoretical cross linked peaks
    private ArrayList<CPeptidePeak> theoXLPeaksAL = new ArrayList<CPeptidePeak>(),
            singlyChargedTheoXLPeaksAL = new ArrayList<CPeptidePeak>(),
            doublyChargedTheoXLPeaksAL = new ArrayList<CPeptidePeak>();
    private double fragTol, // fragment tolerance to select
            xPSMScore = 0, // A XPSM Score
            massWindow = 100, // Mass window to filter out peaks from a given MSnSpectrum
            fracIonTheoPepA = 0, //numFoundTheoPeak/allTheoPeak)PepA
            fracIonTheoPepB = 0, //numFoundTheoPeak/allTheoPeak)PepB
            weight = 0, // for andromeda - (numFoundTheoPeak/allTheoPeak)PepA*(numFoundTheoPeak/allTheoPeak)PepB
            observedMass = 0, // singly charged precursor ion mass of an experimental MS2 spectrum
            ms1Err = 0, // difference between calculated and observed mass
            absMS1Err = 0; // absolute difference between calculated and observed mass
    private int intensityOptionForMSAmandaDerived = 0,
            minFPeaks, // Minimum number of filtered peaks per 100Da mass window.. (To test here)
            maxFPeaks, // Maximum number of filtered peaks per 100Da mass window.. (To test)
            matchedTheoPeaksPepA, // matched theoretical peaks from peptideA
            matchedTheoPeaksPepB; // matched theoretical peaks from peptideB
    private ScoreName scoreName;// 0-MSAmanda_derived (MSAmanda_derived with N=AllPickedPeaks), 1-Andromeda_derived, 2-TheoMSAmandaD (MSAmanda_derived with N=AllTheoPeaks)
    private boolean isTheoXLPeaksReady = false,
            isFoundAndMatched = false,
            doesFindAllMatchedPeaks = false, // True: find all matched peaks False: if there is one experimental peak matched to a theoretical peak (or more than one), it will select the closest one
            isCPeptide = false,
            isPPM = false;
    private static int NONEUTRALLOSS = 0,
            WATERLOSS = 1,
            AMMONIALOSS = 2;

    /* Constructor */
    public MatchAndScore(MSnSpectrum expMS2, ScoreName scoreName, CrossLinking cPeptides,
            double fragTol, int intensityOption, int minFPeakNum, int maxFPeakNum,
            double massWindow, boolean doesFindAllMatchedPeaks, boolean isPPM) {
        this.expMS2 = expMS2;
        this.scoreName = scoreName;
        this.cPeptides = cPeptides;
        if (cPeptides != null) {
            theoXLMS2ions = cPeptides.getTheoretical_ions();
            theoXLPeaks = getTheoreticalXLPeaks();
            isTheoXLPeaksReady = true;
        }
        if (cPeptides instanceof CPeptides) {
            isCPeptide = true;
        }
        this.fragTol = fragTol;
        this.intensityOptionForMSAmandaDerived = intensityOption;
        this.minFPeaks = minFPeakNum;
        this.maxFPeaks = maxFPeakNum;
        this.massWindow = massWindow;
        this.doesFindAllMatchedPeaks = doesFindAllMatchedPeaks;
        this.isPPM = isPPM;
    }

    /* getters and setters */
    public MSnSpectrum getExpMS2() {
        return expMS2;
    }

    /**
     * Weight calculated as
     * (numFoundTheoPeak/allTheoPeak)PepA*(numFoundTheoPeak/allTheoPeak)PepB for
     * AndromedaWeighted scoring Make sure that MatchAndScore method has been
     * called before!
     *
     * @return
     */
    public double getWeight() {
        return weight;
    }

    public double getFracIonTheoPepAs() {
        return fracIonTheoPepA;
    }

    public void setFracIonTheoPepAs(double fracIonTheoPepAs) {
        this.fracIonTheoPepA = fracIonTheoPepAs;
    }

    public double getFracIonTheoPepBs() {
        return fracIonTheoPepB;
    }

    public void setFracIonTheoPepBs(double fracIonTheoPepBs) {
        this.fracIonTheoPepB = fracIonTheoPepBs;
    }

    public double getObservedMass() {
        return observedMass;
    }

    public void setObservedMass(double observedMass) {
        this.observedMass = observedMass;
    }

    public double getMs1Err() {
        return ms1Err;
    }

    public void setMs1Err(double ms1Err) {
        this.ms1Err = ms1Err;
    }

    public double getAbsMS1Err() {
        return absMS1Err;
    }

    public void setAbsMS1Err(double absMS1Err) {
        this.absMS1Err = absMS1Err;
    }

    /**
     * Returns an hashset named theoreticalXLMS2ions which contains mass values
     * derived from a cross-linked peptide. Here charge state information is not
     * considered!
     *
     * @return
     */
    public HashSet<CPeptideIon> getTheoreticalXLMS2ions() {
        if (theoXLMS2ions.isEmpty()) {
            theoXLMS2ions = cPeptides.getTheoretical_ions();
        }
        return theoXLMS2ions;
    }

    public void setTheoXLMS2ions(HashSet<CPeptideIon> theoXLMS2ions) {
        this.theoXLMS2ions = theoXLMS2ions;
    }

    /**
     * Returns cPeptides object.. A crosslinked peptide pair and their
     * cross-linking information.
     *
     * @return
     */
    public CrossLinking getCPeptides() {
        return cPeptides;
    }

    /**
     * Returns an arraylist of CPeptidePeaks for theoretical XPeaks.
     *
     * @return
     */
    public ArrayList<CPeptidePeak> getTheoreticalXPeaksAL() {
        if (!isTheoXLPeaksReady) {
            isFoundAndMatched = false;
            getTheoreticalXLPeaks();
        }
        return theoXLPeaksAL;
    }

    public void setExpMS2(MSnSpectrum expMS2) {
        isFoundAndMatched = false;
        this.expMS2 = expMS2;
    }

    /**
     * It sets theoreticalXLMS2ions from a given HashSet object...
     *
     * @param theoreticalCXMS2ions
     */
    public void setTheoreticalCXMS2ions(HashSet<CPeptideIon> theoreticalCXMS2ions) {
        this.theoXLMS2ions = theoreticalCXMS2ions;
        isTheoXLPeaksReady = false;
        theoXLPeaks = getTheoreticalXLPeaks();
    }

    /**
     * It set a CPeptides object while setting back to all other attributes
     *
     * @param cPeptides
     */
    public void setCPeptides(CPeptides cPeptides) {
        isTheoXLPeaksReady = false;
        this.cPeptides = cPeptides;
        matchedPeaks = new HashSet<Peak>();
        xPSMScore = Double.MIN_VALUE;
        theoXLMS2ions = cPeptides.getTheoretical_ions();
        theoXLPeaks = getTheoreticalXLPeaks();
        matchedTheoXLPeaks = new HashSet<CPeptidePeak>();
        isFoundAndMatched = false;
    }

    /**
     * Returns a integer value representing a precursor charge. It selects the
     * first charge value from a given list of possible charges
     *
     * @return
     */
    public int getPrecursorCharge() {
        ArrayList<Charge> precPossCharges = expMS2.getPrecursor().getPossibleCharges();
        int precursor_charge = precPossCharges.get(0).value;
        if (precPossCharges.size() > 1) {
            for (Charge possCharge : precPossCharges) {
                if (precursor_charge < possCharge.value) {
                    precursor_charge = possCharge.value;
                }
            }
        }
        return precursor_charge;
    }

    /**
     * Returns a fragment tolerance
     *
     * @return
     */
    public double getFragTol() {
        return fragTol;
    }

    public boolean isDoesFindAllMatchedPeaks() {
        return doesFindAllMatchedPeaks;
    }

    public void setDoesFindAllMatchedPeaks(boolean doesFindAllMatchedPeaks) {
        this.doesFindAllMatchedPeaks = doesFindAllMatchedPeaks;
    }

    /**
     * Returns which scoring function is selected to calculate CXPSMs score.
     *
     * 0-MSAmanda_derived with N=AllPickedPeaks(different intensity option)
     *
     * 1-Andromeda_derived with theoretical peaks
     *
     * 3-TheoMSAmanda_derived (MSAmanda_derived with N=theoretical peaks)
     *
     * @return
     */
    public ScoreName getScoreName() {
        return scoreName;
    }

    /**
     * This method selects experimental peaks closed to generated theoretical
     * peaks within given fragment tolerance. It then calculates cumulative
     * binomial probability based scoring_type based on three different
     * approaches if neutral losses are taken into account, theoretical XL peaks
     * will have also peaks with neutral losses.
     *
     *
     * Neutral losses are include based on an attribute of neutralLossesCase
     * with 3 options:
     *
     * 0-no neutral losses
     *
     * 1-neutral losses for parent ion specific selected amino acids
     *
     * 2-all neutral losses
     *
     * @return
     */
    public double getXPSMScore() {
        if (!isFoundAndMatched) {
            int neutralLossCase = cPeptides.neutralLossesCase();
            int totalTheoN = getTheoreticalXLPeaks().size(); // all theoretical peaks...
            Collections.sort(theoXLPeaksAL, CPeptidePeak.order_CPeptidePeak);
            ArrayList<Double> scores = new ArrayList<Double>();
            for (int numHighestPeak = minFPeaks; numHighestPeak <= maxFPeaks; numHighestPeak++) {
                Filter filter = new Filter(expMS2, numHighestPeak, massWindow);
                ArrayList<Peak> filteredPeaks = filter.getFilteredCPeaks();
                Collections.sort(filteredPeaks, new Comparator<Peak>() {
                    @Override
                    public int compare(Peak o1, Peak o2) {
                        return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
                    }
                });
                double probability = (double) numHighestPeak / (double) (filter.getWindowSize());
                int n = 0;
                HashMap<CPeptidePeak, MatchedPeak> matched_theopeak_and_matched_exppeak = new HashMap<CPeptidePeak, MatchedPeak>();
                HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theopeak_and_matched_exppeaks = new HashMap<CPeptidePeak, ArrayList<MatchedPeak>>();
                for (Peak fPeak : filteredPeaks) {
                    double diff = fragTol;// Based on Da.. not ppm... First it starts with FragmentTolerance
                    for (int i = 0; i < theoXLPeaksAL.size(); i++) {
                        CPeptidePeak tmpCPeak = theoXLPeaksAL.get(i);
                        double cPeakDiff_before = tmpCPeak.getDiff();
                        MatchedPeak mPeak = match_theoretical_peaks(tmpCPeak, fPeak, diff, matched_theopeak_and_matched_exppeak, matched_theopeak_and_matched_exppeaks);
                        double cPeakDiff_after = tmpCPeak.getDiff();
                        // check for neutral losses here
                        int neutralLossesType = getNeutralLossType(tmpCPeak);
                        if (cPeakDiff_after != cPeakDiff_before && neutralLossesType != NONEUTRALLOSS && neutralLossCase == 1 && tmpCPeak.getCharge() == 1 && tmpCPeak.getAa_code() != '+') {
                            //check for possible neutral losses from this peak...
                            double diff_neutral_loss = fragTol; // reset for new peak searching 
                            mPeak = checkForNeutralLosses(tmpCPeak, diff_neutral_loss, matched_theopeak_and_matched_exppeak, matched_theopeak_and_matched_exppeaks,
                                    filteredPeaks, neutralLossesType);
                            totalTheoN++;
                        }
                    }
                }
                matchedPeaks = new HashSet<Peak>();
                matchedTheoXLPeaks = new HashSet<CPeptidePeak>();
                double explainedIntensities = 0,
                        intensities = getIntensities(filteredPeaks); // sum up all intensities from filtered peaks list
                if (!doesFindAllMatchedPeaks) {
                    fillForClosestPeak(matched_theopeak_and_matched_exppeak); // so matchedPeaks and matchedTheoreticalCXPeakscan be filled here...
                    explainedIntensities = getExplainedIntensities(matchedPeaks); // sum up all intensities from matched experimental peaks
                } else {
                    fillForAllFoundPeaks(matched_theopeak_and_matched_exppeaks);
                    explainedIntensities = getWeightedExplainedIntensities(matched_theopeak_and_matched_exppeaks, fragTol);
                }
                n = matchedPeaks.size();
                double tmp_score = 0;
                // MSAmanda_derived with expertimentatl spectrum
                switch (scoreName) {
                    case MSAmandaD: {
                        MSAmanda_derived object = new MSAmanda_derived(probability, filter.getFilteredCPeaks().size(), n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName);
                        tmp_score = object.getScore();
                        scores.add(tmp_score);
                        // Andromeda_derived with theoretical spectra size
                        break;
                    }
                    case AndromedaD: {
                        Andromeda_derived object = new Andromeda_derived(probability, totalTheoN, n);
                        tmp_score = object.getScore();
                        scores.add(tmp_score);
                        // MSAmanda_derived with theoretical spectra size
                        break;
                    }
                    case TheoMSAmandaD: {
                        MSAmanda_derived object = new MSAmanda_derived(probability, totalTheoN, n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName);
                        tmp_score = object.getScore();
                        scores.add(tmp_score);
                        break;
                    }
                    case AndromedaDWeighted: {
                        calculateWeightForTheoPeaks(matchedTheoXLPeaks, theoXLPeaksAL, isCPeptide);
                        Andromeda_derived object = new Andromeda_derived(probability, totalTheoN, n, weight);
                        tmp_score = object.getScore();
                        scores.add(tmp_score);
                        break;
                    }
                    case TheoMSAmandaDWeighted: {
                        calculateWeightForTheoPeaks(matchedTheoXLPeaks, theoXLPeaksAL, isCPeptide);
                        MSAmanda_derived object = new MSAmanda_derived(probability, totalTheoN, n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName, weight);
                        tmp_score = object.getScore();
                        scores.add(tmp_score);
                        break;
                    }
                }
            }
            isFoundAndMatched = true;
            xPSMScore = Collections.max(scores);

            // now calculate observed mass and mass error
            observedMass = calculateObservedMass(expMS2);

            // select observed and calculated PrecMZ
            ArrayList<Charge> possibleCharges = expMS2.getPrecursor().getPossibleCharges();
            int charge_value = possibleCharges.get(possibleCharges.size() - 1).value;
            double observedPrecMass = (expMS2.getPrecursor().getMass(charge_value)),
                    calculatedPrecMass = (cPeptides.getTheoretical_xlinked_mass());
            // now calculates MS1 error with precursor mass and theoretical mass of crosslinked peptide-make sure that both masses are singly charged to calculate MS1Err
            // Based on the calcaulation of two values: ms1Err = calculateMS1Err(isPPM, cPeptides.getTheoretical_xlinked_mass(), (observedMass - protonMass));
            // following one only computes theoretical cpeptide mass, but do not do anything with experimental spectrum precursor. 
            ms1Err = CalculateMS1Err.getMS1Err(isPPM, calculatedPrecMass, observedPrecMass);
            absMS1Err = Math.abs(ms1Err);
        }
        return xPSMScore;
    }

    /**
     * This method matches if given cPeak is within tolerances according to two
     * different matching approaches.
     *
     * @param cPeak
     * @param p
     * @param diff
     * @param peak_and_matchedPeak
     * @param matched_theoretical_and_matched_peaks
     * @return
     */
    private MatchedPeak match_theoretical_peaks(CPeptidePeak cPeak, Peak p, double diff,
            HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak, HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks) {
        MatchedPeak mPeak = null;
        if (cPeak.getMz() <= (p.mz + fragTol + 0.01)) { // making sure that now missing any theoretical peak..
            double theoMz = cPeak.getMz(),
                    expMz = p.getMz(),
                    tmp_diff = Math.abs(expMz - theoMz);
            // A theoretical peak which is closest to an experimental peak is selected!
            // if two peaks are selected and two theoretical peaks withing fragment tolerance, only closest theoretical peak is selected
            // In case that a peak has matched to two theoretical peaks with the mass tolerance, only the left one is selected
            if (tmp_diff <= diff && !doesFindAllMatchedPeaks) {
                // first time this filtered peak is matched to a theoretical peak for ever
                if (mPeak == null && !peak_and_matchedPeak.containsKey(cPeak)) {
                    diff = Math.abs(tmp_diff);
                    cPeak.setDiff(diff);
                    mPeak = new MatchedPeak(p, cPeak, Math.abs(tmp_diff));
                    peak_and_matchedPeak.put(cPeak, mPeak);
                    // first time this filtered peak is matched to a theoretical peak, this theoretical peak, however, is already matched to another peak
                } else if (mPeak == null && peak_and_matchedPeak.containsKey(cPeak)) {
                    double stored_diff = peak_and_matchedPeak.get(cPeak).getDiff();
                    if (stored_diff > Math.abs(tmp_diff)) {
                        mPeak = new MatchedPeak(p, cPeak, Math.abs(tmp_diff));
                        cPeak.setDiff(diff);
                        removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                        //  update with a currently found peak..
                        peak_and_matchedPeak.put(cPeak, mPeak);
                        diff = Math.abs(tmp_diff);
                    }
                    // this filtered peak was matched to a theoretical peak before and a currently searched theoretical peak is also found for any filtered peak before...
                } else if (mPeak != null && peak_and_matchedPeak.containsKey(cPeak)) { //
                    double stored_diff = peak_and_matchedPeak.get(cPeak).getDiff();
                    // check if currently found peak has indeed smaller difference
                    if (stored_diff > Math.abs(tmp_diff)) {
                        diff = Math.abs(tmp_diff);
                        removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                        mPeak.setDiff(diff);
                        cPeak.setDiff(diff);
                        // update it with a new one
                        peak_and_matchedPeak.put(cPeak, mPeak);
                    } else {
                        // this peak is stored twice with more than one - remove other ones with  holding a smaller diff
                        diff = Math.abs(tmp_diff);
                        removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                    }
                    // this filtered peak was matched to a theoretical peak before and a currently searched theoretical peak is NOT found for any filtered peak before...
                } else if (mPeak != null && !peak_and_matchedPeak.containsKey(cPeak)) { //
                    double storedDiff = mPeak.getDiff();
                    if (storedDiff > Math.abs(tmp_diff)) {
                        diff = Math.abs(tmp_diff);
                        removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                        mPeak.setDiff(diff);
                        cPeak.setDiff(diff);
                        peak_and_matchedPeak.put(cPeak, mPeak);
                    }
                }
                // from now on we select any peak theoretical peaks/experimental peaks matched to another experimental/theoretical peak within a fragment tolerance
            } else if (tmp_diff <= diff && doesFindAllMatchedPeaks && matched_theoretical_and_matched_peaks.containsKey(cPeak)) {
                MatchedPeak mp = new MatchedPeak(p, cPeak, tmp_diff);
                matched_theoretical_and_matched_peaks.get(cPeak).add(mp);
                cPeak.setDiff(tmp_diff);
            } else if (tmp_diff <= diff && doesFindAllMatchedPeaks && !matched_theoretical_and_matched_peaks.containsKey(cPeak)) {
                ArrayList<MatchedPeak> tmps = new ArrayList<MatchedPeak>();
                tmps.add(new MatchedPeak(p, cPeak, tmp_diff));
                matched_theoretical_and_matched_peaks.put(cPeak, tmps);
                cPeak.setDiff(tmp_diff);
            }
        }
        return mPeak;
    }

    /**
     * This method is used to select a closed experimental peak to only one
     * theoretical peak So, if two experimental peaks are matched to one
     * theoretical peak, the closest experimental would be taken If two
     * theoretical peaks are matched to one experimental peak, only the closest
     * theoretical peak would be selected. If two experimental peaks have the
     * same distance to one theoretical peak, the left one would be selected
     *
     * @param mPeak
     * @param peak_and_matchedPeak
     * @param tmpCPeak
     * @param diff
     * @param tmp_diff
     * @param p
     * @return
     */
    private double findMatchedPeakLessPrecisely(MatchedPeak mPeak, HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak, CPeptidePeak tmpCPeak, double diff, double tmp_diff, Peak p) {
        // first time this filtered peak is matched to a theoretical peak for ever
        if (mPeak == null && !peak_and_matchedPeak.containsKey(tmpCPeak)) {
            diff = Math.abs(tmp_diff);
            tmpCPeak.setDiff(diff);
            mPeak = new MatchedPeak(p, tmpCPeak, Math.abs(tmp_diff));
            peak_and_matchedPeak.put(tmpCPeak, mPeak);
            // first time this filtered peak is matched to a theoretical peak, this theoretical peak, however, is already matched to another peak
        } else if (mPeak == null && peak_and_matchedPeak.containsKey(tmpCPeak)) {
            double stored_diff = peak_and_matchedPeak.get(tmpCPeak).getDiff();
            if (stored_diff > Math.abs(tmp_diff)) {
                mPeak = new MatchedPeak(p, tmpCPeak, Math.abs(tmp_diff));
                tmpCPeak.setDiff(diff);
                removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                //  update with a currently found peak..
                peak_and_matchedPeak.put(tmpCPeak, mPeak);
                diff = Math.abs(tmp_diff);
            }
            // this filtered peak was matched to a theoretical peak before and a currently searched theoretical peak is also found for any filtered peak before...
        } else if (mPeak != null && peak_and_matchedPeak.containsKey(tmpCPeak)) { //
            double stored_diff = peak_and_matchedPeak.get(tmpCPeak).getDiff();
            // check if currently found peak has indeed smaller difference
            if (stored_diff > Math.abs(tmp_diff)) {
                diff = Math.abs(tmp_diff);
                removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                mPeak.setDiff(diff);
                tmpCPeak.setDiff(diff);
                // update it with a new one
                peak_and_matchedPeak.put(tmpCPeak, mPeak);
            } else {
                // this peak is stored twice with more than one - remove other ones with  holding a smaller diff
                diff = Math.abs(tmp_diff);
                removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
            }
            // this filtered peak was matched to a theoretical peak before and a currently searched theoretical peak is NOT found for any filtered peak before...
        } else if (mPeak != null && !peak_and_matchedPeak.containsKey(tmpCPeak)) { //
            double storedDiff = mPeak.getDiff();
            if (storedDiff > Math.abs(tmp_diff)) {
                diff = Math.abs(tmp_diff);
                removePreviousSelectedExpPeakWithHigherFragmentTolerance(peak_and_matchedPeak, mPeak);
                mPeak.setDiff(diff);
                tmpCPeak.setDiff(diff);
                peak_and_matchedPeak.put(tmpCPeak, mPeak);
            }
        }
        return diff;
    }

    private void removePreviousSelectedExpPeakWithHigherFragmentTolerance(HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak, MatchedPeak mPeak) {
        ArrayList<CPeptidePeak> toRemoves = new ArrayList<CPeptidePeak>();
        for (CPeptidePeak cccp : peak_and_matchedPeak.keySet()) {
            if (peak_and_matchedPeak.get(cccp).getMatchedPeak().equals(mPeak.getMatchedPeak())) {
                toRemoves.add(cccp);
            }
        }
        for (CPeptidePeak cccp : toRemoves) {
            peak_and_matchedPeak.remove(cccp);
        }
    }

    /**
     * Returns matched experimental peaks. Unless a CXPSMScore is calculated,
     * then it starts with this then returns a list of experimental peaks
     *
     * @return
     */
    public HashSet<Peak> getMatchedPeaks() {
        if (!isFoundAndMatched) {
            getXPSMScore();
        }
        return matchedPeaks;
    }

    /**
     * Returns matched theoretical CXPeaks. Unless a CXPSMScore is calculated,
     * then it starts with this then returns a list of CPeptidePeak
     *
     * @return
     */
    public HashSet<CPeptidePeak> getMatchedTheoreticalXLPeaks() {
        if (!isFoundAndMatched) {
            getXPSMScore();
        }
        return matchedTheoXLPeaks;
    }

    public void setMatchedTheoreticalXLPeaks(HashSet<CPeptidePeak> matchedTheoPeaks) {
        this.matchedTheoXLPeaks = matchedTheoPeaks;
    }

    /**
     * This method returns a list of CPeptidePeak with singly and doubly charged
     * ions. Peaks with the same m/z may come from the different ions (due to
     * different charge state) and so such peaks are collapsed into one single
     * peak with combined/updated name
     *
     * So it returns a hashset named theoXLPeaks containing m/z values for all
     * singly and doubly charged theoretical XL ions
     *
     *
     * @return
     */
    public HashSet<CPeptidePeak> getTheoreticalXLPeaks() {
        if (!isTheoXLPeaksReady) {
            theoXLPeaks = new HashSet<CPeptidePeak>();
            theoXLMS2ions = getTheoreticalXLMS2ions();
            HashSet<CPeptidePeak> cPeakList = new HashSet<CPeptidePeak>();
            HashMap<Double, CPeptidePeak> mz_and_peaks = new HashMap<Double, CPeptidePeak>();
            for (CPeptideIon c : theoXLMS2ions) {
                // singly and doubly charged ones..
                String name = c.getName();
                double singly_mz = c.get_theoretical_mz(1);
                // check if this mz value already mapped
                if (mz_and_peaks.containsKey(singly_mz)) {
                    // then update this
                    CPeptidePeak tmp = mz_and_peaks.get(singly_mz);
                    tmp.setName(tmp.getName() + "--" + name);
                } else {
                    CPeptidePeak singly_charged = new CPeptidePeak(singly_mz, c.getIntensity(), name, c.getAa_code(), 1);
                    cPeakList.add(singly_charged);
                    singlyChargedTheoXLPeaksAL.add(singly_charged);
                    mz_and_peaks.put(singly_mz, singly_charged);
                }

                if (getPrecursorCharge() > 1) {
                    name = c.getName();
                    double doubly_mz = c.get_theoretical_mz(2);
                    // check again if this m/z is already mapped
                    if (mz_and_peaks.containsKey(doubly_mz)) {
                        // then update this
                        CPeptidePeak tmp = mz_and_peaks.get(doubly_mz);
                        tmp.setName(tmp.getName() + "--" + name);
                    } else {
                        CPeptidePeak doubly_charged = new CPeptidePeak(doubly_mz, c.getIntensity(), name, c.getAa_code(), 2);
                        cPeakList.add(doubly_charged);
                        doublyChargedTheoXLPeaksAL.add(doubly_charged);
                        mz_and_peaks.put(doubly_mz, doubly_charged);
                    }
                }
            }
            theoXLPeaks.addAll(cPeakList);
            theoXLPeaksAL = new ArrayList<CPeptidePeak>(theoXLPeaks);
            Collections.sort(theoXLPeaksAL, CPeptidePeak.order_CPeptidePeak);
            Collections.sort(singlyChargedTheoXLPeaksAL, CPeptidePeak.order_CPeptidePeak);
            Collections.sort(doublyChargedTheoXLPeaksAL, CPeptidePeak.order_CPeptidePeak);
            isTheoXLPeaksReady = true;
            return cPeakList;
        } else {
            return theoXLPeaks;
        }
    }

    /**
     * This method returns summed up of all intensties from matched peaks..
     *
     * @param matchedPeaks
     * @return
     */
    public static double getExplainedIntensities(HashSet<Peak> matchedPeaks) {
        double expIntensities = 0;
        for (Peak p : matchedPeaks) {
            expIntensities += p.getIntensity();
        }
        return expIntensities;
    }

    /**
     * This method returns all intensities from a filtered peaks list.
     *
     * @param filteredPeaks
     * @return
     */
    public static double getIntensities(ArrayList<Peak> filteredPeaks) {
        double intensities = 0;
        for (Peak p : filteredPeaks) {
            intensities += p.intensity;
        }
        return intensities;
    }

    /**
     * This method fills both matched experimental peaks and matched theoretical
     * peaks from given hashmap for doesFindMatchedPeaksLessPrecise=true option
     *
     * @param peak_and_matchedPeak map with theoretical peak and their matched
     * peak objects
     */
    public void fillForClosestPeak(HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak) {
        for (CPeptidePeak cpPeak : peak_and_matchedPeak.keySet()) {
            matchedPeaks.add(peak_and_matchedPeak.get(cpPeak).getMatchedPeak());
            matchedTheoXLPeaks.add(cpPeak);
        }
    }

    /**
     * This method fills both match experimental peaks and theoretical peaks
     * doesFindMatchedPeaksLessPrecise=false option
     *
     * @param matched_theoretical_and_matched_peaks
     */
    public void fillForAllFoundPeaks(HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks) {
        for (CPeptidePeak cp : matched_theoretical_and_matched_peaks.keySet()) {
            matchedTheoXLPeaks.add(cp);
            for (MatchedPeak mp : matched_theoretical_and_matched_peaks.get(cp)) {
                matchedPeaks.add(mp.getMatchedPeak());
            }
        }
    }

    /**
     * This method returns explained intensities. If for one theoretical peak,
     * there is only one experimental peak selected, then they only sum this up.
     * Else if for one theoretical peak, there are more than one experimental
     * peak selected, it calculates a weight of that experimental peak as:
     * |diff-fragTol|/fragTol (value range [0:1] and then this value will be
     * divided by number of experimental peaks.
     *
     *
     * @param matched_theoretical_and_matched_peaks
     * @param fragTol fragment tolerance to calculate weight for explained
     * intensity
     * @return
     */
    public static double getWeightedExplainedIntensities(HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks, double fragTol) {
        double expInt = 0;
        for (CPeptidePeak cp : matched_theoretical_and_matched_peaks.keySet()) {
            ArrayList<MatchedPeak> mps = matched_theoretical_and_matched_peaks.get(cp);
            if (mps.size() == 1) {
                expInt += mps.get(0).getMatchedPeak().getIntensity();
            } else {
                for (int i = 0; i < mps.size(); i++) {
                    MatchedPeak tmpmp = mps.get(i);
                    double weight = (Math.abs(tmpmp.getDiff() - fragTol) / fragTol) / mps.size();
                    double weighted_intensity = tmpmp.getMatchedPeak().getIntensity() * weight;
                    expInt += weighted_intensity;
                }
            }
        }
        return expInt;
    }

    /**
     * This method calculates weight based on found theoretical peaks from each
     * peptide
     *
     * @param matchedTheoXLPeaks list of matched theoretical peaks from both
     * peptides
     * @param theoXLPeaksAL list of all theoretical peaks from both peptides
     * @param isCPeptides
     * @return
     */
    public double calculateWeightForTheoPeaks(HashSet<CPeptidePeak> matchedTheoXLPeaks, ArrayList<CPeptidePeak> theoXLPeaksAL, boolean isCPeptides) {
        if (!isCPeptides) {
            return 1;
        }
        matchedTheoPeaksPepA = 0;
        matchedTheoPeaksPepB = 0;
        for (CPeptidePeak tmpCPeak : matchedTheoXLPeaks) {
            int[] vals = check(tmpCPeak.toString(), matchedTheoPeaksPepA, matchedTheoPeaksPepB);
            matchedTheoPeaksPepA = vals[0];
            matchedTheoPeaksPepB = vals[1];
        }
        int theoPepAs = 0,
                theoPepBs = 0;
        for (CPeptidePeak tmpCPeak : theoXLPeaksAL) {
            int[] vals = check(tmpCPeak.toString(), theoPepAs, theoPepBs);
            theoPepAs = vals[0];
            theoPepBs = vals[1];
        }
        fracIonTheoPepA = (double) matchedTheoPeaksPepA / (double) theoPepAs;
        fracIonTheoPepB = (double) matchedTheoPeaksPepB / (double) theoPepBs;
        weight = (fracIonTheoPepA * fracIonTheoPepB);
        return weight;
    }

    public int getMatchedTheoPeaksPepA() {
        return matchedTheoPeaksPepA;
    }

    public void setMatchedTheoPeaksPepA(int matchedTheoPeaksPepA) {
        this.matchedTheoPeaksPepA = matchedTheoPeaksPepA;
    }

    public int getMatchedTheoPeaksPepB() {
        return matchedTheoPeaksPepB;
    }

    public void setMatchedTheoPeaksPepB(int matchedTheoPeaksPepB) {
        this.matchedTheoPeaksPepB = matchedTheoPeaksPepB;
    }

    /**
     * This method check a given theoretical peak to determine whether belongs
     * to peptideA or peptideB
     *
     * @param theoreticalPeakName
     * @param numTheoPeaksPepA
     * @param numTheoPeaksPepB
     * @return a integer array of the first element with numTheoPeaksPepA and
     * the second element with numTheoPeaksPepB
     */
    public static int[] check(String theoreticalPeakName, int numTheoPeaksPepA, int numTheoPeaksPepB) {
        String split[] = theoreticalPeakName.split("--");
        int[] vals = new int[2];
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("A")) {
                numTheoPeaksPepA++;
            } else if (split[i].contains("B")) {
                numTheoPeaksPepB++;
            }
        }
        vals[0] = numTheoPeaksPepA;
        vals[1] = numTheoPeaksPepB;
        return vals;
    }

    /**
     * This method calculates singly charged precursor mass of given expMS2
     * spectrum
     *
     * @param expMS2
     * @return
     */
    private double calculateObservedMass(MSnSpectrum expMS2) {
        double precMass = CalculatePrecursorMass.getPrecursorMass(expMS2),
                protonMass = ElementaryIon.proton.getTheoreticMass(),
                tmp_observed_mass = precMass + protonMass;
        return tmp_observed_mass;
    }

    /**
     * Returning NeutralLossType for given tmpCPeak
     *
     * @param tmpCPeak is a cross-linked peptide peak
     * @return an integer value to show NONEUTRALLOSS,WATERLOSS or AMMONIALOSS
     */
    private int getNeutralLossType(CPeptidePeak tmpCPeak) {
        int code = NONEUTRALLOSS;
        char aa = tmpCPeak.getAa_code();
        switch (aa) {
            case 'D':
            case 'E':
            case 'S':
            case 'T':
                code = WATERLOSS;
                break;

            case 'K':
            case 'N':
            case 'Q':
            case 'R':
                code = AMMONIALOSS;
                break;

        }
        return code;
    }

    /**
     * This method checks if there should be an fragment ion for a neutral loss
     * (in case that a parent ion exist)
     *
     * @param tmpCPeak
     * @param diff
     * @param peak_and_matchedPeak
     * @param matched_theoretical_and_matched_peaks
     * @param filteredPeaks
     * @param neutralLossesType
     * @return
     */
    private MatchedPeak checkForNeutralLosses(CPeptidePeak tmpCPeak, double diff,
            HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak, HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks,
            ArrayList<Peak> filteredPeaks, int neutralLossesType) {
        // a matched peak
        MatchedPeak matched_peak = null;
        // find CPeak with neutral losses..
        double mz = tmpCPeak.getMz() - NeutralLoss.H2O.getMass();
        String name = tmpCPeak.getName() + "°";
        if (neutralLossesType == AMMONIALOSS) {
            mz = tmpCPeak.getMz() - NeutralLoss.NH3.getMass();
            name = tmpCPeak.getName() + "*";
        }
        // aa_code for '+' shows that this is any neutral loss. 
        CPeptidePeak tmpCPeakNeutralLosses = new CPeptidePeak(mz, CrossLinking.intensity_neutralLosses, name, '+', 1);
        for (Peak p : filteredPeaks) {
            matched_peak = match_theoretical_peaks(tmpCPeakNeutralLosses, p, diff, peak_and_matchedPeak, matched_theoretical_and_matched_peaks);
        }
        return matched_peak;
    }
}
