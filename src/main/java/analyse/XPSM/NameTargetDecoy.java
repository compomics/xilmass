/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM;

import analyse.xwalk_uniprot.TrueLinking;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class select best scored XPSM for one spectrum. If analysis_option is
 * set to 1, first it merges two input files and then select the tops ones It
 * also checks true cross linking option based on predicted cross linkings
 *
 * TODO: Think of what will happen if there are more than one PSM with the same
 * maximum score, both of these need to be selected
 *
 *
 * @author Sule
 */
public class NameTargetDecoy {

    private File input, // labeled
            input2, // noLabeled
            output, // result file
            prediction_file; // a file which predicted linking locations are
    private int proteinA_index, // linked proteinA index
            proteinB_index,// linked proteinA index
            spectrum_name_index,// index of spectrum name
            score_index, // index of XPSM score
            theoretical_peaks_index, // index of theoretical peaks 
            analysis_option, // 0-select best from one given file 1-merge both files and select the top scored PSMs for one spectrum
            linkedIndexB,
            linkedIndexA;
    private String[] protein_names;
    private HashSet<TrueLinking> trueLinkings = new HashSet<TrueLinking>(); // a list of predicted cross linkings
    boolean hasTraditionalDecoy = false,
            isFirstInputLabeled;

    public NameTargetDecoy(File input, File input2, File output, File predicted_linking_file,
            int first_protein_index, int second_protein_index, int spectrum_name_index, int score_index, int theoretical_peaks_index, int analysis_option,
            int linkedIndexA, int linkedIndexB,
            String[] protein_names, boolean isFirstLabeled) {
        this.input = input;
        this.input2 = input2;
        this.output = output;
        this.proteinA_index = first_protein_index;
        this.proteinB_index = second_protein_index;
        this.spectrum_name_index = spectrum_name_index;
        this.score_index = score_index;
        this.theoretical_peaks_index = theoretical_peaks_index;
        this.analysis_option = analysis_option;
        this.protein_names = protein_names;
        this.prediction_file = predicted_linking_file;
        this.linkedIndexA = linkedIndexA;
        this.linkedIndexB = linkedIndexB;
    }

    public void run() throws IOException {
        prepareTrueLinkings();
        if (analysis_option == 0) {
            // there is only one file (indeed the first file), so analysis this
            select_top_scored(input);
        } else if (analysis_option == 1) {
            // there are two files, so first merge these and then analysis
            merge_select_top_scored();
        }
    }

