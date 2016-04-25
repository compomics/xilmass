/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

import com.compomics.util.experiment.biology.ions.ElementaryIon;

/**
 * This super class hold necessary information for any cross linker agent.
 *
 * Note that all mass calculations are based on monoisotopic mass
 *
 * @author Sule
 */
public abstract class CrossLinker {

    protected CrossLinkerName name; // name
    protected double massShift_Type2, // when a linker connected to both peptides - intra protein
            massShift_Type0; // when a linker only connected to one peptide - monolink/dead end
    protected CrossLinkerType type; // amine_to_amine; or carboxyl_to_amine or any other one
    protected boolean isLabeled; // is heavy labeled or not

    /**
     * Return true if a cross linker is labeled
     *
     * @return
     */
    public boolean isIsLabeled() {
        return isLabeled;
    }

    public void setIsLabeled(boolean isLabeled) {
        calculateMassShifts(isLabeled);
        this.isLabeled = isLabeled;
    }

    /**
     * This method returns a theoretical mz happens by cross linking
     *
     * @param chargeValue ion charge
     * @return
     */
    public double get_theoretical_mz_Type2(int chargeValue) {
        return (massShift_Type2 + chargeValue * ElementaryIon.proton.getTheoreticMass()) / chargeValue;
    }

    /**
     * This method returns a theoretical mz happens by cross linking
     *
     * @param chargeValue ion charge
     * @return
     */
    public double get_theoretical_mz_Type0(int chargeValue) {
        return (massShift_Type0 + chargeValue * ElementaryIon.proton.getTheoreticMass()) / chargeValue;
    }

    /**
     * Cross linker type. Either amine-to-amine cross-linker or any other
     * cross-linker type
     *
     * @return an enum type of CrossLinkerType
     */
    public CrossLinkerType getType() {
        return type;
    }

    public void setType(CrossLinkerType type) {
        this.type = type;
    }

    /**
     * A name of a cross linker agent - DSSd0, BS3d0, etc.
     *
     * @return an enum type of a CrossLinkerName
     */
    public CrossLinkerName getName() {
        return name;
    }

    public void setName(CrossLinkerName name) {
        this.name = name;
    }

    public double getMassShift_Type2() {
        return massShift_Type2;
    }

    public void setMassShift_Type2(double massShift_Type2) {
        this.massShift_Type2 = massShift_Type2;
    }

    public double getMassShift_Type0() {
        return massShift_Type0;
    }

    public void setMassShift_Type0(double massShift_Type0) {
        this.massShift_Type0 = massShift_Type0;
    }

    protected abstract void calculateMassShifts(boolean isLabeled);
}
