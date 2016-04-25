/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package precursorRemoval;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;
import specprocessing.DeisotopingAndDeconvoluting;
import start.CalculatePrecursorMass;

/**
 * This class removes any isotopic peaks derived from precursor peaks. This does
 * a smart filter as explained in Mascot.
 * http://www.matrixscience.com/help/search_field_help.html
 *
 * @author Sule
 */
public class MascotAdaptedPrecursorPeakRemoval extends PrecursorPeakRemoval {

    private static int MrSmaller1000AndIsotopes = 3, // Mr (relative calculated precursor mass from observed prec m/z depending on the  observed charge state) - and corresponding observed isotopic peaks
            Mr1000_2000AndIsotopes = 4,
            Mr2000_3000AndIsotopes = 5,
            Mr3000_4000AndIsotopes = 6,
            Mr4000_5000AndIsotopes = 7,
            Mr5000_6000AndIsotopes = 8,
            Mr6000_7000AndIsotopes = 9,
            MrBigger7000AndIsotopes = 10;
    private static double c13diff = DeisotopingAndDeconvoluting.getDiffC12C13(); // C12-C13 differences (Da). 
    private boolean arePrecursorPeaksRemoved = false;

    /**
     * Construct an instance
     *
     * @param msms is an experimental MS/MS spectrum object
     * @param fragmentTolerance fragment tolerance in Da to find fragment ions
     * derived from precursor and its isotopes
     */
    public MascotAdaptedPrecursorPeakRemoval(MSnSpectrum msms, double fragmentTolerance) {
        super(msms, fragmentTolerance);
    }

    /**
     * This class removes any isotope peak derived from precursor ion. For
     * details, check http://www.matrixscience.com/help/search_field_help.html
     * (Precursor removal)
     *
     */
    @Override
    public void removePrecursor() {
        double precursorMass = CalculatePrecursorMass.getPrecursorMass(ms);
        // decide on how many notches (#isotopes) needs to be removed...
        int notches_to_remove = MrSmaller1000AndIsotopes;
        if (precursorMass >= 1000.00 && precursorMass < 2000.00) {
            notches_to_remove = Mr1000_2000AndIsotopes;
        } else if (precursorMass >= 2000.00 && precursorMass < 3000.00) {
            notches_to_remove = Mr2000_3000AndIsotopes;
        } else if (precursorMass >= 3000.00 && precursorMass < 4000.00) {
            notches_to_remove = Mr3000_4000AndIsotopes;
        } else if (precursorMass >= 4000.00 && precursorMass < 5000.00) {
            notches_to_remove = Mr4000_5000AndIsotopes;
        } else if (precursorMass >= 5000.00 && precursorMass < 6000.00) {
            notches_to_remove = Mr5000_6000AndIsotopes;
        } else if (precursorMass >= 6000.00 && precursorMass < 7000.00) {
            notches_to_remove = Mr6000_7000AndIsotopes;
        } else if (precursorMass >= 7000.00) {
            notches_to_remove = MrBigger7000AndIsotopes;
        }
        double prec_mz = ms.getPrecursor().getMz();
        // note that if there is a spectrum with more than one precursor charge, only the first one is taken into account.
        int ms_charge = ms.getPrecursor().getPossibleCharges().get(0).value;
        double isotope_shift = 0;
        ArrayList<Peak> peaksToRemove = new ArrayList<Peak>(),
                peaks = new ArrayList<Peak>(ms.getPeakList());
        for (int tmp_notch = 0; tmp_notch < notches_to_remove; tmp_notch++) {
            double lower = prec_mz + isotope_shift - fragmentTolerance,
                    upper = prec_mz + isotope_shift + fragmentTolerance;
            // remove peaks within these two limits...
            ArrayList<Peak> tmpPeaksToRemove = removeNotch(peaks, lower, upper, fragmentTolerance);
            peaksToRemove.addAll(tmpPeaksToRemove);
            isotope_shift += (c13diff / (double) ms_charge);
        }
        // now clear the peak list from possibly derived from precursor peaks
        peaks.removeAll(peaksToRemove);
        ms.getPeakList().clear();
        ms.setMzOrdered(false);
        ms.setPeaks(peaks);
        arePrecursorPeaksRemoved = true;
    }

