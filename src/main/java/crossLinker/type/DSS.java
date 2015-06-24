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
 * Disuccinimidyl suberate C16-H20-N2-O8 Heavy labeled C16-D12-H8-N2-O8 Light
 * labeled C16-H20-N2-O8
 *
 * Doublet-signal of 12.07Da between heavy and light labeled DSS
 *
 *
 * @author Sule
 */
public class DSS extends CrossLinker {

    public DSS() {
        this.name = CrossLinkerName.DSSd0;
        this.type = CrossLinkerType.homobifunctional;
        double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass());
    }

    /**
     * Here is isLabeled option introduced to return DSSd12. Otherwise DSSd0 is
     * returned
     *
     * @param isLabeled
     */
    public DSS(boolean isLabeled) {
        super.isLabeled = isLabeled;
        this.type = CrossLinkerType.homobifunctional; // K-K     
        double hydrogen_mass = Atom.H.getMonoisotopicMass(),
                deuterium_mass = Atom.H.getIsotopeMass(1);
        if (isLabeled) {
            this.name = CrossLinkerName.DSSd12;
            double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * deuterium_mass);
            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
            super.massShift_Type2 = moleculeMass - (2 * hydrogen_mass);
        } else {
            this.name = CrossLinkerName.DSSd0;
            double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * hydrogen_mass);
            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
            super.massShift_Type2 = moleculeMass - (2 * hydrogen_mass);
        }
    }

}
