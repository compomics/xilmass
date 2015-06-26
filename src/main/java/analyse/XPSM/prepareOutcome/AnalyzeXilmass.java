/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.prepareOutcome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class merges xilmass outputs (labeled and not labeled ones)
 *
 * @author Sule
 */
public class AnalyzeXilmass extends AnalyzeOutcomes {

    private File lightLabeledFile,
            heavyLabeledFile,
            xilmassFile,
            output;
    private int indAccProA = 10,
            indAccProB = 11,
            linkedIndexA = 16,
            linkedIndexB = 17,
            spectrum_title_index = 2,
            score_index = 9;

    HashSet<String> contaminant_MSMS = new HashSet<String>();

    public AnalyzeXilmass(File lightLabeledFile, File heavyLabeledFile, File output, File prediction_file, File psms_contaminant, String[] target_names, boolean hasTraditionalDecoy) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.hasTraditionalDecoy = hasTraditionalDecoy;
        this.lightLabeledFile = lightLabeledFile;
        this.heavyLabeledFile = heavyLabeledFile;
        this.output = output;
        contaminant_MSMS = getContaminant_MSMS();
    }

    public AnalyzeXilmass(File xilmassFile, File output, File prediction_file, File psms_contaminant, String[] target_names, boolean hasTraditionalDecoy) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.hasTraditionalDecoy = hasTraditionalDecoy;
        this.xilmassFile = xilmassFile;
        this.output = output;
        contaminant_MSMS = getContaminant_MSMS();
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
        HashMap<String, ArrayList<String>> spectrum_and_line = new HashMap<String, ArrayList<String>>();
        if (xilmassFile != null) {
            fillHashSets(xilmassFile, spectrum_and_score, spectrum_and_line, bw);
        }
        // read first input one and fill hashset
        if (lightLabeledFile != null) {
            readFile_fillHashSets(lightLabeledFile, spectrum_and_score, spectrum_and_line, false, bw, false);
        }
        // read second input and fill hashset -
        if (heavyLabeledFile != null) {
            readFile_fillHashSets(heavyLabeledFile, spectrum_and_score, spectrum_and_line, true, bw, true);
        }
        // now write up..
        for (String name : spectrum_and_line.keySet()) {
            for (String line : spectrum_and_line.get(name)) {
                String[] split = line.split("\t");
                String proteinAInfo = split[indAccProA],
                        proteinBInfo = split[indAccProB],
                        proteinAAccession = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
                        proteinBAccession = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
                        targetType = getTargetType(proteinAAccession, proteinBAccession, target_names),
                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")"));
                int tmplinkedIndexA = Integer.parseInt(split[linkedIndexA]),
                        tmplinkedIndexB = Integer.parseInt(split[linkedIndexB]),
                        startIndexProteinA = Integer.parseInt(proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf("-"))),
                        startIndexProteinB = Integer.parseInt(proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf("-"))),
                        uniprotLinkingSiteA = startIndexProteinA + tmplinkedIndexA - 1,
                        uniprotLinkingSiteB = startIndexProteinB + tmplinkedIndexB - 1;
                // this retruns "Predicted" or "NotPredicted"
                String trueCrossLinking = assetTrueLinking(proteinAAccession, proteinBAccession, uniprotLinkingSiteA, uniprotLinkingSiteB);
                String info = "";
                for (int index = 1; index < split.length; index++) {
                    info += split[index] + "\t";
                }
                bw.write(info + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\t" + trueCrossLinking + "\n");
            }
        }
        bw.close();
    }

    /**
     * To merge light and heavy labeled output files
     *
     * @param input
     * @param spectrum_and_score
     * @param spectrum_and_line
     * @param isTitleWritten
     * @param bw
     * @param isInputLabeled
     * @throws NumberFormatException
     * @throws IOException
     */
    private void readFile_fillHashSets(File input,
            HashMap<String, Double> spectrum_and_score, HashMap<String, ArrayList<String>> spectrum_and_line,
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
                String specTitle = split[spectrum_title_index];
                double tmpScore = Double.parseDouble(split[score_index]);
                if (!contaminant_MSMS.contains(specTitle)) {
                    if (spectrum_and_score.containsKey(specTitle)) {
                        double storedScore = spectrum_and_score.get(specTitle);
                        if (storedScore < tmpScore) {
                            spectrum_and_line.remove(specTitle);
                            spectrum_and_score.remove(specTitle);
                            String info = line + "\t" + labeled;
                            ArrayList<String> infos = new ArrayList<String>();
                            infos.add(info);
                            spectrum_and_line.put(specTitle, infos);
                            spectrum_and_score.put(specTitle, tmpScore);
                        } else if (storedScore == tmpScore) {
                            String info = line + "\t" + labeled;
                            spectrum_and_line.get(specTitle).add(info);
                        }
                    } else if (!spectrum_and_score.containsKey(specTitle)) {
                        String info = line + "\t" + labeled;
                        ArrayList<String> infos = new ArrayList<String>();
                        infos.add(info);
                        spectrum_and_line.put(specTitle, infos);
                        spectrum_and_score.put(specTitle, tmpScore);
                    }
                }
            } else if (!isTitleWritten) {
                bw.write(line.substring(line.indexOf("SpectrumFile")) +"\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t"
                        + "Target_Decoy" + "\t" + "Predicted" + "\t" + "Euclidean_distance beta(A)" + "\t" + "Euclidean_distance alpha(A)" + "\n");
            }
        }
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        merge_select_top_scored();
    }

    private void fillHashSets(File xilmassFile, HashMap<String, Double> spectrum_and_score, HashMap<String, ArrayList<String>> spectrum_and_line, BufferedWriter bw) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(xilmassFile));
        String line = "";
        boolean isTitleWritten = false;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spect")) {
                String[] split = line.split("\t");
                String specTitle = split[spectrum_title_index];
                double tmpScore = Double.parseDouble(split[score_index]);
                if (!contaminant_MSMS.contains(specTitle)) {
                    if (spectrum_and_score.containsKey(specTitle)) {
                        double storedScore = spectrum_and_score.get(specTitle);
                        if (storedScore < tmpScore) {
                            spectrum_and_line.remove(specTitle);
                            spectrum_and_score.remove(specTitle);
                            ArrayList<String> infos = new ArrayList<String>();
                            infos.add(line);
                            spectrum_and_line.put(specTitle, infos);
                            spectrum_and_score.put(specTitle, tmpScore);
                        } else if (storedScore == tmpScore) {
                            spectrum_and_line.get(specTitle).add(line);
                        }
                    } else if (!spectrum_and_score.containsKey(specTitle)) {
                        ArrayList<String> infos = new ArrayList<String>();
                        infos.add(line);
                        spectrum_and_line.put(specTitle, infos);
                        spectrum_and_score.put(specTitle, tmpScore);
                    }
                }
            } else if (!isTitleWritten) {
                bw.write(line.substring(line.indexOf("SpectrumFile")) +  "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t"
                        + "Target_Decoy" + "\t" + "Predicted" + "\t" + "Euclidean_distance beta(A)" + "\t" + "Euclidean_distance alpha(A)" + "\n");
                isTitleWritten = true;
            }
        }
    }

}
