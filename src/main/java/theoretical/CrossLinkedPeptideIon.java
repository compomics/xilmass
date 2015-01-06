/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.ions.ElementaryIon;

/**
 *
 * @author Sule
 */
public class CrossLinkedPeptideIon {

    private double intensity,
            mass;
    private int charge;
    private CrossLinkedPeptideIonType type = CrossLinkedPeptideIonType.PeptideFragmentIon;

    public CrossLinkedPeptideIon(double intensity, double mass) {
        this.intensity = intensity;
        this.mass = mass;
    }

    public CrossLinkedPeptideIon(double intensity, double mass, int charge) {
        this.intensity = intensity;
        this.charge = charge;
        this.mass = mass;
    }

    public CrossLinkedPeptideIon(double intensity, double mass, int charge, CrossLinkedPeptideIonType type) {
        this.intensity = intensity;
        this.charge = charge;
        this.type = type;
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
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

    public CrossLinkedPeptideIonType getType() {
        return type;
    }

    public void setType(CrossLinkedPeptideIonType type) {
        this.type = type;
    }

    /**
     * This method returns a theoretical mz happens with given chargeValue
     *
     * @param chargeValue ion charge
     * @return a theoretical m/z
     */
    public double get_theoretical_mz(int chargeValue) {
        return (mass + chargeValue * ElementaryIon.proton.getTheoreticMass()) / chargeValue;
    }

    /**
     * This method returns a theoretical mz happens with a chargeValue given
     * during construction
     *
     * @return a theoretical m/z
     */
    public double get_theoretical_mz() {
        return (mass + charge * ElementaryIon.proton.getTheoreticMass()) / charge;
    }

}
