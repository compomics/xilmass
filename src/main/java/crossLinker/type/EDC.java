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
 * Ethyl(dimethylaminopropyl)carbodiimide TODO: Ask about these mass shifts!
 *
 * @author Sule
 */
public class EDC extends CrossLinker {

    public EDC() {
        super.name = CrossLinkerName.EDC;
        super.type = CrossLinkerType.heterobifunctional;
        super.massShift_Type2 = 0;
        super.massShift_Type0 = Double.MAX_VALUE;
    }

    @Override
    protected void calculateMassShifts(boolean isLabeled) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
