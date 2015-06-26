/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.shared.retrieving;

import analyse.shared.Information;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * pLink XPSMs:
 * No1_ALPHA AND No1_BETA sequences are retrieved.
 * Then their e-value scores are ranked to get FDR! 
 * 
 * 
 * @author Sule
 */
public class RetrievePLink extends RetrieveValidatedList {

    public RetrievePLink(File input) throws IOException {
        super(input);
    }

    @Override
    public ArrayList<Information> getRetrievedInfo() throws FileNotFoundException, IOException {
        if (!isRetrievedInfoReady) {
            isRetrievedInfoReady=true;
            BufferedReader br = new BufferedReader(new FileReader(input));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("SpectrumFile")) {
                    String[] split = line.split("\t");
                    String fileName = split[0],
                            spectrumTitle = split[1],
                            proteinA = split[14],
                            peptideA = split[17],
                            modsOnPeptideA = split[15],
                            linkAStr = split[18],
                            proteinB = split[19],
                            peptideB = split[22],
                            linkBStr = split[23],
                            modsOnPeptideB = split[20],
                            label = split[13],
                            td = split[24],
                            predicted = split[25],
                            euclideanAlphaStr = split[26],
                            euclideanBetaStr = split[27];
                    String scanNumberStr = spectrumTitle.substring(spectrumTitle.indexOf("scans:") + 6).replace(" ", "");
                    // pLink e-value!
                    double evaluescore = Double.parseDouble(split[7]),
                            score = Double.parseDouble(split[6]);
                    // check if there is oxidation...
                    String modA = "";
                    if (modsOnPeptideA.contains("Oxidation_M")) {
                        modA = "oxidation of m";
                    }
                    String modB = "";
                    if (modsOnPeptideB.contains("Oxidation_M")) {
                        modB = "oxidation of m";
                    }
                    Information i = new Information("pLink", fileName, scanNumberStr,
                            proteinA, peptideA, modA, linkAStr,
                            proteinB, peptideB, modB, linkBStr,
                            label, td,
                            predicted, euclideanAlphaStr, euclideanBetaStr, evaluescore);
                    retrievedInfo.add(i);
                }
            }
        }
        return retrievedInfo;
    }

    @Override
    public HashSet<Information>getValidateds(int numDecoy) throws IOException {
        int foundDecoy = 0;
        HashSet<Information> validated = new HashSet<Information>();
        // e-value sorting must be other way around! in ascending way...
        Collections.sort(getRetrievedInfo(), Information.ScoreASC);
        for (Information i : retrievedInfo) {
            if (!i.getTd().replace(" ", "").equals("target")) {
                foundDecoy++;
            }
            if (foundDecoy <= numDecoy) {
                validated.add(i);
            }
        }
        return validated;
    }

    /**
     * Get a list of validated information at FDR (<1)
     *
     * @param fdr
     * @return
     * @throws IOException
     */
    @Override
    public ArrayList<Information> getValidateds(double fdr, boolean isTraditionalFDR) throws IOException {
        int foundHalfDecoy = 0,
                foundFullDecoy = 0,
                foundTarget = 0;
        boolean isFDRreached = false;
        double tmp_fdr = 0;
        ArrayList<Information> validated = new ArrayList<Information>();
        Collections.sort(getRetrievedInfo(), Information.ScoreASC);
        for (int rank = 0; rank < retrievedInfo.size(); rank++) {
            boolean isTargetFound = false;
            Information i = retrievedInfo.get(rank);
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
                rankAnDfdr.put(rank, tmp_fdr);
            } else if (!isTraditionalFDR && !isTargetFound) {
                tmp_fdr = (double) (foundHalfDecoy - foundFullDecoy) / (double) foundTarget;
                rankAnDfdr.put(rank, tmp_fdr);
            }
            if (tmp_fdr <= fdr && i.getTd().replace(" ", "").equals("target")) {
                validated.add(i);
            }
            if (tmp_fdr > fdr && !isFDRreached) {
                isFDRreached = true;
                rankAnDfdr.put(rank, tmp_fdr);
            }
        }
        return validated;
    }

    
}
