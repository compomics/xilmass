/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker.type;

import com.compomics.util.experiment.biology.Atom;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerName;
import crossLinker.CrossLinkerType;

/**
 * Glutaraldehyde OHC(CH2)3CHO
 *
 * @author Sule
 */
public class GA extends CrossLinker {

    public GA() {
        this.name = CrossLinkerName.GA;
        this.type = CrossLinkerType.homobifunctional; // K-K
        double moleculeMass = (5 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (8 * Atom.H.getMonoisotopicMass()),
                waterMass = (2 * Atom.H.getMonoisotopicMass()) + Atom.O.getMonoisotopicMass();
        super.massShift_Type0 = moleculeMass - waterMass;
        super.massShift_Type2 = moleculeMass - (2 * waterMass);
    }

    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
