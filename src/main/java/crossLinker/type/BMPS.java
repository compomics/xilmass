package crossLinker.type;

import com.compomics.util.experiment.biology.Atom;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerName;
import crossLinker.CrossLinkerType;

/**
 *This class defines properties of cross linker BMPS
 * @author Genet
 */
public class BMPS extends CrossLinker {
    
    
    /**
     * Initializing the property of BMPS
     */
     public BMPS() {
        this.name = CrossLinkerName.BMPS;
        this.type = CrossLinkerType.AMINE_TO_SULFHYDRYL;  
        double moleculeMass = (7 * Atom.C.getMonoisotopicMass()) + Atom.N.getMonoisotopicMass () + (4 * Atom.O.getMonoisotopicMass()) + (7 * Atom.H.getMonoisotopicMass());
        
        super.massShift_Type0 = moleculeMass;
        super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass()+ Atom.O.getMonoisotopicMass());
    }

   /**
    * Calculates mass shift based on the formula 
    * @param isLabeled 
    */
    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        
        if (!isLabeled) {
            this.name = CrossLinkerName.BMPS;
            double moleculeMass = (7 * Atom.C.getMonoisotopicMass()) + Atom.N.getMonoisotopicMass () + (4 * Atom.O.getMonoisotopicMass()) + (7 * Atom.H.getMonoisotopicMass());
            super.massShift_Type0 = moleculeMass ;
            super.massShift_Type2 = moleculeMass - (2 * Atom.H.getMonoisotopicMass()+ Atom.O.getMonoisotopicMass());
        } else {//No labeling information given and hence it doesn't support
            throw new UnsupportedOperationException("Labeling should be false, no labeling feature provided."); 
        }
    }
}
