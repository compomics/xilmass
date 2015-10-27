/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.shared.retrieving;

import analyse.shared.Information;
import analyse.shared.InformationQValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public class RetrievePercolatorKojak extends RetrieveValidatedList {

    public RetrievePercolatorKojak(File input) throws IOException {
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
                            scanNumberStr = split[1],
                            scoreStr = split[2],
                            qValueStr = split[3],
                            posteriorStr = split[4],
                            proteinA = split[5],
                            proteinB = split[6],
                            peptideA = split[7],
                            peptideB = split[8],
                            linkAStr = split[9],
                            linkBStr = split[10],
                            predicted = split[11],
                            betaeuclidean = split[12],
                            alphaeuclidean = split[13];

                    double score = Double.parseDouble(scoreStr);
                    // check if there is oxidation...
                    String modA = "",
                            modB = "";
                    modB = "oxidation of m";

                    Information i = new Information("PercolatorKojak", fileName, scanNumberStr,
                            proteinA, peptideA, modA, linkAStr,
                            proteinB, peptideB, modB, linkBStr,
                            "", "target",
                            predicted, alphaeuclidean, betaeuclidean,
                            Double.parseDouble(qValueStr));
                    retrievedInfo.add(i);
                }
            }
        }
        return retrievedInfo;
    }

    public ArrayList<Information>getQValidateds(double qvalue) throws IOException {
       ArrayList<Information> res = new ArrayList<Information>();

        Collections.sort(getRetrievedInfo(), Information.ScoreASC);
        for (int rank = 0; rank < retrievedInfo.size(); rank++) {
            Information i = retrievedInfo.get(rank);

            if (i.getScore() <= qvalue) {
                res.add(i);
            }
        }
        return res;

    }

}