    /**
     * This method removes a notch within the lower and upper mass offsets on
     * given spectrum (peak lists), by taking into account of fragment tolerance
     *
     * @param peaks a list of experimental peaks from given MS/MS spectrum
     * @param lower a mass offset in the beginning of the notch (Da)
     * @param upper a mass offset in the end of the notch (Da)
     * @param fragTol a fragment tolerance to select peaks (Da)
     * @return
     */
    public static ArrayList<Peak> removeNotch(ArrayList<Peak> peaks, double lower, double upper, double fragTol) {
        ArrayList<Peak> peaksToRemove = new ArrayList<Peak>();
        Collections.sort(peaks, Peak.AscendingMzComparator);
        for (Peak tmpPeak : peaks) {
            double tmpMZ = tmpPeak.getMz();
            if (tmpPeak.getMz() >= lower && tmpPeak.getMz() <= upper) {
                peaksToRemove.add(tmpPeak);
            }
            if (tmpMZ > upper + fragTol) {
                break;
            }
        }
        return peaksToRemove;
    }

    /*getter and setter methods */
    public static int getMrSmaller1000AndIsotopes() {
        return MrSmaller1000AndIsotopes;
    }

    public static void setMrSmaller1000AndIsotopes(int MrSmaller1000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.MrSmaller1000AndIsotopes = MrSmaller1000AndIsotopes;
    }

    public static int getMr1000_2000AndIsotopes() {
        return Mr1000_2000AndIsotopes;
    }

    public static void setMr1000_2000AndIsotopes(int Mr1000_2000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr1000_2000AndIsotopes = Mr1000_2000AndIsotopes;
    }

    public static int getMr2000_3000AndIsotopes() {
        return Mr2000_3000AndIsotopes;
    }

    public static void setMr2000_3000AndIsotopes(int Mr2000_3000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr2000_3000AndIsotopes = Mr2000_3000AndIsotopes;
    }

    public static int getMr3000_4000AndIsotopes() {
        return Mr3000_4000AndIsotopes;
    }

    public static void setMr3000_4000AndIsotopes(int Mr3000_4000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr3000_4000AndIsotopes = Mr3000_4000AndIsotopes;
    }

    public static int getMr4000_5000AndIsotopes() {
        return Mr4000_5000AndIsotopes;
    }

    public static void setMr4000_5000AndIsotopes(int Mr4000_5000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr4000_5000AndIsotopes = Mr4000_5000AndIsotopes;
    }

    public static int getMr5000_6000AndIsotopes() {
        return Mr5000_6000AndIsotopes;
    }

    public static void setMr5000_6000AndIsotopes(int Mr5000_6000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr5000_6000AndIsotopes = Mr5000_6000AndIsotopes;
    }

    public static int getMr6000_7000AndIsotopes() {
        return Mr6000_7000AndIsotopes;
    }

    public static void setMr6000_7000AndIsotopes(int Mr6000_7000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.Mr6000_7000AndIsotopes = Mr6000_7000AndIsotopes;
    }

    public static int getMrBigger7000AndIsotopes() {
        return MrBigger7000AndIsotopes;
    }

    public static void setMrBigger7000AndIsotopes(int MrBigger7000AndIsotopes) {
        MascotAdaptedPrecursorPeakRemoval.MrBigger7000AndIsotopes = MrBigger7000AndIsotopes;
    }

    public static double getC13diff() {
        return c13diff;
    }

    public static void setC13diff(double c13diff) {
        MascotAdaptedPrecursorPeakRemoval.c13diff = c13diff;
    }

    /**
     * To return an MS/MS experimental spectrum with already precursor peaks
     * removed. It checks if the process to remove precursor peaks has been
     * already applied. If not, first precursor peaks are removed.
     *
     * @return
     */
    public MSnSpectrum getPrecursorPeaksRemovesExpMSnSpectrum() {
        if (!arePrecursorPeaksRemoved) {
            removePrecursor();
        }
        return ms;
    }

    public void setExpMSnSpectrum(MSnSpectrum ms) {
        arePrecursorPeaksRemoved = false;
        this.ms = ms;
    }

    public double getFragmentTolerance() {
        return fragmentTolerance;
    }

    public void setFragmentTolerance(double fragmentTolerance) {
        this.fragmentTolerance = fragmentTolerance;
    }

    public boolean arePrecursorPeaksRemoved() {
        return arePrecursorPeaksRemoved;
    }

    public void setArePrecursorPeaksRemoved(boolean arePrecursorPeaksRemoved) {
        this.arePrecursorPeaksRemoved = arePrecursorPeaksRemoved;
    }
}
