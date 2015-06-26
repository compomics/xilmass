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
public class RetrieveXilmass extends RetrieveValidatedList {

    public RetrieveXilmass(File input) throws IOException {
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
                            proteinA = split[9],
                            peptideA = split[11],
                            modsOnPeptideA = split[13],
                            linkOnPeptideA = split[15],
                            proteinB = split[10],
                            peptideB = split[12],
                            linkOnPeptideB = split[16],
                            modsOnPeptideB = split[14],
                            label = split[25],
                            td = split[28],
                            predicted = split[29],
                            euclideanBetaStr = split[30],
                            euclideanAlphaStr = split[31];

                    // rename label..
                    if(label.equals("FALSE")){
                        label="light";
                    }
                    if(label.equals("TRUE")){
                        label="heavy";
                    }
                    // get scan number from a spectrum title...
                    String scanNumberStr = spectrumTitle.substring(spectrumTitle.indexOf("scans:") + 6).replace(" ", "");
                    Integer startIndexProteinA = Integer.parseInt(proteinA.substring(proteinA.indexOf("(") + 1, proteinA.indexOf("-"))),
                            startIndexProteinB = Integer.parseInt(proteinB.substring(proteinB.indexOf("(") + 1, proteinB.indexOf("-"))),
                            uniprotLinkingSiteA = startIndexProteinA + Integer.parseInt(linkOnPeptideA) - 1,
                            uniprotLinkingSiteB = startIndexProteinB + Integer.parseInt(linkOnPeptideB) - 1;

                    proteinA = proteinA.substring(0, proteinA.indexOf("("));
                    proteinB = proteinB.substring(0, proteinB.indexOf("("));

                    double score = Double.parseDouble(split[8]);
                    // check if there is oxidation...
                    String modA = "";
                    if (modsOnPeptideA.contains("oxidation of m")) {
                        modA = "oxidation of m";
                    }
                    String modB = "";
                    if (modsOnPeptideB.contains("oxidation of m")) {
                        modB = "oxidation of m";
                    }
                    Information i = new Information("xilmass", fileName, scanNumberStr,
                            proteinA, peptideA, modA, uniprotLinkingSiteA.toString(),
                            proteinB, peptideB, modB, uniprotLinkingSiteB.toString(),
                            label, td,
                            predicted, euclideanAlphaStr, euclideanBetaStr, score);
                    retrievedInfo.add(i);
                }
            }
        }
        return retrievedInfo;

    }

}
