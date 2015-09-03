/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
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
import theoretical.CrossLinkedPeptides;

/**
 *
 * This class finds matched peaks between a pair of an experimental and
 * theoretical spectrum. Then, it calculates a cXPSMScore!
 *
 * @author Sule
 */
public class MatchAndScore {

    private MSnSpectrum expMS2; // experimental MS2 spectrum
    private HashSet<Peak> matchedPeaks = new HashSet<Peak>(); // matched peaks on on an experimental MS2 spectrum with the certain fragment tolerance against theoretical spectrum
    private CrossLinkedPeptides cPeptides; // cross linked peptide object (containing peptideA, peptideB and crosslinker)
    private HashSet<CPeptideIon> theoXLMS2ions = new HashSet<CPeptideIon>(); // theoretical crosslinked ions derived from a cross linked peptide (they have an attribute called mass)
    private HashSet<CPeptidePeak> theoXLPeaks = new HashSet<CPeptidePeak>(), // theoretical crosslinked peaks derived from a cross linked peptide (they have an attribute called MZ, not MASS)
            // TODO: Need to see the performance in terms of object generation!
            matchedTheoXLPeaks = new HashSet<CPeptidePeak>(); // Matched theoretical cross linked peaks
    private ArrayList<CPeptidePeak> theoXLPeaksAL = new ArrayList<CPeptidePeak>(),
            singlyChargedTheoXLPeaksAL = new ArrayList<CPeptidePeak>(),
            doublyChargedTheoXLPeaksAL = new ArrayList<CPeptidePeak>();
    private double fragTol, // fragment tolerance to select
            cXPSMScore = 0, // A CX-PSM Score
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
    /* Constructor */

