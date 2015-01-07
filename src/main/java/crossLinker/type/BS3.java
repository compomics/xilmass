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
        double moleculeMass = (8*Atom.C.getMonoisotopicMass())+ (2*Atom.O.getMonoisotopicMass())+(12*Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass - (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2*Atom.H.getMonoisotopicMass());
    }

}
