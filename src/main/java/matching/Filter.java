/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matching;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class selects N peaks with highest intensity per each defined window
 * size on an MSnSpectrum.
 *
 * @author Sule
 */
public class Filter {

    private MSnSpectrum expMSnSpectrum;
    private ArrayList<Peak> filteredCPeaks = new ArrayList<Peak>();
    private int topN;
    private double windowSize = 100;

    /**
     * This constructs an object to filter out spectra based on 100Da window and
     * it selects X peaks with highest intensities for each window.
     *
     * @param expMSnSpectrum is an experimental spectrum
     * @param topN is picked peak numbers with highest intensities
     */
    public Filter(MSnSpectrum expMSnSpectrum, int topN) {
        this.expMSnSpectrum = expMSnSpectrum;
        this.topN = topN;
    }

    /**
     * This constructs an object with a given window size instead of a default
     * value.
     *
     * The default window size is 100Da
     *
     * @param expMSnSpectrum is an experimental spectrum
     * @param topN is picked peak numbers with highest intensities
     * @param windowSize size of window, based on this a given spectrum is
     * divided into smaller parts.
     *
     */
    public Filter(MSnSpectrum expMSnSpectrum, int topN, double windowSize) {
        this.expMSnSpectrum = expMSnSpectrum;
        this.topN = topN;
        this.windowSize = windowSize;
    }

    private void process() {
        double startMz = expMSnSpectrum.getOrderedMzValues()[0],
                limitMz = startMz + windowSize;
        ArrayList<Peak> cPeaks = new ArrayList<Peak>();
        for (int index_exp = 0; index_exp < expMSnSpectrum.getOrderedMzValues().length; index_exp++) {
            double tmpMZ = expMSnSpectrum.getOrderedMzValues()[index_exp];
            Peak tmpPeak = expMSnSpectrum.getPeakMap().get(tmpMZ);
            if (tmpMZ < limitMz) {
                cPeaks.add(tmpPeak);
            } else {
                Collections.sort(cPeaks, Peak.DESC_intensity_order);
                int tmp_num = topN;
                if (topN > cPeaks.size()) {
                    tmp_num = cPeaks.size();
                }
                for (int num = 0; num < tmp_num; num++) {
                    Peak tmpCPeakToAdd = cPeaks.get(num);
                    filteredCPeaks.add(tmpCPeakToAdd);
                }
                cPeaks.clear();
                limitMz = limitMz + 100;
                index_exp = index_exp - 1;
            }
        }
        if (!cPeaks.isEmpty()) {
            Collections.sort(cPeaks, Peak.DESC_intensity_order);
            int tmp_num = topN;
            if (topN > cPeaks.size()) {
                tmp_num = cPeaks.size();
            }
            for (int num = 0; num < tmp_num; num++) {
                Peak tmpCPeakToAdd = cPeaks.get(num);
                filteredCPeaks.add(tmpCPeakToAdd);
            }
        }
    }

    public MSnSpectrum getExpMSnSpectrum() {
        return expMSnSpectrum;
    }

    public void setExpMSnSpectrum(MSnSpectrum expMSnSpectrum) {
        this.expMSnSpectrum = expMSnSpectrum;
    }

    public double getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(double windowSize) {
        this.windowSize = windowSize;
    }
    

    /**
     * This method returns a list of peaks which are filtered out from given
     * MSnSpectrum.
     *
     * First, it divides a spectrum into window according to a given window size
     * (default=100Da). Then, for each window it picks topN peaks ordered by in
     * DESC_intensity_order in that window. After picking such peaks, it put
     * them all on an arraylist and return it as a final
     *
     * @return
     */
    public ArrayList<Peak> getFilteredCPeaks() {
        if (filteredCPeaks.isEmpty()) {
            process();
        }
        return filteredCPeaks;
    }

    public void setFilteredCPeaks(ArrayList<Peak> filteredCPeaks) {
        this.filteredCPeaks = filteredCPeaks;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

}
