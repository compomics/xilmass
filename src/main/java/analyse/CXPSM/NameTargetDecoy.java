/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM;

import analyse.CXPSM.prepareOutcome.*;
import analyse.xwalk_uniprot.color30A.PyMolScriptCol;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import config.ConfigHolder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;

/**
 * This class selects the best scored XPSM for each spectrum.
 *
 * @author Sule
 */
public class NameTargetDecoy {

    /**
     *
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, IllegalArgumentException, InterruptedException, ClassNotFoundException, ConfigurationException {

        File xilmassResFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("xilmass.results")),
                prediction = null,
                // The validated PSM list from contaminants
                psms_contamination = null,
                pLinkCombValidatedFile = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.validated.file")),
                pLinkFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLink.folder")),
                pLinkAllOutput = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("pLinkalloutput")),
                percolatorKojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.kojak")),
                percolatorXilmassFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("percolator.output.folder.xilmass")),
                kojakFolder = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("kojak.folder")),
                database = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("database.file"));

        // get every protein entry on database with their accession numbers
        HashMap<String, String> accs = getAccs(database);
        String proteinA = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinA"),
                proteinB = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("proteinB"),
                scoringFunctionName = ConfigHolder.getTargetDecoyAnalyzeInstance().getString("scoringFunctionName");

        String[] protein_names = {proteinA, proteinB};
        boolean isPITFDR = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("is.PIT.FDR"),
                isMS1ErrPPM = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("is.ms1Err.ppm"),
                doesContainCPeptidePattern = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("doesContainCPeptidePattern"),
                doesContainIonWeight = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("doesContainIonWeight"),
                doesCheckLysine = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("doesCheckLysine"),
                isBasedOnManualValidation = false,
                isRequiredPostprocessing = ConfigHolder.getTargetDecoyAnalyzeInstance().getBoolean("isRequiredPostprocessing");
        double qvalue = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("qvalue"),
                fdr_cutoff = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("fdr"),
                fdr_interPro = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("fdrInterPro"),
                fdr_intraPro = ConfigHolder.getTargetDecoyAnalyzeInstance().getDouble("fdrIntraPro");
        int analysis = ConfigHolder.getTargetDecoyAnalyzeInstance().getInt("analysis"); // 1-Kojak/2-AllPLink 3-ValPLink 4-Xilmass 5-PercolatorKojak runs! 6-Percolator-Xilmass runs

        File td = null,
                allXPSMs = null;
        boolean doesSplitFDR = true;
        if (args.length > 1) {
            analysis = Integer.parseInt(args[0]);
            xilmassResFolder = new File(args[1]);
            scoringFunctionName = args[2];
            td = new File(args[3]);
            allXPSMs = new File(args[4]);
            fdr_interPro = Double.parseDouble(args[5]);
            fdr_intraPro = Double.parseDouble(args[6]);
            fdr_cutoff = Double.parseDouble(args[7]);
            doesSplitFDR = Boolean.parseBoolean(args[8]);
        } else {
            td = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("output"));
            allXPSMs = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("allXPSMOutputName"));
            // Xwalk predicted and manullay curated cross linking sites
            prediction = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("prediction"));
            psms_contamination = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("psms.contaminant"));
        }
        td.createNewFile();
        allXPSMs.createNewFile();

        // NOW RUN!!!
        AnalyzeOutcomes o = null;
        if (!isRequiredPostprocessing) {
            switch (analysis) {
                case 0:
                    o = new AnalyzeXilmass(xilmassResFolder, td, prediction, psms_contamination, fdr_cutoff, isPITFDR, isMS1ErrPPM, doesContainCPeptidePattern, doesContainIonWeight,
                            allXPSMs, scoringFunctionName);
                    break;
                case 10:
                    o = new AnalyzeXilmass(xilmassResFolder, td, prediction, psms_contamination, fdr_cutoff, isPITFDR, isMS1ErrPPM, doesContainCPeptidePattern, doesContainIonWeight,
                            fdr_interPro, fdr_intraPro, allXPSMs, scoringFunctionName);
                    break;
                case 11:
                    o = new AnalyzeXilmass(xilmassResFolder, td, psms_contamination, fdr_cutoff, doesSplitFDR, isPITFDR, isMS1ErrPPM,
                            fdr_interPro, fdr_intraPro, allXPSMs, scoringFunctionName);
                    break;
                case 1:
                    o = new AnalyzeKojak(td, kojakFolder, prediction, psms_contamination, database, protein_names, fdr_cutoff, isPITFDR);
                    break;
                case 2:
                    o = new AnalyzePLink(pLinkFolder, pLinkAllOutput, td, prediction, psms_contamination, protein_names, fdr_cutoff, isPITFDR);
                    break;
                case 3:
                    o = new AnalyzePLinkValidatedResult(pLinkCombValidatedFile, td, prediction, psms_contamination, protein_names, accs);
                    break;
                case 4:
                    o = new AnalyzePercolator(td, percolatorXilmassFolder, prediction, psms_contamination, protein_names, accs, true, qvalue, doesCheckLysine, isBasedOnManualValidation);
                    break;
                case 5:
                    o = new AnalyzePercolator(td, percolatorKojakFolder, prediction, psms_contamination, protein_names, accs, qvalue, doesCheckLysine, isBasedOnManualValidation);
                    break;
            }
            o.run();

            // now check post-processing
        } else {
            File measuredDistOuputFile = new File(ConfigHolder.getTargetDecoyAnalyzeInstance().getString("measuredDistOuputFile"));
            post_processing(td, measuredDistOuputFile);
        }
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

    public static HashMap<String, String> getAccs(File fasta) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        HashMap<String, String> acc_seq = new HashMap<String, String>();
        SequenceFactory fct = SequenceFactory.getInstance();
        fct.loadFastaFile(fasta);
        Set<String> accession_original_db = fct.getAccessions();
        for (String acc : accession_original_db) {
            String proSeq = fct.getProtein(acc).getSequence();
            acc_seq.put(acc, proSeq);
        }
        return acc_seq;
    }

    private static void post_processing(File output, File measuredDistOuputFile) throws FileNotFoundException, IOException {
        File outputWdists = new File(output.getAbsolutePath().substring(0, output.getAbsolutePath().indexOf(".txt")) + "_dist.txt");
        BufferedReader br = new BufferedReader(new FileReader(output));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputWdists));

        HashMap<String, Double> distName_and_computedDist = PyMolScriptCol.get_distName_and_computedDist(measuredDistOuputFile, true);

        String line = "",
                title = "";
        int xlinkingsiteind = 0,
                sasdistind = 0;
        boolean isFirstLine = false;
        double computedDist = 0;
        while ((line = br.readLine()) != null) {
            if (!isFirstLine) {
                isFirstLine = true;
                title = line + "\t" + "ComputedEuclideanAlpha" + "\t" + "PostProcessingName" + "\t" + "PredictedByXWalk";
                String[] sp = line.split("\t");
                for (int i = 0; i < sp.length; i++) {
                    if (sp[i].equals("crossLinkingSite")) {
                        xlinkingsiteind = i;
                    } else if (sp[i].equals("SASDist")) {
                        sasdistind = i;
                    }

                }
                bw.write(title);
                bw.newLine();
            } else {
                // first name based on distance
                String crossLinkingSites = line.split("\t")[xlinkingsiteind];
                String postprocessingname = "within";
                if (distName_and_computedDist.containsKey(crossLinkingSites)) {
                    computedDist = distName_and_computedDist.get(crossLinkingSites);
                    if (computedDist >= 30.00) {
                        postprocessingname = "exceeding";
                    } else if (computedDist == -1) {
                        postprocessingname = "missing";
                        String[] sp = crossLinkingSites.split("_");
                        int first = Integer.parseInt(sp[1]),
                                second = Integer.parseInt(sp[3]);
                        if (first == second && sp[0].equals(sp[2])) {
                            postprocessingname = "missing_intra_peptide";
                        }
                    } else if (computedDist == 0) {
                        postprocessingname = "intra_peptide";
                    }
                } else {
                    postprocessingname = "noInfo";
                    computedDist = -2;
                }
                // now check  xwalk predicted ones
                String sasPredict = "Pred";
                if (line.split("\t")[sasdistind].equals("-")) {
                    sasPredict = "UnPred";
                }
                bw.write(line + "\t" + computedDist + "\t" + postprocessingname + "\t" + sasPredict);
                bw.newLine();
            }
        }
        bw.close();
    }

}
