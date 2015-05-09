/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import scoringFunction.Andromeda_derived;
import scoringFunction.MSAmanda_derived;
import theoretical.CPeptideIon;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;

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
    private CPeptides cPeptides; // cross linked peptide object (containing peptideA, peptideB and crosslinker)
    private HashSet<CPeptideIon> theoreticalCXMS2ions = new HashSet<CPeptideIon>(); // theoretical crosslinked ions derived from a cross linked peptide (they have an attribute called mass)
    private HashSet<CPeptidePeak> theoreticalCXPeaks = new HashSet<CPeptidePeak>(), // theoretical crosslinked peaks derived from a cross linked peptide (they have an attribute called MZ, not MASS)
            // TODO: Need to see the performance in terms of object generation!
            matchedTheoreticalCXPeaks = new HashSet<CPeptidePeak>(); // Matched theoretical cross linked peaks
    private ArrayList<CPeptidePeak> theoreticalCXPeaksAL = new ArrayList<CPeptidePeak>();
    private double fragTol, // fragment tolerance to select 
            cXPSMScore = 0, // A CX-PSM Score 
            massWindow = 100; // Mass window to filter out peaks from a given MSnSpectrum
    private int scoring_type, // 0-MSAmanda_derived (MSAmanda_derived with N=AllPickedPeaks), 1-Andromeda_derived, 2-TheoMSAmanda (MSAmanda_derived with N=AllTheoPeaks)
            intensityOptionForMSAmandaDerived = 0,
            minFPeaks, // Minimum number of filtered peaks per 100Da mass window.. (To test here)            
            maxFPeaks; // Maximum number of filtered peaks per 100Da mass window.. (To test)
    private boolean isTheoreticalCXPeaksReady = false,
            isFoundAndMatched = false;

    /* Constructor */
    public MatchAndScore(MSnSpectrum expMS2, int scoring, CPeptides cPeptides, double fragTol, int intensityOption, int minFPeakNum, int maxFPeakNum, double massWindow) {
        this.expMS2 = expMS2;
        this.scoring_type = scoring;
        this.cPeptides = cPeptides;
        if (cPeptides != null) {
            theoreticalCXMS2ions = cPeptides.getTheoterical_ions();
            theoreticalCXPeaks = getTheoreticalCXPeaks();
            isTheoreticalCXPeaksReady = true;
        }
        this.fragTol = fragTol;
        this.intensityOptionForMSAmandaDerived = intensityOption;
        this.minFPeaks = minFPeakNum;
        this.maxFPeaks = maxFPeakNum;
        this.massWindow = massWindow;
    }

    /* getters and setters */
    public MSnSpectrum getExpMS2() {
        return expMS2;
    }

    /**
     * Returns an hashset named theoreticalCXMS2ions which contains mass values
     * derived from a cross-linked peptide. Here charge state information is not
     * considered!
     *
     * @return
     */
    public HashSet<CPeptideIon> getTheoreticalCXMS2ions() {
        if (theoreticalCXMS2ions.isEmpty()) {
            theoreticalCXMS2ions = cPeptides.getTheoterical_ions();
        }
        return theoreticalCXMS2ions;
    }

    /**
     * Returns cPeptides object.. A crosslinked peptide pair and their
     * cross-linking information.
     *
     * @return
     */
    public CPeptides getCPeptides() {
        return cPeptides;
    }

    public ArrayList<CPeptidePeak> getTheoreticalCXPeaksAL() {
        if (!isTheoreticalCXPeaksReady) {
            isFoundAndMatched = false;
            getTheoreticalCXPeaks();
        }
        return theoreticalCXPeaksAL;
    }

    public void setExpMS2(MSnSpectrum expMS2) {
        isFoundAndMatched = false;
        this.expMS2 = expMS2;
    }

    /**
     * It sets theoreticalCXMS2ions from a given HashSet object...
     *
     * @param theoreticalCXMS2ions
     */
    public void setTheoreticalCXMS2ions(HashSet<CPeptideIon> theoreticalCXMS2ions) {
        this.theoreticalCXMS2ions = theoreticalCXMS2ions;
        theoreticalCXPeaks = getTheoreticalCXPeaks();
    }

    /**
     * It set a CPeptides object while setting back to all other attributes
     *
     * @param cPeptides
     */
    public void setCPeptides(CPeptides cPeptides) {
        isTheoreticalCXPeaksReady = false;
        this.cPeptides = cPeptides;
        matchedPeaks = new HashSet<Peak>();
        cXPSMScore = Double.MIN_VALUE;
        theoreticalCXMS2ions = cPeptides.getTheoterical_ions();
        theoreticalCXPeaks = getTheoreticalCXPeaks();
        matchedTheoreticalCXPeaks = new HashSet<CPeptidePeak>();
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
    public int getScoring_Type() {
        return scoring_type;
    }

    /**
     * This method selects experimental peaks closed to generated theoretical
     * peaks within given fragment tolerance. It then calculates cumulative
     * binomial probability based scoring_type based on three different
     * approaches
     *
     * @return
     */
    public double getCXPSMScore() {
        if (!isFoundAndMatched) {
            int totalN = getTheoreticalCXPeaks().size();
            ArrayList<Double> scores = new ArrayList<Double>();
            for (int numHighestPeak = minFPeaks; numHighestPeak < maxFPeaks; numHighestPeak++) {
                matchedPeaks = new HashSet<Peak>();
                Filter filter = new Filter(expMS2, numHighestPeak, massWindow);
                ArrayList<Peak> filteredPeaks = filter.getFilteredCPeaks();
                double probability = (double) numHighestPeak / (double) (filter.getWindowSize());
                int n = 0;
                double intensities = 0,
                        explainedIntensities = 0;
                boolean are_intensities_ready = false;
                for (Peak p : filteredPeaks) {
                    Peak matchedPeak = null;
                    CPeptidePeak matchedCPeak = null;
                    for (int i = 0; i < theoreticalCXPeaksAL.size(); i++) {
                        CPeptidePeak tmpCPeak = theoreticalCXPeaksAL.get(i);
                        if (tmpCPeak.getMz() < (p.mz + 20)) {
                            double theoMz = tmpCPeak.getMz();
                            double diff = fragTol;// Based on Da.. not ppm...
                            double tmpMz = p.getMz(),
                                    tmpIntensity = p.getIntensity();
                            if (!are_intensities_ready) {
                                intensities += tmpIntensity;
                            }
                            double tmp_diff = (tmpMz - theoMz);
                            // A theoretical peak which is closest to an experimental peak is selected! 
                            // In case that a peak has matched to two theoretical peaks with the mass tolerance, only the left one is selected
                            if (Math.abs(tmp_diff) < diff) {
                                diff = Math.abs(tmp_diff);
                                if (tmpCPeak.getDiff() >= diff) {
                                    matchedPeak = p;
                                    matchedCPeak = tmpCPeak;
                                    tmpCPeak.setDiff(diff);
                                }
                            }
                        }
                    }
                    are_intensities_ready = true;
                    if (matchedPeak != null) {
                        matchedTheoreticalCXPeaks.add(matchedCPeak);
                        matchedPeaks.add(matchedPeak);
                        explainedIntensities += matchedPeak.intensity;
                    }
                }
                n = matchedPeaks.size();
                // MSAmanda_derived with expertimentatl spectrum
                if (scoring_type == 0) {
                    MSAmanda_derived object = new MSAmanda_derived(probability, filter.getFilteredCPeaks().size(), n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                    // Andromeda_derived with theoretical spectra size
                } else if (scoring_type == 1) {
                    Andromeda_derived object = new Andromeda_derived(probability, totalN, n);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                    // MSAmanda_derived with theoretical spectra size
                } else if (scoring_type == 2) {
                    MSAmanda_derived object = new MSAmanda_derived(probability, totalN, n, intensities, explainedIntensities, intensityOptionForMSAmandaDerived);
                    double tmp_score = object.getScore();
                    scores.add(tmp_score);
                }
            }
            isFoundAndMatched = true;
            cXPSMScore = Collections.max(scores);
        }
        return cXPSMScore;
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
        return matchedTheoreticalCXPeaks;
    }

    public void setMatchedTheoreticalCPeaks(HashSet<CPeptidePeak> matchedTheoPeaks) {
        this.matchedTheoreticalCXPeaks = matchedTheoPeaks;
    }

    /**
     * This method returns a list of CPeptidePeak with singly and doubly charged
     * ions. Because, peaks with the same m/z may come from the different ions
     * (due to different charge state). Here such peaks are collapsed into one
     * single peak with combined/updated name
     *
     * So it returns a hashset named theoreticalCXPeaks which contains mz values
     * derived from all singly and doubly charged theoretical CXIons
     *
     *
     * @param theoCMS2ions
     * @return
     */
    private HashSet<CPeptidePeak> getTheoreticalCXPeaks() {
        if (!isTheoreticalCXPeaksReady) {
            theoreticalCXPeaks = new HashSet<CPeptidePeak>();
            HashSet<CPeptidePeak> cPeakList = new HashSet<CPeptidePeak>();
            HashMap<CPeptidePeak, Double> singlyCharged_peak_and_mz = new HashMap<CPeptidePeak, Double>();
            for (CPeptideIon c : getTheoreticalCXMS2ions()) {
                // singly and doubly charged ones..
                String name = "singlyCharged_" + c.getName();
                double singly_mz = c.get_theoretical_mz(1);
                CPeptidePeak singly_charged = new CPeptidePeak(singly_mz, c.getIntensity(), 1, name);
                singlyCharged_peak_and_mz.put(singly_charged, singly_mz);
                cPeakList.add(singly_charged);
            }
            for (CPeptideIon c : getTheoreticalCXMS2ions()) {
                String name = "doublyCharged_" + c.getName();
                double doubly_mz = c.get_theoretical_mz(2);
                CPeptidePeak doubly_charged = new CPeptidePeak(doubly_mz, c.getIntensity(), 2, name);
                if (singlyCharged_peak_and_mz.containsValue(doubly_mz)) {
                    for (CPeptidePeak cP : singlyCharged_peak_and_mz.keySet()) {
                        if (singlyCharged_peak_and_mz.get(cP) == doubly_mz) {
                            name = cP.getName() + "_" + name;
                            doubly_charged.setName(name);
                            cPeakList.remove(cP);
                        }
                    }
                }
                cPeakList.add(doubly_charged);
            }
            theoreticalCXPeaks.addAll(cPeakList);

            theoreticalCXPeaksAL = new ArrayList<CPeptidePeak>(theoreticalCXPeaks);
            Collections.sort(theoreticalCXPeaksAL, CPeptidePeak.Peak_ASC_mz_order);

            isTheoreticalCXPeaksReady = true;
            return cPeakList;
        } else {
            return theoreticalCXPeaks;
        }
    }

}
