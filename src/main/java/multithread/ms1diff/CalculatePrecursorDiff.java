/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.ms1diff;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import start.CalculateMS1Err;
import start.SpectrumInfo;

/**
 * A class to calculate MS1Err differences via multithreading...
 * Note: Not working as fast as expected..
 * 
 * @author Sule
 */
public class CalculatePrecursorDiff implements Callable<DiffInfo> {

    private Double theoMass; // Theoretical mass from cross linked peptide
    private ArrayList<SpectrumInfo> specAndInfo; // All spectra and their precursor mass information...
    private boolean isPPM; // PPM based precursor mass difference: true/in Dalton :false
    private double ms1Err;
    private String line;

    public CalculatePrecursorDiff(Double theoMass, ArrayList<SpectrumInfo> specAndInfo, boolean isPPM, double ms1Err, String line) {
        this.theoMass = theoMass;
        this.specAndInfo = specAndInfo;
        this.isPPM = isPPM;
        this.ms1Err = ms1Err;
        this.line = line;
    }

    @Override
    public DiffInfo call() throws Exception {
        ArrayList<MSnSpectrum> results = new ArrayList<MSnSpectrum>();
        InnerIteratorSync<SpectrumInfo> iteratorSpectrumInfos = new InnerIteratorSync(specAndInfo.iterator());
        while (iteratorSpectrumInfos.iter.hasNext()) {
            SpectrumInfo tmpSpecInfo = (SpectrumInfo) iteratorSpectrumInfos.iter.next();
            synchronized (tmpSpecInfo) {
                // now calculate precursor mass difference...
                double tmpPrecMass = tmpSpecInfo.getPrecursorMass();
                    double tmpDiff = CalculateMS1Err.getMS1Err(isPPM, theoMass, tmpPrecMass);
                    if (tmpDiff <= ms1Err) {
                        results.add(tmpSpecInfo.getMS());
                    }
            }
        }
        DiffInfo res = new DiffInfo(results, line);
        return res;
    }

    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
    private class InnerIteratorSync<T> {

        private Iterator<T> iter = null;

        public InnerIteratorSync(Iterator<T> aIterator) {
            iter = aIterator;
        }

        public synchronized T next() {
            T result = null;
            if (iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }
    }

}
