/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.Comparator;

/**
 * This class is used to sort an arraylist of MSnSpectrum objects based on their
 * precursor masses
 *
 * @author Sule
 */
public class SpectrumInfo {

    private MSnSpectrum ms;  // a MSnSpectrum.
    private double actualPrecMass; // an actual precursor mass from a selected MS2 spectrum
    private int roundDownPrecMass;

    /**
     * To construct SpectrumInfo object
     *
     * @param ms is MSnSpectrum object
     * @param actualPrecMass a precursor mass of that MSnSpectrum object
     */
    public SpectrumInfo(MSnSpectrum ms, double actualPrecMass) {
        this.ms = ms;
        this.actualPrecMass = actualPrecMass;
        roundDownPrecMass = (int) actualPrecMass;
    }

    /**
     * To return MSnSpectrum object
     *
     * @return
     */
    public MSnSpectrum getMS() {
        return ms;
    }

    /**
     * To set MSnSpectrum object to another one. It also calculates again
     * precursor mass..
     *
     * @param ms
     */
    public void setMSnSpectrum(MSnSpectrum ms) {
        this.ms = ms;
        actualPrecMass = CalculatePrecursorMass.getPrecursorMass(ms);
    }

    /**
     * This method return a precursor mass from a given MSnSpectrum object
     *
     * @return
     */
    public double getPrecursorMass() {
        return actualPrecMass;
    }

    public int getRoundDownPrecMass() {
        return roundDownPrecMass;
    }

    public void setRoundDownPrecMass(int roundDownPrecMass) {
        this.roundDownPrecMass = roundDownPrecMass;
    }

    /**
     * To sort SpectrumInfo objects in a ascending m/z order
     */
    public static final Comparator<SpectrumInfo> Precursor_ASC_mz_order
            = new Comparator<SpectrumInfo>() {
                @Override
                public int compare(SpectrumInfo o1, SpectrumInfo o2) {
                    return o1.getPrecursorMass() < o2.getPrecursorMass() ? -1 : o1.getPrecursorMass() == o2.getPrecursorMass() ? 0 : 1;
                }
            };

    /**
     * To sort SpectrumInfo objects in a ascending rounding down mz order
     */
    public static final Comparator<SpectrumInfo> Precursor_ASC_roundDown_mz_order
            = new Comparator<SpectrumInfo>() {
                @Override
                public int compare(SpectrumInfo o1, SpectrumInfo o2) {
                    return o1.getRoundDownPrecMass() < o2.getRoundDownPrecMass() ? -1 : o1.getRoundDownPrecMass() == o2.getRoundDownPrecMass() ? 0 : 1;
                }
            };

}
