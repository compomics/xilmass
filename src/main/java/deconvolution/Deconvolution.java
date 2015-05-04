/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deconvolution;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Sule
 */
public class Deconvolution {

    private MSnSpectrum expMSnSpectrum;
    private boolean isDeconvoluted = false;

    public Deconvolution(MSnSpectrum expMSnSpectrum) {
        this.expMSnSpectrum = expMSnSpectrum;
    }

    public MSnSpectrum getDeconvolutedExpMSnSpectrum() {
        if (!isDeconvoluted) {
            deconvoluteAlt();
        }
        return expMSnSpectrum;
    }

    public void setExpMSnSpectrum(MSnSpectrum expMSnSpectrum) {
        this.expMSnSpectrum = expMSnSpectrum;
    }

    public boolean isIsDeconvoluted() {
        return isDeconvoluted;
    }

    public void setIsDeconvoluted(boolean isDeconvoluted) {
        this.isDeconvoluted = isDeconvoluted;
    }

    /**
     * This method find one ion with the same mass but different charge state If
     * it finds a peak with the same mass but the different charge, they
     * collapse them into one peak
     *
     */
    public void deconvolute() {
        ArrayList<Peak> toRemove = new ArrayList<Peak>(),
                deconvoluted = new ArrayList<Peak>();
        int maxPrecursorCharge = getMaxPrecursorCharge();
        double precursorMass = getPrecurMass(),
                finalSection = 0;

        for (int charge = 1; charge < maxPrecursorCharge; charge++) {
            double upperMZLimit = getMZ(precursorMass, charge),//inclusive.. 
                    lowerMZLimit = getMZ(precursorMass, charge + 1);//exclusive..
            finalSection = lowerMZLimit;
            System.out.println("Charge=" + charge);
            System.out.println("UpperMZ=" + upperMZLimit);
            System.out.println("LowerMZ=" + lowerMZLimit);
            Iterator<Peak> peakIterator = expMSnSpectrum.getPeakList().iterator();
            while (peakIterator.hasNext()) {
                boolean isDeconvoluted = false;
                Peak tmpPeak = peakIterator.next();
                double tmp_mz = tmpPeak.getMz(),
                        tmp_mz_copy = tmp_mz,
                        tmp_int = tmpPeak.getIntensity();
                if (tmp_mz <= upperMZLimit && tmp_mz > lowerMZLimit) {
                    for (int otherCharge = charge + 1; otherCharge <= maxPrecursorCharge; otherCharge++) {
                        double tmp_mz_increased_charge = getMZIncreasedCharge(tmp_mz, charge, otherCharge);
                        System.out.print(tmp_mz + "\t" + charge + "\t" + tmp_mz_increased_charge + "\t" + otherCharge + "\t");
                        Peak foundPeak = find(tmp_mz_increased_charge);
                        if (foundPeak != null) {
                            System.out.println("Found=" + tmp_mz + "\t" + charge + "\t" + tmp_mz_increased_charge + "\t" + otherCharge);
                            toRemove.add(foundPeak);
                            isDeconvoluted = true;
                            tmp_mz_copy += foundPeak.getMz();
                            tmp_int += foundPeak.getIntensity();
                        }
                    }
                }
                if (isDeconvoluted) {
                    toRemove.add(tmpPeak);
                    deconvoluted.add(new Peak(tmp_mz_copy, tmp_int));
                }
            }
        }
        expMSnSpectrum.getPeakList().removeAll(toRemove);
        expMSnSpectrum.getPeakList().addAll(deconvoluted);

    }

