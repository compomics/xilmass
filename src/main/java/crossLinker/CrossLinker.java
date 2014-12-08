/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker;

import com.compomics.util.experiment.biology.ions.ElementaryIon;

/**
 *
 * @author Sule
 */
public abstract class CrossLinker {

    protected CrossLinkerName name;
    protected double massDift; // monoisotopic or average?- double check this value! all fragment ions are based on average mass, not monoistopic mass
    protected CrossLinkerType type;
    //protected int chargeValue;// maybe not necessary?

//    public CrossLinker(CrossLinkerName name, double massDift, CrossLinkerType type) {
//        this.name = name;
//        this.type = type;
//        this.massDift = massDift;
//    }
    /**
     * This method returns a theoretical mz happens by cross linking 
     * 
     * @param chargeValue ion charge
     * @return
     */
    public double get_theoretical_mz(int chargeValue) {
        return (massDift + chargeValue * ElementaryIon.proton.getTheoreticMass()) / chargeValue;

    }

    public CrossLinkerType getType() {
        return type;
    }

    public void setType(CrossLinkerType type) {
        this.type = type;
    }

    public CrossLinkerName getName() {
        return name;
    }

    public void setName(CrossLinkerName name) {
        this.name = name;
    }

    public double getMassDift() {
        return massDift;
    }

    public void setMassDift(double massDift) {
        this.massDift = massDift;
    }

}