    public MatchAndScore(MSnSpectrum expMS2, ScoreName scoreName, CrossLinkedPeptides cPeptides, 
            double fragTol, int intensityOption, int minFPeakNum, int maxFPeakNum,
            double massWindow, boolean doesFindAllMatchedPeaks, boolean isPPM) {
        this.expMS2 = expMS2;
        this.scoreName = scoreName;
        this.cPeptides = cPeptides;
        if (cPeptides != null) {
            theoXLMS2ions = cPeptides.getTheoretical_ions();
            theoXLPeaks = getTheoreticalCXPeaks();
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
    public HashSet<CPeptideIon> getTheoreticalCXMS2ions() {
        if (theoXLMS2ions.isEmpty()) {
            theoXLMS2ions = cPeptides.getTheoretical_ions();
        }
        return theoXLMS2ions;
    }

    /**
     * Returns cPeptides object.. A crosslinked peptide pair and their
     * cross-linking information.
     *
     * @return
     */
    public CrossLinkedPeptides getCPeptides() {
        return cPeptides;
    }

    public ArrayList<CPeptidePeak> getTheoreticalCXPeaksAL() {
        if (!isTheoXLPeaksReady) {
            isFoundAndMatched = false;
            getTheoreticalCXPeaks();
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
        theoXLPeaks = getTheoreticalCXPeaks();
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
        cXPSMScore = Double.MIN_VALUE;
        theoXLMS2ions = cPeptides.getTheoretical_ions();
        theoXLPeaks = getTheoreticalCXPeaks();
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
     * approaches
     *
     * TODO: Future - Instead of weight calculation after finding matched peaks,
     * count each matched peak while finding any match in order to speed up
     *
     * @return
     */
    public double getCXPSMScore() {
        if (!isFoundAndMatched) {
            int totalTheoN = getTheoreticalCXPeaks().size(); // all theoretical peaks...
            ArrayList<Double> scores = new ArrayList<Double>();
            for (int numHighestPeak = minFPeaks; numHighestPeak < maxFPeaks; numHighestPeak++) {
                Filter filter = new Filter(expMS2, numHighestPeak, massWindow);
                ArrayList<Peak> filteredPeaks = filter.getFilteredCPeaks();
                Collections.sort(filteredPeaks, Peak.ASC_mz_order);
                double probability = (double) numHighestPeak / (double) (filter.getWindowSize());
                int n = 0;
                HashMap<CPeptidePeak, MatchedPeak> peak_and_matchedPeak = new HashMap<CPeptidePeak, MatchedPeak>();
                HashMap<CPeptidePeak, ArrayList<MatchedPeak>> matched_theoretical_and_matched_peaks = new HashMap<CPeptidePeak, ArrayList<MatchedPeak>>();
                for (Peak p : filteredPeaks) {
                    MatchedPeak mPeak = null;
                    double diff = fragTol;// Based on Da.. not ppm... First it starts with FragmentTolerance
                    for (int i = 0; i < theoXLPeaksAL.size(); i++) {
                        CPeptidePeak tmpCPeak = theoXLPeaksAL.get(i);
                        if (tmpCPeak.getMz() <= (p.mz + fragTol + 0.01)) { // making sure that now missing any theoretical peak..
                            double theoMz = tmpCPeak.getMz(),
                                    expMz = p.getMz(),
                                    tmp_diff = Math.abs(expMz - theoMz);
                            // A theoretical peak which is closest to an experimental peak is selected!
                            // if two peaks are selected and two theoretical peaks withing fragment tolerance, only closest theoretical peak is selected
                            // In case that a peak has matched to two theoretical peaks with the mass tolerance, only the left one is selected
                            if (tmp_diff <= diff && !doesFindAllMatchedPeaks) {
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
                                // from now on we select any peak theoretical peaks/experimental peaks matched to another experimental/theoretical peak within a fragment tolerance
                            } else if (tmp_diff <= diff && doesFindAllMatchedPeaks && matched_theoretical_and_matched_peaks.containsKey(tmpCPeak)) {
                                MatchedPeak mp = new MatchedPeak(p, tmpCPeak, tmp_diff);
                                matched_theoretical_and_matched_peaks.get(tmpCPeak).add(mp);
                            } else if (tmp_diff <= diff && doesFindAllMatchedPeaks && !matched_theoretical_and_matched_peaks.containsKey(tmpCPeak)) {
                                ArrayList<MatchedPeak> tmps = new ArrayList<MatchedPeak>();
                                tmps.add(new MatchedPeak(p, tmpCPeak, tmp_diff));
                                matched_theoretical_and_matched_peaks.put(tmpCPeak, tmps);
                            }
                        }
                    }
                }
                matchedPeaks = new HashSet<Peak>();
                matchedTheoXLPeaks = new HashSet<CPeptidePeak>();
                double explainedIntensities = 0,
                        intensities = getIntensities(filteredPeaks); // sum up all intensities from filtered peaks list
                if (!doesFindAllMatchedPeaks) {
                    fillForClosestPeak(peak_and_matchedPeak); // so matchedPeaks and matchedTheoreticalCXPeakscan be filled here...
                    explainedIntensities = getExplainedIntensities(matchedPeaks); // sum up all intensities from matched experimental peaks
                } else {
                    fillForAllFoundPeaks(matched_theoretical_and_matched_peaks);
                    explainedIntensities = getWeightedExplainedIntensities(matched_theoretical_and_matched_peaks, fragTol);
                }
                n = matchedPeaks.size();
                // MSAmanda_derived with expertimentatl spectrum
                if (scoreName.equals(ScoreName.MSAmandaD)) {
                    MSAmanda_derived object = new MSAmanda_derived(probability, filter.getFilteredCPeaks().size(), n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                    // Andromeda_derived with theoretical spectra size
                } else if (scoreName.equals(ScoreName.AndromedaD)) {
                    Andromeda_derived object = new Andromeda_derived(probability, totalTheoN, n);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                    // MSAmanda_derived with theoretical spectra size
                } else if (scoreName.equals(ScoreName.TheoMSAmandaD)) {
                    MSAmanda_derived object = new MSAmanda_derived(probability, totalTheoN, n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                } else if (scoreName.equals(ScoreName.AndromedaDWeighted)) {
                    calculateWeightForTheoPeaks(matchedTheoXLPeaks, theoXLPeaksAL, isCPeptide);
                    Andromeda_derived object = new Andromeda_derived(probability, totalTheoN, n, weight);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                } else if (scoreName.equals(ScoreName.TheoMSAmandaDWeighted)) {
                    calculateWeightForTheoPeaks(matchedTheoXLPeaks, theoXLPeaksAL, isCPeptide);
                    MSAmanda_derived object = new MSAmanda_derived(probability, totalTheoN, n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived, scoreName, weight);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                }
            }
            isFoundAndMatched = true;
            cXPSMScore = Collections.max(scores);
            // now calculate observed mass and mass error
            observedMass = calculateObservedMass(expMS2);
            // now calculates MS1 error with precursor mass and theoretical mass of crosslinked peptide
            ms1Err = calculateMS1Err(isPPM, CalculatePrecursorMass.getPrecursorMass(expMS2), cPeptides.getTheoretical_xlinked_mass());
            absMS1Err = Math.abs(ms1Err);
        }
        return cXPSMScore;
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
            getCXPSMScore();
        }
        return matchedPeaks;
    }

    /**
     * Returns matched theoretical CXPeaks. Unless a CXPSMScore is calculated,
     * then it starts with this then returns a list of CPeptidePeak
     *
     * @return
     */
    public HashSet<CPeptidePeak> getMatchedTheoreticalCPeaks() {
        if (!isFoundAndMatched) {
            getCXPSMScore();
        }
        return matchedTheoXLPeaks;
    }

    public void setMatchedTheoreticalCPeaks(HashSet<CPeptidePeak> matchedTheoPeaks) {
        this.matchedTheoXLPeaks = matchedTheoPeaks;
    }

    /**
     * This method returns a list of CPeptidePeak with singly and doubly charged
     * ions. Peaks with the same m/z may come from the different ions (due to
     * different charge state) and so such peaks are collapsed into one single
     * peak with combined/updated name
     *
     * So it returns a hashset named theoXLPeaks containing mz values for all
     * singly and doubly charged theoretical CXIons
     *
     *
     * @return
     */
    public HashSet<CPeptidePeak> getTheoreticalCXPeaks() {
        if (!isTheoXLPeaksReady) {
            theoXLPeaks = new HashSet<CPeptidePeak>();
            theoXLMS2ions = getTheoreticalCXMS2ions();
            HashSet<CPeptidePeak> cPeakList = new HashSet<CPeptidePeak>();
            HashMap<CPeptidePeak, Double> singlyCharged_peak_and_mz = new HashMap<CPeptidePeak, Double>();
            for (CPeptideIon c : theoXLMS2ions) {
                // singly and doubly charged ones..
                String name = "singlyCharged_" + c.getName();
                double singly_mz = c.get_theoretical_mz(1);
                CPeptidePeak singly_charged = new CPeptidePeak(singly_mz, c.getIntensity(), 1, name);
                singlyCharged_peak_and_mz.put(singly_charged, singly_mz);
                cPeakList.add(singly_charged);
                singlyChargedTheoXLPeaksAL.add(singly_charged);
            }

            // if precursor charge is bigger than one, add only doubly charged ions
            if (getPrecursorCharge() > 1) {
                for (CPeptideIon c : theoXLMS2ions) {
                    String name = "doublyCharged_" + c.getName();
                    double doubly_mz = c.get_theoretical_mz(2);
                    CPeptidePeak doubly_charged = new CPeptidePeak(doubly_mz, c.getIntensity(), 2, name);
                    if (singlyCharged_peak_and_mz.containsValue(doubly_mz)) {
                        for (CPeptidePeak cP : singlyCharged_peak_and_mz.keySet()) {
                            if (singlyCharged_peak_and_mz.get(cP) == doubly_mz) {
                                name = cP.getName() + "_" + name; // update name by combining...
                                cP.setName(name);
                            }
                        }
                    } else {
                        cPeakList.add(doubly_charged);
                        doublyChargedTheoXLPeaksAL.add(doubly_charged);
                    }
                }
            }
            theoXLPeaks.addAll(cPeakList);
            theoXLPeaksAL = new ArrayList<CPeptidePeak>(theoXLPeaks);
            Collections.sort(theoXLPeaksAL, CPeptidePeak.Peak_ASC_mz_order);
            Collections.sort(singlyChargedTheoXLPeaksAL, CPeptidePeak.Peak_ASC_mz_order);
            Collections.sort(doublyChargedTheoXLPeaksAL, CPeptidePeak.Peak_ASC_mz_order);
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
        String split[] = theoreticalPeakName.split("_");
        int[] vals = new int[2];
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("pepA")) {
                numTheoPeaksPepA++;
            } else if (split[i].equals("pepB")) {
                numTheoPeaksPepB++;
            } else if (split[i].equals("lepA") && !split[i + 1].startsWith("mono")) {
                numTheoPeaksPepA++;
            } else if (split[i].equals("lepB") && !split[i + 1].startsWith("mono")) {
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
                observedMass = precMass + protonMass;
        return observedMass;
    }

    /**
     * This method calculates difference between an observed and calculated
     * mass.
     *
     * @param isPPM true: ms1Err is in PPM
     * @param observedMass
     * @param calculatedMass
     * @return
     */
    private double calculateMS1Err(boolean isPPM, double observedMass, double calculatedMass) {
        double mS1Err = CalculateMS1Err.getMS1Err(isPPM, observedMass, calculatedMass);
        return mS1Err;
    }

}
