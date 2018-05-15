package crossLinker.type;

import com.compomics.util.experiment.biology.Atom;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerName;
import crossLinker.CrossLinkerType;

/**
 *
 * @author Genet
 */
public class BMPS extends CrossLinker {
    
    
    
     public BMPS() {
        this.name = CrossLinkerName.BMPS;
        this.type = CrossLinkerType.AMINE_TO_SULFHYDRYL;  
        double moleculeMass = (7 * Atom.C.getMonoisotopicMass()) + Atom.N.getMonoisotopicMass () + (4 * Atom.O.getMonoisotopicMass()) + (7 * Atom.H.getMonoisotopicMass());
        
        super.massShift_Type0 = moleculeMass;
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass()+ Atom.O.getMonoisotopicMass());
    }

   
    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        
        if (isLabeled) {
            this.name = CrossLinkerName.BMPS;
            double moleculeMass = (7 * Atom.C.getMonoisotopicMass()) + Atom.N.getMonoisotopicMass () + (4 * Atom.O.getMonoisotopicMass()) + (7 * Atom.H.getMonoisotopicMass());
            super.massShift_Type0 = moleculeMass;
            super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass()+Atom.O.getMonoisotopicMass());
        } else {
            this.name = CrossLinkerName.BMPS;
            double moleculeMass = (7 * Atom.C.getMonoisotopicMass()) + Atom.N.getMonoisotopicMass () + (4 * Atom.O.getMonoisotopicMass()) + (7 * Atom.H.getMonoisotopicMass());
            super.massShift_Type0 = moleculeMass + (Atom.O.getMonoisotopicMass());
            super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass()+ Atom.O.getMonoisotopicMass());
        }
    }
}
