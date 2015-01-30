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
 * Disuccinimidylsuberate
 * C16H20N2O8
 * TODO: Ask about these Type0 and Type2 reactions!

 * 
 * @author Sule
 */
public class DSS extends CrossLinker {

    public DSS() {
        this.name = CrossLinkerName.DSS;
        this.type = CrossLinkerType.homobifunctional;
        double moleculeMass = (8*Atom.C.getMonoisotopicMass())+ (2*Atom.O.getMonoisotopicMass())+(12*Atom.H.getMonoisotopicMass());
        super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
        super.massShift_Type2 = moleculeMass - (2*Atom.H.getMonoisotopicMass());
        
        super.massShift_Type0 = 253.0000; // TODO: ask about this Type0 shift! This must be accurate!!!!!
       // super.massShift_Type2 = 138.0681000;
    }

}
