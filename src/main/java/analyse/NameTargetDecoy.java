/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse;

import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class NameTargetDecoy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\xilmass_testing/2pfu/dss_2pfu_hcd_elit_NoLabel_DSS_Both_Andromeda_attaching_hcd_common_mw100.txt"),
        //        output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\xilmass_testing/2pfu/dss_2pfu_hcd_elit_NoLabel_DSS_Both_Andromeda_attaching_hcd_common_mw100_td.txt");
        File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\xilmass_testing/4pfu/dss_hcd_elit_NoLabel_DSS_Both_andromeda_attaching_hcd_common.txt"),
                input2 = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\xilmass_testing/4pfu/dss_4pfu_hcd_elit_Labeled_DSS_Both_Andromeda_attaching_hcd_common_mw100.txt"),
                output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\xilmass_testing/4pfu/dss_4pfu_hcd_elit_L_NL_Both_Andromeda_attaching_hcd_common_mw100_td.txt");

        int first_protein_index = 10,
                second_protein_index = 11,
                spectrum_name_index = 2,
                score_index = 9;
        String first_protein_name = "Q15149",
                second_protein_name = "P62158";

        merge_select_top_scored(input, input2, output, first_protein_index, second_protein_index, first_protein_name, second_protein_name, spectrum_name_index, score_index);
//        select_scores(input, output, first_protein_index, second_protein_index, first_protein_name, second_protein_name, spectrum_name_index, score_index);

    }

    private static void select_top_scored(File input, File output,
            int first_protein_index, int second_protein_index,
            String first_protein_name, String second_protein_name,
            int spectrum_name_index, int score_index) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(input));
        String line = "";
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        HashMap<String, Double> spectrum_and_score = new HashMap<String, Double>();
        HashMap<String, String> spectrum_and_line = new HashMap<String, String>();

        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spect")) {
                String[] split = line.split("\t");
                String specName = split[spectrum_name_index];
                double score = Double.parseDouble(split[score_index]);
                if (spectrum_and_score.containsKey(specName)) {
                    double tmpScore = spectrum_and_score.get(specName);
                    if (tmpScore < score) {
                        spectrum_and_line.remove(specName);
                        spectrum_and_score.remove(specName);
                        spectrum_and_line.put(specName, line);
                        spectrum_and_score.put(specName, score);
                    }
                } else {
                    spectrum_and_line.put(specName, line);
                    spectrum_and_score.put(specName, score);
                }
            } else {
                bw.write(line + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\n");
            }
        }
        // now write up..
        for (String name : spectrum_and_line.keySet()) {
            String[] split = spectrum_and_line.get(name).split("\t");
            String proteinAInfo = split[first_protein_index],
                    proteinBInfo = split[second_protein_index],
                    proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                    proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                    indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("("), proteinAInfo.indexOf(")")),
                    indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("("), proteinBInfo.indexOf(")")),
                    targetType = getTargetType(proteinAName, proteinBName, first_protein_name, second_protein_name);

            bw.write(spectrum_and_line.get(name) + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\n");
        }
        bw.close();
    }

    private static void select_scores(File input, File output,
            int first_protein_index, int second_protein_index,
            String first_protein_name, String second_protein_name,
            int spectrum_name_index, int score_index) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(input));
        String line = "";
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spect")) {
                String[] split = line.split("\t");

                String proteinAInfo = split[first_protein_index],
                        proteinBInfo = split[second_protein_index],
                        proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                        proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("("), proteinAInfo.indexOf(")")),
                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("("), proteinBInfo.indexOf(")")),
                        targetType = getTargetType(proteinAName, proteinBName, first_protein_name, second_protein_name);

                bw.write(line + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\n");

            } else {
                bw.write(line + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\n");
            }
        }
        bw.close();
    }

    private static void merge_select_top_scored(File input, File input2, File output,
            int first_protein_index, int second_protein_index,
            String first_protein_name, String second_protein_name,
            int spectrum_name_index, int score_index) throws FileNotFoundException, IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, Double> spectrum_and_score = new HashMap<String, Double>();
        HashMap<String, String> spectrum_and_line = new HashMap<String, String>();
        // read first input...
        BufferedReader br = new BufferedReader(new FileReader(input));
        // read first input one and fill hashset
        readFile_fillHashSets(br, spectrum_name_index, score_index, spectrum_and_score, spectrum_and_line, false, bw);
        // read second input and fill hashset
        BufferedReader br2 = new BufferedReader(new FileReader(input2));
        readFile_fillHashSets(br2, spectrum_name_index, score_index, spectrum_and_score, spectrum_and_line, true, bw);

        // now write up..
        for (String name : spectrum_and_line.keySet()) {
            String[] split = spectrum_and_line.get(name).split("\t");
            String proteinAInfo = split[first_protein_index],
                    proteinBInfo = split[second_protein_index],
                    proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                    proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                    indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("("), proteinAInfo.indexOf(")")),
                    indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("("), proteinBInfo.indexOf(")")),
                    targetType = getTargetType(proteinAName, proteinBName, first_protein_name, second_protein_name);

            bw.write(spectrum_and_line.get(name) + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\n");
        }
        bw.close();
    }

    private static void readFile_fillHashSets(BufferedReader br, int spectrum_name_index, int score_index, HashMap<String, Double> spectrum_and_score, HashMap<String, String> spectrum_and_line, boolean isTitleWritten, BufferedWriter bw) throws NumberFormatException, IOException {
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spect")) {
                String[] split = line.split("\t");
                String specName = split[spectrum_name_index];
                double tmpScore = Double.parseDouble(split[score_index]);
                if (spectrum_and_score.containsKey(specName)) {
                    double storedScore = spectrum_and_score.get(specName);
                    if (storedScore < tmpScore) {
                        spectrum_and_line.remove(specName);
                        spectrum_and_score.remove(specName);
                        spectrum_and_line.put(specName, line);
                        spectrum_and_score.put(specName, tmpScore);
                    }
                } else {
                    spectrum_and_line.put(specName, line);
                    spectrum_and_score.put(specName, tmpScore);
                }
            } else if (!isTitleWritten) {
                bw.write(line + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\n");
                isTitleWritten = true;
            }
        }
    }

    private static String getTargetType(String proteinAName, String proteinBName, String first_protein_name, String second_protein_name) {
        String type = "uncertain";
        if ((proteinAName.equals(first_protein_name) || proteinAName.equals(second_protein_name))
                && (proteinBName.equals(first_protein_name) || proteinBName.equals(second_protein_name))) {
            type = "target";
        }
        if ((!proteinAName.equals(first_protein_name) && !proteinAName.equals(second_protein_name))
                && (!proteinBName.equals(first_protein_name) && !proteinBName.equals(second_protein_name))) {
            type = "decoy";
        }
        return type;
    }

}
