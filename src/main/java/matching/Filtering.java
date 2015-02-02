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
 *
 * @author Sule
 */
public class Filtering {

    private MSnSpectrum expMSnSpectrum;
    private ArrayList<Peak> finalCPeaks = new ArrayList<Peak>();
    private int numHighestPeak;

    public Filtering(MSnSpectrum expMSnSpectrum, int numHighestPeak) {
        this.expMSnSpectrum = expMSnSpectrum;
        this.numHighestPeak = numHighestPeak;
    }
    
    private void process() {
        double startMz = expMSnSpectrum.getOrderedMzValues()[0],
                limitMz = startMz + 100;
        ArrayList<Peak> cPeaks = new ArrayList<Peak>();
        for (int index_exp = 0; index_exp < expMSnSpectrum.getOrderedMzValues().length; index_exp++) {
            double tmpMZ = expMSnSpectrum.getOrderedMzValues()[index_exp];
            Peak tmpPeak = expMSnSpectrum.getPeakMap().get(tmpMZ);
            if (tmpMZ < limitMz) {
                cPeaks.add(tmpPeak);
            } else  {
                Collections.sort(cPeaks, Peak.DESC_intensity_order);
                int tmp_num = numHighestPeak;
                if (numHighestPeak > cPeaks.size()) {
                    tmp_num = cPeaks.size();
                }
                for (int num = 0; num < tmp_num; num++) {
                    Peak tmpCPeakToAdd = cPeaks.get(num);
                    finalCPeaks.add(tmpCPeakToAdd);
                }
                cPeaks.clear();
                limitMz = limitMz + 100;
                index_exp = index_exp - 1;
            }
        }
        if (!cPeaks.isEmpty()) {
            Collections.sort(cPeaks, Peak.DESC_intensity_order);
            int tmp_num = numHighestPeak;
            if (numHighestPeak > cPeaks.size()) {
                tmp_num = cPeaks.size();
            }
            for (int num = 0; num < tmp_num; num++) {
                Peak tmpCPeakToAdd = cPeaks.get(num);
                finalCPeaks.add(tmpCPeakToAdd);
            }
        }
    }

    public MSnSpectrum getExpMSnSpectrum() {
        return expMSnSpectrum;
    }

    public void setExpMSnSpectrum(MSnSpectrum expMSnSpectrum) {
        this.expMSnSpectrum = expMSnSpectrum;
    }

    public ArrayList<Peak> getFinalCPeaks() {
        if (finalCPeaks.isEmpty()) {
            process();
        }
        return finalCPeaks;
    }

    public void setFinalCPeaks(ArrayList<Peak> finalCPeaks) {
        this.finalCPeaks = finalCPeaks;
    }

    public int getNumHighestPeak() {
        return numHighestPeak;
    }

    public void setNumHighestPeak(int numHighestPeak) {
        this.numHighestPeak = numHighestPeak;
    }

}
