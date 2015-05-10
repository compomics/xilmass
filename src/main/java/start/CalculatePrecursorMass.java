/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;

/**
 * This class holds a static method to calculate precursor mass based on the
 * last possible Charge
 *
 * @author Sule
 */
public class CalculatePrecursorMass {

    /**
     * This method calculates precursor mass of a given MSnSpectrum object. It
     * selects the las possible charge object, and calculate based on this
     * charge state, it calculate precursor mass
     *
     * @param ms
     * @return
     */
    public static double getPrecursorMass(MSnSpectrum ms) {
        ArrayList<Charge> possibleCharges = ms.getPrecursor().getPossibleCharges();
        Charge charge = possibleCharges.get(possibleCharges.size() - 1);
        double precMass = ms.getPrecursor().getMass(charge.value);
        return precMass;
    }
}
