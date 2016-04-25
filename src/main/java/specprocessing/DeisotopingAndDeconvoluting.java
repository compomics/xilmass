/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specprocessing;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class does two processes: - deisotoping and followed by charge state deconvoluting
 *
 * @author Sule
 */
public class DeisotopingAndDeconvoluting {

    private MSnSpectrum expMSnSpectrum; // experimental spectrum to be deisotoped and deonvulated
    private boolean isDeisotopedDeconvoluted = false; // a control to check if this is already processed
    private double deisotopePrecision, // the tolerance between C12 peak and C12withoneC13 peak
            deconvolutePrecision; // the precision to select if singly charged and deconvulated peak exist within this precision value
    private static double diffC12C13 = 1.0034; // difference between C12-C13 (Da)

    /**
     * To construct an instance 
     * @param expMSnSpectrum is an experimental MS/MS spectrum 
     * @param deisotopDiff accuracy for deisotoping 
     * @param deconvolutePrecision precision of charge state deconvolution 
     */
    public DeisotopingAndDeconvoluting(MSnSpectrum expMSnSpectrum, double deisotopDiff, double deconvolutePrecision) {
        this.expMSnSpectrum = expMSnSpectrum;
        this.deisotopePrecision = deisotopDiff;
        this.deconvolutePrecision = deconvolutePrecision;
    }

    /**
     * This method returns an MSnSpectrum object that is already deisotoped and
     * deconvulated
     *
     * @return
     */
    public MSnSpectrum getDeisotopedDeconvolutedExpMSnSpectrum() {
        if (!isDeisotopedDeconvoluted) {
            deisotope_and_deconvolute();
        }
        return expMSnSpectrum;
    }

    /* Getter and setter methods */
    public MSnSpectrum getExpMSnSpectrum() {
        return expMSnSpectrum;
    }

    public void setExpMSnSpectrum(MSnSpectrum expMSnSpectrum) {
        this.expMSnSpectrum = expMSnSpectrum;
        isDeisotopedDeconvoluted = false;
    }

    public boolean isIsDeisotopedDeconvoluted() {
        return isDeisotopedDeconvoluted;
    }

    public void setIsDeisotopedDeconvoluted(boolean isDeisotopedDeconvoluted) {
        this.isDeisotopedDeconvoluted = isDeisotopedDeconvoluted;
    }

    public double getDeisotopePrecision() {
        return deisotopePrecision;
    }

    public void setDeisotopePrecision(double deisotopePrecision) {
        this.deisotopePrecision = deisotopePrecision;
    }

    public double getDeconvulatePrecision() {
        return deconvolutePrecision;
    }

    public void setDeconvulatePrecision(double deconvulatePrecision) {
        this.deconvolutePrecision = deconvulatePrecision;
    }

    public static double getDiffC12C13() {
        return diffC12C13;
    }

    public static void setDiffC12C13(double diffC12C13) {
        DeisotopingAndDeconvoluting.diffC12C13 = diffC12C13;
    }

