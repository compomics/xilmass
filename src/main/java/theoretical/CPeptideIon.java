/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import theoretical.CPeptideIonType;
import java.util.Comparator;

/**
 * This class holds information about all cross linked peptide ions!
 *
 * @author Sule
 */
public class CPeptideIon {

    private double intensity,
            monoisotopic_mass;
    private int charge,
            fragmentIonType = 1; //0-a, 1-b, 2-c , 3-x, 4-y, 5-z
    private CPeptideIonType type = CPeptideIonType.Backbone_PepA;

//    public CPeptideIon(double intensity, double mass) {
//        this.intensity = intensity;
//        this.monoisotopic_mass = mass;
//        type = CPeptideIonType.Backbone_PepA;
//    }

    public CPeptideIon(double intensity, double mass, int charge) {
        this.intensity = intensity;
        this.charge = charge;
        this.monoisotopic_mass = mass;
    }

    public CPeptideIon(double intensity, double mass, int charge, CPeptideIonType type, int fragmentIonType) {
        this.intensity = intensity;
        this.charge = charge;
        this.type = type;
        this.monoisotopic_mass = mass;
        this.fragmentIonType = fragmentIonType;
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

    public double getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
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
   
    /**
     * This method returns a theoretical mz happens with given chargeValue
     *
     * @param chargeValue ion charge
     * @return a theoretical m/z
     */
    public double get_theoretical_mz(int chargeValue) {
        return (monoisotopic_mass + chargeValue * ElementaryIon.proton.getTheoreticMass()) / chargeValue;
    }

    /**
     * This method returns a theoretical mz happens with a chargeValue given
     * during construction
     *
     * @return a theoretical m/z
     */
    public double get_theoretical_mz() {
        return (monoisotopic_mass + charge * ElementaryIon.proton.getTheoreticMass()) / charge;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.monoisotopic_mass) ^ (Double.doubleToLongBits(this.monoisotopic_mass) >>> 32));
        hash = 89 * hash + this.charge;
        hash = 89 * hash + (this.type != null ? this.type.hashCode() : 0);
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
        if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity)) {
            return false;
        }
        if (Double.doubleToLongBits(this.monoisotopic_mass) != Double.doubleToLongBits(other.monoisotopic_mass)) {
            return false;
        }
        if (this.charge != other.charge) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  "(ch=" + charge + "_fragIon=" + fragmentIonType + "_" + type + ')';
    }
        

    /**
     * To sort CPeptideIon in a ascending mass order
     */
    static final Comparator<CPeptideIon> Ion_ASC_mass_order
            = new Comparator<CPeptideIon>() {
                @Override
                public int compare(CPeptideIon o1, CPeptideIon o2) {
                    return o1.getMass() < o2.getMass() ? -1 : o1.getMass() == o2.getMass() ? 0 : 1;
                }
            };

}
