/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import java.util.Comparator;

/**
 * This class holds information about all cross linked peptide ions!
 *
 * @author Sule
 */
public class CPeptideIon {

    private double intensity,
            monoisotopic_mass,
            diff = Double.MAX_VALUE;
    private int fragmentIonType, //0-a, 1-b, 2-c , 3-x, 4-y, 5-z
            identification_charge = 0;
    private boolean isFound = false;
    private CPeptideIonType type;
    private String name;
    private char aa_code = '+';

    /**
     * To construct a CPeptideIon object
     *
     * @param intensity
     * @param mass
     * @param type
     * @param fragmentIonType enumeration comes from PeptideFragmentIon class.
     * Additionally, 6 shows a water loss and 7 shows ammonia loss!
     * @param name
     */
    public CPeptideIon(double intensity, double mass, CPeptideIonType type, int fragmentIonType, String name) {
        this.intensity = intensity;
        this.type = type;
        this.monoisotopic_mass = mass;
        this.fragmentIonType = fragmentIonType;
        this.name = name;
    }

    
    /**
     * To construct a CPeptideIon object
     *
     * @param intensity
     * @param mass
     * @param type
     * @param fragmentIonType enumeration comes from PeptideFragmentIon class.
     * Additionally, 6 shows a water loss and 7 shows ammonia loss!
     * @param aa_code a single letter code of an amino acid for selected ion type
     * @param name 
     */
    public CPeptideIon(double intensity, double mass, CPeptideIonType type, int fragmentIonType, String name, char aa_code) {
        this.intensity = intensity;
        this.type = type;
        this.monoisotopic_mass = mass;
        this.fragmentIonType = fragmentIonType;
        this.name = name;
        this.aa_code = aa_code;
    }

    /**
     * Returns a amino acid single letter code for CPeptidePeak
     *
     * @return
     */
    public char getAa_code() {
        return aa_code;
    }

    /**
     * Sets a amino acid single letter code for CPeptidePeak
     *
     * @param aa_code
     */
    public void setAa_code(char aa_code) {
        this.aa_code = aa_code;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setIsFound(boolean isFound) {
        this.isFound = isFound;
    }

    public int getIdentification_charge() {
        return identification_charge;
    }

    public void setIdentification_charge(int identification_charge) {
        this.identification_charge = identification_charge;
    }

    public double getMass() {
        return monoisotopic_mass;
    }

    public void setMass(double mass) {
        this.monoisotopic_mass = mass;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public CPeptideIonType getType() {
        return type;
    }

    public void setType(CPeptideIonType type) {
        this.type = type;
    }

    public int getFragmentIonType() {
        return fragmentIonType;
    }

    public void setFragmentIonType(int fragmentIonType) {
        this.fragmentIonType = fragmentIonType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns a theoretical mz happens with given chargeValue
     *
     * @param chargeValue ion charge
     * @return a theoretical m/z
     */
    public double get_theoretical_mz(int chargeValue) {
        double mz = (monoisotopic_mass + (chargeValue * ElementaryIon.proton.getTheoreticMass())) / (double) chargeValue;
        double rounded_mz = Math.floor(mz * 1000000) / 1000000;
        return rounded_mz;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.monoisotopic_mass) ^ (Double.doubleToLongBits(this.monoisotopic_mass) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.diff) ^ (Double.doubleToLongBits(this.diff) >>> 32));
        hash = 17 * hash + this.fragmentIonType;
        hash = 17 * hash + this.identification_charge;
        hash = 17 * hash + (this.isFound ? 1 : 0);
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final CPeptideIon other = (CPeptideIon) obj;
        if (Double.doubleToLongBits(this.monoisotopic_mass) != Double.doubleToLongBits(other.monoisotopic_mass)) {
            return false;
        }
        if (this.fragmentIonType != other.fragmentIonType) {
            return false;
        }
        if (this.identification_charge != other.identification_charge) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        double mass_to_show = Math.floor(monoisotopic_mass * 10000) / 10000;
        if (identification_charge == 0) {
            return "(mass=" + mass_to_show + "_" + name + ')';
        }
        String id_info = "singly";
        if (identification_charge == 2) {
            id_info = "doubly";
        }
        double mz = get_theoretical_mz(identification_charge),
                mz_to_show = Math.floor(mz * 10000) / 10000;
        return "(mass=" + mass_to_show + "_" + id_info + "MZ=" + mz_to_show + "_" + name + ')';
    }

    /**
     * To sort CPeptideIon in a ascending mass order, if two peptide have the
     * same mass, then the one on peptide backbone is selected. In case that
     * both from
     */
    public static final Comparator<CPeptideIon> Ion_ASC_mass_order
            = new Comparator<CPeptideIon>() {
        @Override
        public int compare(CPeptideIon o1, CPeptideIon o2) {

            double diff = o1.getMass() - o2.getMass();
            // order peaks by first m/z order 
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
                // if two peaks have the same m/z values
            } else {
                return 0;
            }
        }
    };

    public static final Comparator<CPeptideIon> Ion_ASC_mass_order_IDCharged
            = new Comparator<CPeptideIon>() {
        @Override
        public int compare(CPeptideIon o1, CPeptideIon o2) {

            return o1.get_theoretical_mz(o1.getIdentification_charge()) < o2.get_theoretical_mz(o2.getIdentification_charge()) ? -1 : o1.get_theoretical_mz(o1.getIdentification_charge()) == o2.get_theoretical_mz(o2.getIdentification_charge()) ? 0 : 1;
        }
    };

}
