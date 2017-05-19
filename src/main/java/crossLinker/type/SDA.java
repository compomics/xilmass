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
 * @author Genet
 */
public class SDA extends CrossLinker {
    
    
    public SDA() {
        this.name = CrossLinkerName.SDA;
        this.type = CrossLinkerType.AMINE_TO_AMINE; // primary amines (K or n-termini)     
        double moleculeMass = (5 * Atom.C.getMonoisotopicMass()) + (1 * Atom.O.getMonoisotopicMass()) + (6 * Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass());
    }

    /**    
     * If isLabeled is true, it returns BSSd4, otherwise BS3d0.
     *
     * @param isLabeled
     */
//    public SDA(boolean isLabeled) {
//        super.isLabeled = isLabeled;
//        this.type = CrossLinkerType.AMINE_TO_AMINE; // primary amines (K or n-termini)    
//        double hydrogen_mass = Atom.H.getMonoisotopicMass(),
//                deuterium_mass = Atom.H.getIsotopeMass(1);
//        if (isLabeled) {
//            this.name = CrossLinkerName.SDA;
//            double moleculeMass = (5 * Atom.C.getMonoisotopicMass()) + (1 * Atom.O.getMonoisotopicMass()) + (6 * Atom.H.getMonoisotopicMass());
//            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
//            super.massShift_Type2 = moleculeMass - (2 * hydrogen_mass);
//        } else {
//            this.name = CrossLinkerName.BS3d0;
//            double moleculeMass = (8 * Atom.C.getMonoisotopicMass()) + (2 * Atom.O.getMonoisotopicMass()) + (12 * hydrogen_mass);
//            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
//            super.massShift_Type2 = moleculeMass - (2 * hydrogen_mass);
//        }
//    }

    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        this.name = CrossLinkerName.SDA;
        this.type = CrossLinkerType.AMINE_TO_AMINE; // primary amines (K or n-termini)     
        double moleculeMass = (5 * Atom.C.getMonoisotopicMass()) + (1 * Atom.O.getMonoisotopicMass()) + (6 * Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass());
    }
    
}
