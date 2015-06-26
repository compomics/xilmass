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
public class RetrievepLinkValidateds extends RetrieveValidatedList {

    public RetrievepLinkValidateds(File input) throws IOException {
        super(input);
    }

    @Override
    public ArrayList<Information> getRetrievedInfo() throws FileNotFoundException, IOException {
        if (!isRetrievedInfoReady) {
            isRetrievedInfoReady = true;
            BufferedReader br = new BufferedReader(new FileReader(input));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("SpectrumFile")) {
                    String[] split = line.split("\t");
                    String fileName = split[0],
                            spectrumTitle = split[1],
                            proteinA = split[9],
                            peptideA = split[10],
                            modsOnPeptideA = split[8],
                            linkAStr = split[11],
                            proteinB = split[12],
                            peptideB = split[13],
                            linkBStr = split[14],
                            modsOnPeptideB = "",
                            label = split[6],
                            td = split[15],
                            predicted = split[16],
                            euclideanAlphaStr = split[17],
                            euclideanBetaStr = split[18];

                    String[] st = split[8].split(";");
                    if (st.length == 2) {
                        modsOnPeptideA = st[0];
                        modsOnPeptideB = st[1];
                    }
                    String scanNumberStr = spectrumTitle.substring(spectrumTitle.indexOf("scans:") + 6).replace(" ", "");
                    // pLink e-value!
                    double score = Double.parseDouble(split[2]);
                    // check if there is oxidation...
                    String modA = "";
                    if (modsOnPeptideA.contains("Oxidation_M")) {
                        modA = "oxidation of m";
                    }
                    String modB = "";
                    if (modsOnPeptideB.contains("Oxidation_M")) {
                        modB = "oxidation of m";
                    }
                    Information i = new Information("plink", fileName, scanNumberStr,
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
