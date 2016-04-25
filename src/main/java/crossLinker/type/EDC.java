/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crossLinker.type;

import crossLinker.CrossLinker;
import crossLinker.CrossLinkerName;
import crossLinker.CrossLinkerType;

/**
 * 1-ethyl-3-(3-dimethylaminopropyl)carbodiimide hydrochloride
 *
 * Reactive group: carbodiimide Target: carboxyl groups to primary amines
 *
 * @author Sule
 */
public class EDC extends CrossLinker {

    public EDC() {
        super.name = CrossLinkerName.EDC;
        super.type = CrossLinkerType.CARBOXYL_TO_AMINE; // carboxyl groups to primary amines
        super.massShift_Type2 = 0;
        super.massShift_Type0 = Double.MAX_VALUE;
        throw new UnsupportedOperationException("EDC cross-linker is not supported yet."); 
    }

    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        throw new UnsupportedOperationException("EDC cross-linker is not supported yet.");
    }
}
