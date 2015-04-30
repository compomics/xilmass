/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import java.util.Comparator;

/**
 * This class holds m/z, intensity and name information derived from CPeptideIon
 * object It is used during finding a matched peaks while considering singly and
 * doubly charged peaks.
 *
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

    public CPeptidePeak(double mz, double intensity, int charge, String name) {
        this.mz = mz;
        this.intensity = intensity;
        this.charge = charge;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        if (diff < Double.MAX_VALUE) {
            isFound = true;
        }
        this.diff = diff;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public boolean isIsFound() {
        return isFound;
    }

    public void setIsFound(boolean isFound) {
        this.isFound = isFound;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.mz) ^ (Double.doubleToLongBits(this.mz) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 67 * hash + this.charge;
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
        if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity)) {
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
     * To sort CPeptidePeak in a ascending m/z order
     */
    public static final Comparator<CPeptidePeak> Peak_ASC_mz_order
            = new Comparator<CPeptidePeak>() {
                @Override
                public int compare(CPeptidePeak o1, CPeptidePeak o2) {
                    return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
                }
            };

}