    /**
     * This method de-isotoping for isotope envelope and deconvolute charge
     * state to singly charged peak. Intensities of isotopes are added to singly
     * charged C12 peak
     *
     */
    public void deisotope_and_deconvolute() {
        // get every peak
        ArrayList<Double> mzs = new ArrayList<Double>(expMSnSpectrum.getPeakMap().keySet());
        HashMap<Double, Peak> toUpdateMap = new HashMap<Double, Peak>(),
                toRemoveMap = new HashMap<Double, Peak>(),
                deconvulateds = new HashMap<Double, Peak>();
        // an array that holds all checked indices..
        int[] checked = new int[mzs.size()];
        // a peak cannot have higher than its precursor charge state.
        int maxPrecursorCharge = getMaxPrecursorCharge();
        Collections.sort(mzs); // mz values from smaller to higher
        for (int i = 0; i < mzs.size(); i++) {
            Peak tmpPeak = expMSnSpectrum.getPeakMap().get(mzs.get(i));
            // find possible next isotope candidate..
            for (int next = i + 1; next < mzs.size(); next++) {
                Peak nextPeak = expMSnSpectrum.getPeakMap().get(mzs.get(next));
                double diff = Math.abs(tmpPeak.mz - nextPeak.mz);
                if (diff > 1.5) {
                    break;
                    // if it is within the range and has not been checked yet...
                } else if (checked[next] != 1) {
                    boolean isNextIsotope = false;
                    // first find if the next one is indeed isotope and its charge state
                    int isotopeCharge = 0;
                    Peak toAdd = null;
                    // check for every possible charge state of a given peak...
                    for (int tmpCharge = 1; tmpCharge < maxPrecursorCharge; tmpCharge++) {
                        if (Math.abs(diff - (diffC12C13 / tmpCharge)) < deisotopePrecision) {
                            // so it is indeed C13 peaks.. so update the tmpPeak with adding intensity of nextPeak and remove nextPeak 
                            toAdd = new Peak(tmpPeak.mz, tmpPeak.intensity + nextPeak.intensity);
                            toRemoveMap.put(nextPeak.mz, nextPeak);
                            isNextIsotope = true;
                            isotopeCharge = tmpCharge;
                            checked[i] = 1;
                            checked[next] = 1;
                        }
                    }
                    // oki let's continue if this is indeed an isotope
                    if (isNextIsotope) {
                        double isotopicShift = (diffC12C13 / isotopeCharge);
                        // check the following ones to find further isotopes from that C12 peak
                        boolean doesFind = true;
                        double nextPossIsotop = nextPeak.mz + isotopicShift;
                        while (doesFind) {
                            doesFind = false;
                            // check if there is
                            for (int indNextPossIsotop = i + 2; indNextPossIsotop < mzs.size(); indNextPossIsotop++) {
                                Peak nextPossIsotopPeak = expMSnSpectrum.getPeakMap().get(mzs.get(indNextPossIsotop));
                                double tmpdiff = Math.abs(nextPossIsotopPeak.mz - nextPossIsotop);
                                if (tmpdiff < deisotopePrecision && checked[indNextPossIsotop] == 0) {
                                    doesFind = true;
                                    // so it is indeed C13 peaks.. so update the tmpPeak with adding intensity of nextPeak and remove nextPeak 
                                    toAdd = new Peak(toAdd.mz, toAdd.intensity + nextPossIsotopPeak.intensity);
                                    toRemoveMap.put(nextPossIsotopPeak.mz, nextPossIsotopPeak);
                                    checked[indNextPossIsotop] = 1;
                                    nextPossIsotop = nextPossIsotopPeak.mz + isotopicShift;
                                } else if (tmpdiff > 1.5) {
                                    break;
                                }
                            }
                        }
                    }
                    // the final step: charge state deconvulation
                    if (toAdd != null && isotopeCharge > 1) {
                        Peak singleToAdd = deconvolute(toAdd, isotopeCharge, deconvulateds);
                        toRemoveMap.put(toAdd.mz, toAdd);
                        toUpdateMap.put(singleToAdd.mz, singleToAdd);
                    } else if (toAdd != null && isotopeCharge == 1) {
                        toUpdateMap.put(toAdd.mz, toAdd);
                    }
                }
            }
        }
        ArrayList<Peak> peaks = new ArrayList<Peak>();
        // update and remove
        for (Double tmpmz : expMSnSpectrum.getPeakMap().keySet()) {
            if (!toRemoveMap.containsKey(tmpmz) && !toUpdateMap.containsKey(tmpmz)) {
                peaks.add(expMSnSpectrum.getPeakMap().get(tmpmz));
            }
        }
        // now clean and update existing peak list on an spectrum object..
        peaks.addAll(toUpdateMap.values());
        expMSnSpectrum.getPeakList().clear();
        expMSnSpectrum.getPeakMap().clear();
        for (Peak p : peaks) {
            expMSnSpectrum.addPeak(p);
        }
        isDeisotopedDeconvoluted = true;
    }

