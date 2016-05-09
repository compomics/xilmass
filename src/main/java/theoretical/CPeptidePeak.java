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
//    private int charge;
    private boolean isFound = false;
    private String name;
    private Peak matchedPeak;

    /**
     * Construct a CPeptidePeak object
     *
     * @param mz
     * @param intensity
     * @param charge
     * @param name
     */
    public CPeptidePeak(double mz, double intensity, String name) {
        this.mz = mz;
        this.intensity = intensity;
        this.name = name;
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
        return name + "_mz=" + mz_to_show;
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
                        return -1;
                    } else if (diff > 0) {
                        return 1;
                        // if two peaks have the same m/z values
                    } else {
                        boolean is_o1_peptideA = false,
                        is_o2_peptideA = false,
                        is_o1_peptideB = false,
                        is_o2_peptideB = false;

                        if (o1.getName().contains("pepA") && (!o1.getName().contains("lepB"))) {
                            is_o1_peptideA = true;
                        } else if (o1.getName().contains("pepB") && (!o1.getName().contains("lepA"))) {
                            is_o1_peptideB = true;
                        }
                        if (o2.getName().contains("pepA") && (!o2.getName().contains("lepB"))) {
                            is_o2_peptideA = true;
                        } else if (o2.getName().contains("pepB") && (!o2.getName().contains("lepA"))) {
                            is_o2_peptideB = true;
                        }
                        // first select the peak from a backbone, peptideA is in priority
                        if (is_o1_peptideA && !is_o1_peptideB && !is_o2_peptideA && !is_o2_peptideB) {
                            return -1;
                        } else if (!is_o1_peptideA && is_o1_peptideB && !is_o2_peptideA && !is_o2_peptideB) {
                            return -1;
                        } else if (!is_o1_peptideA && !is_o1_peptideB && is_o2_peptideA && !is_o2_peptideB) {
                            return 1;
                        } else if (!is_o1_peptideA && !is_o1_peptideB && !is_o2_peptideA && is_o2_peptideB) {
                            return 1;
                        } else {
                            return 1;
                        }
                    }
                }
            };
}
