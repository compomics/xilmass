/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

/**
 * To calculate ms1error for a selected MSnSpectrum
 * 
 * @author Sule
 */
public class CalculateMS1Err {

    /**
     * Calculations was explained here
     * http://genesis.ugent.be/files/costore/practicals/bioinformatics-for-proteomics/A-Answers/A-Answers.pdf (Answer 1.3e)
     * The calculated value is not an absolute value (is either negative or positive).
     * 
     * 
     * @param isPPM true: PPM based, false: Dalton based
     * @param theoreticalPrecMZ - Theoretical precursor mass
     * @param observedPrecMZ - Observed mass
     * @return ms1err
     */
    public static double getMS1Err(boolean isPPM, double theoreticalPrecMZ, double observedPrecMZ) {
//        System.out.println("isPPM="+isPPM+"\t and theorMass="+theoreticalPrecursorMass+"\t and measuredMass="+measuredPrecusorMass);
        double error = 0;        
        if (isPPM) {
            // This PPM calculation was from bioinformatics-for-proteomics
            double diff = observedPrecMZ - theoreticalPrecMZ;
            double ppm_error = (diff) / theoreticalPrecMZ;
            ppm_error = ppm_error * 1000000;
            error = ppm_error;
        } else {
            double mz_error = theoreticalPrecMZ - observedPrecMZ;
            error = mz_error;
        }
        return error;
    }

}