    /**
     * This method deconvolutes of given toAdd peak, according to its
     * toAddCharge charge state. First, it calculates singly charged m/z value
     * of toAdd peak, and then it checks if any peak with the same singly
     * charged m/z is previously deconvoluted (checking deconvoluteds with
     * deconvolutePrecision). If toAdd is previously deconvoluted, the
     * corresponding peak is updated; otherwise a new peak is constructed.
     *
     * @param toAdd
     * @param toAddCharge
     * @param deconvoluteds
     * @return
     */
    public Peak deconvolute(Peak toAdd, int toAddCharge, HashMap<Double, Peak> deconvoluteds) {
        Peak deconvulated = null;
        boolean doesExist = false,
                isAlreadyConvulated = false;
        double mz = toAdd.mz,
                multipliedMZ = mz * toAddCharge,
                theoProton = ElementaryIon.proton.getTheoreticMass(),
                multipliedProton = toAddCharge * theoProton,
                mass = multipliedMZ - multipliedProton,
                singlyMZ = mass + theoProton,
                intensity = toAdd.intensity;
        for (Double tmpmz : deconvoluteds.keySet()) {
            double diff = Math.abs(tmpmz - singlyMZ);
            // just making sure that it is the same peak
            if (diff < 0.0000000001) {
                isAlreadyConvulated = true;
                Peak p = deconvoluteds.get(tmpmz);
                p.setIntensity(p.intensity + intensity);
                deconvulated = p;
            }
        }
        if (!isAlreadyConvulated) {
            for (Double tmpmz : expMSnSpectrum.getPeakMap().keySet()) {
                if (Math.abs(tmpmz - singlyMZ) < deconvolutePrecision && !doesExist) {
                    // so update its intensity
                    deconvulated = new Peak(tmpmz, expMSnSpectrum.getPeakMap().get(tmpmz).intensity + toAdd.intensity);
                    doesExist = true;
                    break;
                }
            }
            if (!doesExist) {
                deconvulated = new Peak(singlyMZ, intensity);
            }
        }
        return deconvulated;
    }

    /**
     * This method finds the maximum precursor charge from all possible
     * charges..
     *
     * @return
     */
    private int getMaxPrecursorCharge() {
        // get PM charge value!
        ArrayList<Charge> possibleCharges = expMSnSpectrum.getPrecursor().getPossibleCharges();
        int maxCharge = 0;
        for (Charge ch : possibleCharges) {
            if (maxCharge < ch.value) {
                maxCharge = ch.value;
            }
        }
        return maxCharge;
    }

    /**
     * Returns the mass of a precursor ion according to the maximum precursor
     * charge value
     *
     * @return the mass of the compound with the given charge
     */
    public double getPrecurMass() {
        int maxPrecursorCharge = getMaxPrecursorCharge();
        double precursorMZ = expMSnSpectrum.getPrecursor().getMz();
        double multipliedMZ = precursorMZ * maxPrecursorCharge,
                theoProton = ElementaryIon.proton.getTheoreticMass(),
                multipliedProton = maxPrecursorCharge * theoProton,
                mass = multipliedMZ - multipliedProton;
        return mass;
    }

    /**
     * This method returns a precursor m/z value for given charge of a given
     * precursorMass
     *
     * @param precursorMass
     * @param charge
     * @return
     */
    public double getMZ(double precursorMass, int charge) {
        double mz = (precursorMass + (ElementaryIon.proton.getTheoreticMass() * charge)) / charge;
        return mz;
    }

    /**
     * This method calculates mz value with otherCharge state for the given
     * tmp_mz value
     *
     * @param tmp_mz
     * @param charge
     * @param otherCharge
     * @return
     */
    private double getMZIncreasedCharge(double tmp_mz, int charge, int otherCharge) {
        double nextMZ = (((charge * (tmp_mz - ElementaryIon.proton.getTheoreticMass())) / otherCharge) + ElementaryIon.proton.getTheoreticMass());
        return nextMZ;
    }

    private Peak find(double tmp_mz_increased_charge) {
        Peak foundPeak = null;
        Iterator<Peak> peakIterator = expMSnSpectrum.getPeakList().iterator();
        while (peakIterator.hasNext()) {
            Peak tmpPeak = peakIterator.next();
            double tmp_mz = tmpPeak.getMz();
            double diff = Math.abs(tmp_mz - tmp_mz_increased_charge);
            if (diff < 1) {
                System.out.println(tmp_mz + "\t" + tmp_mz_increased_charge + "\t" + "Find..." + diff);
            }
            if (diff < 0.00000001) {
                foundPeak = tmpPeak;
            }
        }
        System.out.print("\n");
        return foundPeak;
    }

}