    /**
     * This method reads two given files (results from labelling and results
     * from non-labelling). Then, it finds the best scores XPSMs for one
     * spectrum.
     *
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void merge_select_top_scored() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, Double> spectrum_and_score = new HashMap<String, Double>();
        HashMap<String, String> spectrum_and_line = new HashMap<String, String>();
        // read first input one and fill hashset
        readFile_fillHashSets(input, spectrum_and_score, spectrum_and_line, false, bw, isFirstInputLabeled);
        // read second input and fill hashset -
        readFile_fillHashSets(input2, spectrum_and_score, spectrum_and_line, true, bw, !isFirstInputLabeled);

        // now write up..
        for (String name : spectrum_and_line.keySet()) {
            String[] split = spectrum_and_line.get(name).split("\t");
            String proteinAInfo = split[proteinA_index],
                    proteinBInfo = split[proteinB_index],
                    proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                    proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                    targetType = getTargetType(proteinAName, proteinBName, protein_names, hasTraditionalDecoy),
                    indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                    indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")"));
            int tmplinkedIndexA = Integer.parseInt(split[linkedIndexA]),
                    tmplinkedIndexB = Integer.parseInt(split[linkedIndexB]),
                    startIndexProteinA = Integer.parseInt(proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf("-"))),
                    startIndexProteinB = Integer.parseInt(proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf("-")));
            // this retruns "Predicted" or "NotPredicted"
            String trueCrossLinking = assetTrueLinking(proteinAName, proteinBName, startIndexProteinA, startIndexProteinB, tmplinkedIndexA, tmplinkedIndexB, trueLinkings);
            bw.write(spectrum_and_line.get(name) + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\t" + trueCrossLinking + "\n");
        }
        bw.close();
    }

    private String assetTrueLinking(String proteinAInfo, String proteinBInfo, int indexProteinA, int indexProteinB, int tmplinkedIndexA, int tmplinkedIndexB, HashSet<TrueLinking> trueLinkings) {
        int tmpA = indexProteinA + tmplinkedIndexA - 1,
                tmpB = indexProteinB + tmplinkedIndexB - 1;
        String res = "Not-predicted" + "\t" + "-";
        for (TrueLinking tl : trueLinkings) {
            if (tl.getProteinA().equals(proteinAInfo)
                    && tl.getProteinB().equals(proteinBInfo)
                    && tl.getIndexA() == tmpA
                    && tl.getIndexB() == tmpB) {
                res = tl.getClassification() + "\t" + tl.getEuclidean_distance_alpha() + "\t" + tl.getEuclidean_distance_beta();
            }
        }
        return res;
    }

    private void readFile_fillHashSets(File input,
            HashMap<String, Double> spectrum_and_score, HashMap<String, String> spectrum_and_line,
            boolean isTitleWritten, BufferedWriter bw,
            boolean isInputLabeled) throws NumberFormatException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(input));
        String line = "",
                labeled = "heavy";
        if (!isInputLabeled) {
            labeled = "light";
        }
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
                        String info = line + "\t" + labeled;
                        spectrum_and_line.put(specName, info);
                        spectrum_and_score.put(specName, tmpScore);
                    }
                } else if (!spectrum_and_score.containsKey(specName)) {
                    String info = line + "\t" + labeled;
                    spectrum_and_line.put(specName, info);
                    spectrum_and_score.put(specName, tmpScore);
                }
            } else if (!isTitleWritten) {
                bw.write(line + "\t" + "Labeled" + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\t" + "Predicted" + "\t" + "Euclidean_distance(A)" + "\n");
            }
        }
    }

    /**
     * This method checks if a given pair of protein is either target or decoy
     * or half-decoy
     *
     * @param proteinAName
     * @param proteinBName
     * @param protein_names
     * @param hasTraditionalDecoy
     * @return
     */
    private String getTargetType(String proteinAName, String proteinBName, String[] protein_names, boolean hasTraditionalDecoy) {
        String first_protein_name = protein_names[0],
                second_protein_name = protein_names[1];
        String type = "";
        if (hasTraditionalDecoy) {
            type = "half-decoy";
            if ((!proteinAName.contains("decoy")) && (!proteinBName.contains("decoy"))) {
                type = "target";
            } else if ((!proteinAName.contains("decoy")) && (proteinBName.contains("decoy"))) {
                type = "td";
            } else if ((proteinAName.contains("decoy")) && (!proteinBName.contains("decoy"))) {
                type = "td";
            } else if ((proteinAName.contains("decoy")) && (proteinBName.contains("decoy"))) {
                type = "decoy";
            }
        } else {
            type = "half-decoy";
            if ((proteinAName.equals(first_protein_name) || proteinAName.equals(second_protein_name))
                    && (proteinBName.equals(first_protein_name) || proteinBName.equals(second_protein_name))) {
                type = "target";
            }
            if ((!proteinAName.equals(first_protein_name) && !proteinAName.equals(second_protein_name))
                    && (!proteinBName.equals(first_protein_name) && !proteinBName.equals(second_protein_name))) {
                type = "decoy";
            }
        }
        return type;
    }

