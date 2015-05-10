/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.ms1diff;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;

/**
 * Multithreading result to hold all information
 *
 *
 * @author Sule
 */
public class DiffInfo {

    private ArrayList<MSnSpectrum> selectedSpectra; // a list of selected MS2 spectra within certain MS1err
    private String line; // a line from index file which contains CPeptides objects and all linking information

    public DiffInfo(ArrayList<MSnSpectrum> selectedSpectra, String line) {
        this.selectedSpectra = selectedSpectra;
        this.line = line;
    }

    public ArrayList<MSnSpectrum> getSelectedSpectra() {
        return selectedSpectra;
    }

    public void setSelectedSpectra(ArrayList<MSnSpectrum> selectedSpectra) {
        this.selectedSpectra = selectedSpectra;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

}
