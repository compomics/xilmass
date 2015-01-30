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
 *
 * @author Sule
 */
public class BS3 extends CrossLinker {

    public BS3() {
        this.name = CrossLinkerName.BS3;
        this.type = CrossLinkerType.homobifunctional; // K-K     
        double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass());
    }

    // To introduce heavy options, otherwise it is "light only"
    // Here deteurium is used instead of hydrogen atom
    public BS3(boolean isLabeled) {
        this.name = CrossLinkerName.BS3;
        this.type = CrossLinkerType.homobifunctional; // K-K     
        double hydrogen_mass = Atom.H.getMonoisotopicMass(),
                deuterium_mass = Atom.H.getIsotopeMass(1);
        if (isLabeled) {
            double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * deuterium_mass);
            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
            super.massShift_Type2 = moleculeMass - (2 * deuterium_mass);
        } else {
            double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * hydrogen_mass);
            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
            super.massShift_Type2 = moleculeMass - (2 * hydrogen_mass);
        }
    }

}
