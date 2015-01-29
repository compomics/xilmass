/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

/**
 *
 * @author Sule
 */
public class CalculateMS1Err {

    public static double getMS1Err(boolean isPPM, double theoreticalPrecursorMass, double measuredPrecusorMass) {
        double error = 0;
        if (isPPM) {
            double diff = Math.abs(theoreticalPrecursorMass - measuredPrecusorMass);
            // TODO this might be either relative or absolute mass error
           double ppm_error = (diff) / measuredPrecusorMass;
           ppm_error = ppm_error * 1000000;
           error = ppm_error;
        } else {
            double mz_error = Math.abs(theoreticalPrecursorMass - measuredPrecusorMass);
            error = mz_error;
        }
        return error;
    }

}
