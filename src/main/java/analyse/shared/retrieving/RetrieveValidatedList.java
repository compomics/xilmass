/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.shared.retrieving;

import analyse.shared.Information;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public abstract class RetrieveValidatedList {

    protected File input; // an input result file
    protected ArrayList<Information> retrievedInfo; // XPSM information retrieved from a given result file
    protected boolean isRetrievedInfoReady = false; // first step is to read each XPSMs to retrieve XPSMInfo while constructing an object, a list of information called retrievedInfo being filled 
    protected HashMap<Integer, Double> rankAnDfdr = new HashMap<Integer, Double>(); //rank of decoy and corresponding FDR (based on either traditionalFDR or pLinkFDR) 

    public RetrieveValidatedList(File input) throws IOException {
        this.input = input;
        retrievedInfo = new ArrayList<Information>();
        getRetrievedInfo();
    }

    public File getInput() {
        return input;
    }

    public void setInput(File input) {
        this.input = input;
    }

    public abstract ArrayList<Information> getRetrievedInfo() throws FileNotFoundException, IOException;

    public HashSet<Information> getValidateds(int numDecoy) throws IOException {
        int foundDecoy = 0;
        HashSet<Information> validated = new HashSet<Information>();
        Collections.sort(getRetrievedInfo(), Information.ScoreDESC);
        for (Information i : retrievedInfo) {
            
            if (!i.getTd().replace(" ", "").equals("target")) {
                foundDecoy++;
            }
            if (foundDecoy < numDecoy && i.getTd().equals("target")) {
                validated.add(i);
            }
            if(foundDecoy>numDecoy){
                break;
            }
        }
        return validated;
    }

    /**
     * Get a list of validated information at FDR (<)
     *
     * @param fdr
     * @return
     * @throws IOException
     */
    public ArrayList<Information> getValidateds(double fdr, boolean isTraditionalFDR) throws IOException {
        int foundHalfDecoy = 0,
                foundFullDecoy = 0,
                foundTarget = 0;
        boolean isFDRreached = false;
        double tmp_fdr = 0;
        ArrayList<Information> validated = new ArrayList<Information>();
        Collections.sort(retrievedInfo, Information.ScoreDESC);
        for (int index = 0; index < retrievedInfo.size(); index++) {
            boolean isTargetFound = false;
            Information i = retrievedInfo.get(index);
            int rank = index + 1;
            if (i.getTd().replace(" ", "").equals("target")) {
                foundTarget++;
                isTargetFound = true;
            } else if (i.getTd().replace(" ", "").equals("half-decoy")) {
                foundHalfDecoy++;
            } else if (i.getTd().replace(" ", "").equals("decoy")) {
                foundFullDecoy++;
            }
            if (isTraditionalFDR && !isTargetFound) {
                tmp_fdr = (double) (foundFullDecoy + foundHalfDecoy) / (double) foundTarget;
                double roundedFDR = Math.floor(tmp_fdr * 1000) / 1000;
                rankAnDfdr.put(rank, roundedFDR);
            } else if (!isTraditionalFDR && !isTargetFound) {
                tmp_fdr = (double) (foundHalfDecoy - foundFullDecoy) / (double) foundTarget;
                double roundedFDR = Math.floor(tmp_fdr * 1000) / 1000;
                rankAnDfdr.put(rank, roundedFDR);
            }
            if (tmp_fdr <= fdr && i.getTd().replace(" ", "").equals("target") && !isFDRreached) {
                validated.add(i);
            }
            if (tmp_fdr > fdr && !isFDRreached) {
                isFDRreached = true;
                rankAnDfdr.put(rank, tmp_fdr);
            }
        }
        return validated;
    }

    public HashMap<Integer, Double> getRankAnDfdr(double tillFDR) {
        HashMap<Integer, Double> info = new HashMap<Integer, Double>();
        int control = 0;
        boolean isBiggerFDR = false;
        ArrayList<Integer> list = new ArrayList<Integer>(rankAnDfdr.keySet());
        Collections.sort(list);
        for (Integer rank : list) {
            double tmp_fdr = rankAnDfdr.get(rank);
            if (tmp_fdr > tillFDR) {
                if (!isBiggerFDR) {
                    // print a last FDR...
                    info.put(rank, tmp_fdr);
                }
                isBiggerFDR = true;
            }
            if (tmp_fdr <= tillFDR && !isBiggerFDR) {
                info.put(rank, tmp_fdr);
            } else if (control == 0 && !isBiggerFDR) {
                info.put(rank, tmp_fdr);
                control++;
            }
        }
        return info;
    }

}
