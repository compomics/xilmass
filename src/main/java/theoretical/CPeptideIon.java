/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.ions.ElementaryIon;

/**
 * This class holds information about all cross linked peptide ions!
 * 
 * @author Sule
 */
public class CPeptideIon {

    private double intensity,
            monoisotopic_mass;
    private int charge;
    private IonType type = IonType.PeptideFragmentIon;

    public CPeptideIon(double intensity, double mass) {
        this.intensity = intensity;
        this.monoisotopic_mass = mass;
    }

    public CPeptideIon(double intensity, double mass, int charge) {
        this.intensity = intensity;
        this.charge = charge;
        this.monoisotopic_mass = mass;
    }

    public CPeptideIon(double intensity, double mass, int charge, IonType type) {
        this.intensity = intensity;
        this.charge = charge;
        this.type = type;
        this.monoisotopic_mass = mass;
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

    public IonType getType() {
        return type;
    }

    public void setType(IonType type) {
        this.type = type;
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

}
