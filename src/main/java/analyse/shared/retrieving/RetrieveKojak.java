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

/**
 *
 * @author Sule
 */
public class RetrieveKojak extends RetrieveValidatedList {

    public RetrieveKojak(File input) throws IOException {
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
                            scanNumberStr = split[1],
                            proteinA = split[11],
                            peptideA = split[9],
                            modifiedPeptideA = split[16],
                            linkAStr = split[10],
                            proteinB = split[14],
                            peptideB = split[12],
                            linkBStr = split[13],
                            modifiedPeptideB = split[17],
                            label = split[18],
                            td = split[19],
                            predicted = split[20],
                            euclideanAlphaStr = split[21],
                            euclideanBetaStr = split[22];
                    double score = Double.parseDouble(split[6]);
                    // check if there is oxidation...
                    String modA = "";
                    if (modifiedPeptideA.contains("168.15")) {
                        modA = "oxidation of m";
                    }
                    String modB = "";
                    if (modifiedPeptideB.contains("168.15")) {
                        modB = "oxidation of m";
                    }
                    Information i = new Information("Kojak", fileName, scanNumberStr,
                            proteinA, peptideA, modA, linkAStr,
                            proteinB, peptideB, modB, linkBStr,
                            label, td,
                            predicted, euclideanAlphaStr, euclideanBetaStr, score);
                    retrievedInfo.add(i);
                }
            }
        }
        return retrievedInfo;
    }

}
