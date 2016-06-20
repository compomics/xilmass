/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.Comparator;

/**
 * This class holds m/z, intensity and name attributes for CPeptideIon objects.
 * This is used while finding a matched peak, with considering singly and doubly
 * charged peaks (doubly charged peaks in case that a precursor charge is bigger
 * than one).
 *
 * @author Sule
 */
public class CPeptidePeak {

    private double mz,
            intensity,
            diff = Double.MAX_VALUE;
    private int charge;
    private boolean isFound = false;
    private String name;
    private char aa_code;
    private Peak matchedPeak;

    /**
     * Construct a CPeptidePeak object
     *
     * @param mz
     * @param intensity
     * @param name
     */
    public CPeptidePeak(double mz, double intensity, String name) {
        this.mz = mz;
        this.intensity = intensity;
        this.name = name;
    }

    /**
     * Construct a CPeptidePeak object
     *
     * @param mz
     * @param intensity
     * @param name
     * @param aa_code
     * @param charge
     */
    public CPeptidePeak(double mz, double intensity, String name, char aa_code, int charge) {
        this.mz = mz;
        this.intensity = intensity;
        this.name = name;
        this.aa_code = aa_code;
        this.charge = charge;
    }

    /**
     * Returns a charge value for a CPeptidePeak
     *
     * @return
     */
    public int getCharge() {
        return charge;
    }

    /**
     * Sets a charge value for a CPeptidePeak
     *
     * @param charge
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }

    /**
     * Returns an amino acid single letter code for a CPeptidePeak
     *
     * @return
     */
    public char getAa_code() {
        return aa_code;
    }

    /**
     * Sets an amino acid single letter code for CPeptidePeak
     *
     * @param aa_code
     */
    public void setAa_code(char aa_code) {
        this.aa_code = aa_code;
    }

    /**
     * Returns a name of CPeptidePeak
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set a name of CPeptidePeak
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a m/z value of CPeptidePeak
     *
     * @return a double m/z value
     */
    public double getMz() {
        return mz;
    }

    /**
     * Set a m/z value of CPeptidePeak
     *
     * @param mz
     */
    public void setMz(double mz) {
        this.mz = mz;
    }

    /**
     * Returns an intensity value of CPeptidePeak
     *
     * @return double intensity value
     */
    public double getIntensity() {
        return intensity;
    }

    /**
     * Sets an intensity of CPeptidePeak
     *
     * @param intensity
     */
    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns a difference between m/z value of CPeptidePeak in selection and
     * m/z value of matched experimental peak
     *
     * @return a double value of m/z difference
     */
    public double getDiff() {
        return diff;
    }

    /**
     * Set a value for diff attribute, while also setting isFound as true.
     *
     * @param diff a double value of diff
     */
    public void setDiff(double diff) {
        if (diff < Double.MAX_VALUE) {
            isFound = true;
        }
        this.diff = diff;
    }

    /**
     * Returns a peak on an experimental spectrum that is matched to
     * CPeptidePeak in selection.
     *
     * @return a Peak
     */
    public Peak getMatchedPeak() {
        return matchedPeak;
    }

    /**
     * Set a peak on an experimental spectrum that is matched to CPeptidePeak in
     * selection.
     *
     * @param matchedPeak is an experimental matched peak
     */
    public void setMatchedPeak(Peak matchedPeak) {
        this.matchedPeak = matchedPeak;
    }

    public boolean isIsFound() {
        return isFound;
    }

    public void setIsFound(boolean isFound) {
        this.isFound = isFound;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.mz) ^ (Double.doubleToLongBits(this.mz) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CPeptidePeak other = (CPeptidePeak) obj;
        if (Double.doubleToLongBits(this.mz) != Double.doubleToLongBits(other.mz)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * to write a cPeptidePeak with their name and m/z
     *
     * @return
     */
    @Override
    public String toString() {
        double mz_to_show = Math.floor(mz * 10000) / 10000;
        String toString = "(" + name + ")+" + "_mz=" + mz_to_show;
        if (charge == 2) {
            toString = "(" + name + ")++" + "_mz=" + mz_to_show;
        }
        return toString;
    }

    /**
     * To sort CPeptidePeak If two peaks have different m/z values, it selects
     * in a ascending m/z order. But if there are two peaks with the same m/z
     * value, then a peak from a peptide backbone is selected. If both peaks are
     * from a peptide backbone, then peak from peptide A is selected.
     */
    public static final Comparator<CPeptidePeak> order_CPeptidePeak
            = new Comparator<CPeptidePeak>() {
        @Override
        public int compare(CPeptidePeak o1, CPeptidePeak o2) {
            double diff = o1.getMz() - o2.getMz();
            // order peaks by first m/z order 
            if (diff < 0) {
                return 1;
            } else if (diff > 0) {
                return -1;
                // if two peaks have the same m/z values
            } else {
                return 0;

            }
        }
    };
}