    /**
     * This method find one ion with the same mass but different charge state If
     * it finds a peak with the same mass but the different charge, they
     * collapse them into one peak
     *
     */
    public void deconvoluteAlt() {
        ArrayList<Peak> toRemove = new ArrayList<Peak>(),
                deconvoluted = new ArrayList<Peak>();
        int maxPrecursorCharge = getMaxPrecursorCharge();
        double precursorMass = getPrecurMass(),
                finalSection = 0;

        for (int charge = 1; charge < maxPrecursorCharge; charge++) {
            double upperMZLimit = getMZ(precursorMass, charge);//inclusive.. 
            System.out.println("Charge=" + charge);
            System.out.println("UpperMZ=" + upperMZLimit);
            Iterator<Peak> peakIterator = expMSnSpectrum.getPeakList().iterator();
            while (peakIterator.hasNext()) {
                boolean isDeconvoluted = false;
                Peak tmpPeak = peakIterator.next();
                double tmp_mz = tmpPeak.getMz(),
                        tmp_mz_copy = tmp_mz,
                        tmp_int = tmpPeak.getIntensity();
                if (tmp_mz <= upperMZLimit) {
                    for (int otherCharge = charge + 1; otherCharge <= maxPrecursorCharge; otherCharge++) {
                        double tmp_mz_increased_charge = getMZIncreasedCharge(tmp_mz, charge, otherCharge);
                        System.out.print(tmp_mz + "\t" + charge + "\t" + tmp_mz_increased_charge + "\t" + otherCharge + "\t");
                        Peak foundPeak = find(tmp_mz_increased_charge);
                        if (foundPeak != null) {
                            System.out.println("Found=" + tmp_mz + "\t" + charge + "\t" + tmp_mz_increased_charge + "\t" + otherCharge);
                            toRemove.add(foundPeak);
                            isDeconvoluted = true;
                            tmp_mz_copy += foundPeak.getMz();
                            tmp_int += foundPeak.getIntensity();
                        }
                    }
                }
                if (isDeconvoluted) {
                    toRemove.add(tmpPeak);
                    deconvoluted.add(new Peak(tmp_mz_copy, tmp_int));
                }
            }
        }
        expMSnSpectrum.getPeakList().removeAll(toRemove);
        expMSnSpectrum.getPeakList().addAll(deconvoluted);

    }

    /**
     * This method finds the maximum precursor charge from all possible
     * charges..
     *
     * @return
     */
    private int getMaxPrecursorCharge() {
        // get PM charge value!
        ArrayList<Charge> possibleCharges = expMSnSpectrum.getPrecursor().getPossibleCharges();
        int maxCharge = 0;
        for (Charge ch : possibleCharges) {
            if (maxCharge < ch.value) {
                maxCharge = ch.value;
            }
        }
        return maxCharge;
    }

    /**
     * Returns the mass of a precursor ion according to the maximum precursor
     * charge value
     *
     * @return the mass of the compound with the given charge
     */
    public double getPrecurMass() {
        int maxPrecursorCharge = getMaxPrecursorCharge();
        double precursorMZ = expMSnSpectrum.getPrecursor().getMz();
        double multipliedMZ = precursorMZ * maxPrecursorCharge,
                theoProton = ElementaryIon.proton.getTheoreticMass(),
                multipliedProton = maxPrecursorCharge * theoProton,
                mass = multipliedMZ - multipliedProton;
        return mass;
    }

    /**
     * This method returns a precursor m/z value for given charge of a given
     * precursorMass
     *
     * @param precursorMass
     * @param charge
     * @return
     */
    public double getMZ(double precursorMass, int charge) {
        double mz = (precursorMass + (ElementaryIon.proton.getTheoreticMass() * charge)) / charge;
        return mz;
    }

    /**
     * This method calculates mz value with otherCharge state for the given
     * tmp_mz value
     *
     * @param tmp_mz
     * @param charge
     * @param otherCharge
     * @return
     */
    private double getMZIncreasedCharge(double tmp_mz, int charge, int otherCharge) {
        double nextMZ = (((charge * (tmp_mz - ElementaryIon.proton.getTheoreticMass())) / otherCharge) + ElementaryIon.proton.getTheoreticMass());
        return nextMZ;
    }

    private Peak find(double tmp_mz_increased_charge) {
        Peak foundPeak = null;
        Iterator<Peak> peakIterator = expMSnSpectrum.getPeakList().iterator();
        while (peakIterator.hasNext()) {
            Peak tmpPeak = peakIterator.next();
            double tmp_mz = tmpPeak.getMz();
            double diff = Math.abs(tmp_mz - tmp_mz_increased_charge);
            if (diff < 1) {
                System.out.println(tmp_mz + "\t" + tmp_mz_increased_charge + "\t" + "Find..." + diff);
            }
            if (diff < 0.00000001) {
                foundPeak = tmpPeak;
            }
        }
        System.out.print("\n");
        return foundPeak;
    }

}