    // TODO: Run Labeled one as welll
    private void select_top_scored(File input) throws FileNotFoundException, IOException {
        String line = "",
                labeled = "heavy";
        if (!isFirstInputLabeled) {
            labeled = "light";
        }
        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, Double> spectrum_and_score = new HashMap<String, Double>();
        HashMap<String, String> spectrum_and_line = new HashMap<String, String>();
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spect")) {
                String[] split = line.split("\t");
                String specName = split[spectrum_name_index];
                double score = Double.parseDouble(split[score_index]);
                String proteinAInfo = split[proteinA_index],
                        proteinBInfo = split[proteinB_index],
                        proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                        proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                        targetType = getTargetType(proteinAName, proteinBName, protein_names, hasTraditionalDecoy),
                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")"));
                int tmplinkedIndexA = Integer.parseInt(split[linkedIndexA]),
                        tmplinkedIndexB = Integer.parseInt(split[linkedIndexB]),
                        startIndexProteinA = Integer.parseInt(proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf("-"))),
                        startIndexProteinB = Integer.parseInt(proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf("-")));
                String trueCrossLinking = assetTrueLinking(proteinAName, proteinBName, startIndexProteinA, startIndexProteinB, tmplinkedIndexA, tmplinkedIndexB, trueLinkings);

                if (spectrum_and_score.containsKey(specName)) {
                    double tmpScore = spectrum_and_score.get(specName);
                    if (tmpScore < score) {
                        spectrum_and_line.remove(specName);
                        spectrum_and_score.remove(specName);
                        String info = line + "\t" + labeled + "\t" + trueCrossLinking;
                        spectrum_and_line.put(specName, info);
                        spectrum_and_score.put(specName, score);
                    }
                } else {
                    String info = line + "\t" + labeled;
                    spectrum_and_line.put(specName, info);
                    spectrum_and_score.put(specName, score);
                }
            } else {
                bw.write(line + "\t" + "Labeled" + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\t" + "Predicted" + "\t" + "Euclidean_distance(A)" + "\n");
            }
        }
        // now write up..
        for (String name : spectrum_and_line.keySet()) {
            String[] split = spectrum_and_line.get(name).split("\t");
            String proteinAInfo = split[proteinA_index],
                    proteinBInfo = split[proteinB_index],
                    proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                    proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                    indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                    indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")")),
                    targetType = getTargetType(proteinAName, proteinBName, protein_names, hasTraditionalDecoy);

            bw.write(spectrum_and_line.get(name) + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\n");
        }
        bw.close();
    }

    /**
     * This method reads a given prediction file and fills a hashset of
     * TrueLinking objects
     *
     *
     * @throws IOException
     */
    private void prepareTrueLinkings() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(prediction_file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("PDB")) {
//                2F3Y.pdb	LYS-13-A-CB	8	20.9	LYS-21-A-CB	14.2	12.3	POSSIBLE	P62158	14	P62158	22
                String[] split = line.split("\t");
                String structure = split[0],
                        proteinA = split[1],
                        idDistance = split[2],
                        sasDistanceStr = split[3],
                        proteinB = split[4],
                        betaMeasuredDistanceStr = split[5],
                        alphaMeasuredDistanceStr = split[6],
                        classification = split[7],
                        uniprotAcc1 = split[8],
                        uniprotAcc1Index = split[9],
                        uniprotAcc2 = split[10],
                        uniprotAcc2Index = split[11];

                int indexA = Integer.parseInt(uniprotAcc1Index),
                        indexB = Integer.parseInt(uniprotAcc2Index);
                double betaMeasuredDistance = Double.parseDouble(betaMeasuredDistanceStr),
                        alphaMeasuredDistance = Double.parseDouble(alphaMeasuredDistanceStr);
                TrueLinking tl = new TrueLinking(uniprotAcc1, uniprotAcc2, classification, indexA, indexB, alphaMeasuredDistance, betaMeasuredDistance);
                trueLinkings.add(tl);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File input = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/xilmass_testing/run/dss_hcdElit_NoLabel_Intra_TheoMSAmanda.txt"),
                output = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/xilmass_testing/run/td_dss_hcdElit_AllBoth_WeightedTheoMSAmanda.txt"),
                //        File input = new File("C:/Users/Sule/Desktop/db100/result_Nolabeled_td_target_2Pfus_9.fasta.txt"), // NonLabel
                input2 = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/xilmass_testing/run/dss_hcdElit_Label_Intra_TheoMSAmanda.txt"),
                //                output = new File("C:/Users/Sule/Desktop/td_all_2Pfus_9_dss_hcdElit_Both_TheoMSAmandaWeighted.txt"),
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt");

        int first_protein_index = 10,
                second_protein_index = 11,
                spectrum_name_index = 2,
                score_index = 9,
                theoretical_peaks_index = 21,
                linkingA = 16,
                linkingB = 17,
                analysis = 1; // merge two files...

        String[] protein_names = {"Q15149", "P62158"};
        boolean hasTraditionalDecoy = false;
        NameTargetDecoy obj = new NameTargetDecoy(input, input2, output, prediction,
                first_protein_index, second_protein_index, spectrum_name_index, score_index, theoretical_peaks_index,
                analysis, linkingA, linkingB,
                protein_names, false);
        obj.run();
    }

    /**
     * This method finds a2/b2 pairs for each cross linked peptide
     *
     * @param input
     * @param output
     * @param first_protein_index
     * @param second_protein_index
     * @param first_protein_name
     * @param second_protein_name
     * @param spectrum_name_index
     * @param score_index
     * @param hasTraditionalDecoy
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void get_paired_info(File input, File output,
            int first_protein_index, int second_protein_index,
            String[] protein_names,
            int spectrum_name_index, int score_index, boolean hasTraditionalDecoy,
            int theoretical_peaks_index) throws FileNotFoundException, IOException {

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
                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")")),
                        targetType = getTargetType(proteinAName, proteinBName, protein_names, hasTraditionalDecoy),
                        theoreticalPeaks = split[theoretical_peaks_index];
                boolean has_pair_proteinA = search_pairs(theoreticalPeaks, true),
                        has_pair_proteinB = search_pairs(theoreticalPeaks, false);

                bw.write(line + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\t" + has_pair_proteinA + "\t" + has_pair_proteinB + "\n");

            } else {
                bw.write(line + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\t" + "hasPairProteinA" + "\t" + "hasPairProteinB" + "\n");
            }
        }
        bw.close();
    }

    /**
     * This method decides if there is a2/b2 pair on given theoretical peak from
     * either proteinA (isProteinA=true) or proteinB (isProteinA=false)
     *
     * @param theoreticalPeaks
     * @param isProteinA
     * @return
     */
    private static boolean search_pairs(String theoreticalPeaks, boolean isProteinA) {
        boolean hasPairs = false;
        int a2 = 0,
                b2 = 0;
        String[] split = theoreticalPeaks.split(" ");
        for (String st : split) {
            String[] st_split = st.split("_");

            for (int i = 0; i < st_split.length - 1; i++) {
                if (st_split[i].equals("pepA") && st_split[i + 1].equals("a2") && isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepA") && st_split[i + 1].equals("b2") && isProteinA) {
                    b2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("a2") && !isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("b2") && !isProteinA) {
                    b2++;
                }
            }
        }
        if (a2 > 0 && b2 > 0) {
            hasPairs = true;
        }
        return hasPairs;
    }

    private class Result {

        private String proteinAInfo,
                proteinBInfo,
                proteinAName,
                proteinBName,
                indexProteinA,
                indexProteinB,
                targetType,
                labelInfo,
                trueCrossLinking;

        /**
         *
         * @param proteinAInfo
         * @param proteinBInfo
         * @param proteinAName protein A name
         * @param proteinBName protein B name
         * @param indexProteinA index on protein A
         * @param indexProteinB index on protein B
         * @param targetType target/decoy/half-decoy
         * @param labelInfo -light/heavy
         * @param trueCrossLinking - predicted/not predicted
         */
        public Result(String proteinAInfo, String proteinBInfo, String proteinAName, String proteinBName,
                String indexProteinA, String indexProteinB,
                String targetType, String labelInfo, String trueCrossLinking) {
            this.proteinAInfo = proteinAInfo;
            this.proteinBInfo = proteinBInfo;
            this.proteinAName = proteinAName;
            this.proteinBName = proteinBName;
            this.indexProteinA = indexProteinA;
            this.indexProteinB = indexProteinB;
            this.targetType = targetType;
            this.labelInfo = labelInfo;
            this.trueCrossLinking = trueCrossLinking;
        }

        public String getProteinAInfo() {
            return proteinAInfo;
        }

        public void setProteinAInfo(String proteinAInfo) {
            this.proteinAInfo = proteinAInfo;
        }

        public String getProteinBInfo() {
            return proteinBInfo;
        }

        public void setProteinBInfo(String proteinBInfo) {
            this.proteinBInfo = proteinBInfo;
        }

        public String getProteinAName() {
            return proteinAName;
        }

        public void setProteinAName(String proteinAName) {
            this.proteinAName = proteinAName;
        }

        public String getProteinBName() {
            return proteinBName;
        }

        public void setProteinBName(String proteinBName) {
            this.proteinBName = proteinBName;
        }

        public String getIndexProteinA() {
            return indexProteinA;
        }

        public void setIndexProteinA(String indexProteinA) {
            this.indexProteinA = indexProteinA;
        }

        public String getIndexProteinB() {
            return indexProteinB;
        }

        public void setIndexProteinB(String indexProteinB) {
            this.indexProteinB = indexProteinB;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getLabelInfo() {
            return labelInfo;
        }

        public void setLabelInfo(String labelInfo) {
            this.labelInfo = labelInfo;
        }

        public String getTrueCrossLinking() {
            return trueCrossLinking;
        }

        public void setTrueCrossLinking(String trueCrossLinking) {
            this.trueCrossLinking = trueCrossLinking;
        }

    }
}
