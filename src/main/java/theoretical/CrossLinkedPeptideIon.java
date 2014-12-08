/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

/**
 *
 * @author Sule
 */
public class CrossLinkedPeptideIon {

    private double intensity,
            mz;
    private double charge;
    private CrossLinkedPeptideIonType type = CrossLinkedPeptideIonType.PeptideFragmentIon;

    public CrossLinkedPeptideIon(double intensity, double mz, double charge, CrossLinkedPeptideIonType type) {
        this.intensity = intensity;
        this.mz = mz;
        this.charge = charge;
        this.type = type;
    }

    public CrossLinkedPeptideIon(double intensity, double mz, double charge) {
        this.intensity = intensity;
        this.mz = mz;
        this.charge = charge;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public CrossLinkedPeptideIonType getType() {
        return type;
    }

    public void setType(CrossLinkedPeptideIonType type) {
        this.type = type;
    }
    
    

}
